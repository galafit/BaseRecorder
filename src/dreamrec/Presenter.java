package dreamrec;

import com.biorecorder.basechart.ChartConfig;
import com.biorecorder.basechart.ChartPanel;
import com.biorecorder.basechart.chart.BStroke;
import com.biorecorder.basechart.chart.Range;
import com.biorecorder.basechart.chart.config.Theme;
import com.biorecorder.basechart.chart.config.traces.BooleanTraceConfig;
import com.biorecorder.basechart.chart.config.traces.LineTraceConfig;
import com.biorecorder.basechart.chart.scales.Unit;
import com.biorecorder.basechart.data.FloatSeries;
import com.biorecorder.basechart.data.GroupingType;
import com.biorecorder.basechart.data.XYData;
import data.DataSeries;
import filters.FilterBandPass_Alfa;
import filters.FilterDerivativeRem;
import filters.FilterHiPass;
import filters.HiPassCollectingFilter;
import functions.Abs;
import functions.Constant;
import functions.Minus;
import gui.MainWindow;


/**
 * Created by mac on 19/02/15.
 */
public class Presenter implements ControllerListener {
    private final double PREVIEW_TIME_FREQUENCY = 50.0 / 750;
    MainWindow mainWindow;
    ChartPanel chartPanel;

    public Presenter(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void dataStoreUpdated(Object dataStore) {
        if (dataStore instanceof RemDataStore) {
            RemDataStore remDataStore = (RemDataStore) dataStore;
            ChartConfig config = configureRemGraphViewer(remDataStore);
            chartPanel = new ChartPanel(config);
            mainWindow.setChartPanel(chartPanel);
            remDataStore.addListener(new DataStoreListener() {
                @Override
                public void onDataUpdate() {
                    chartPanel.update();
                }
            });
        }
    }

    private ChartConfig configureRemGraphViewer(RemDataStore remDataStore) {
        return rem(remDataStore);
    }

    private ChartConfig configureGraphViewer(DataStore dataStore) {
        ChartConfig config = new ChartConfig(true);
        for (int i = 0; i < dataStore.getNumberOfChannels(); i++) {
            DataSeries channel = dataStore.getChannelData(i);

            config.addTrace(new LineTraceConfig(), toChartData(channel));
        }

        if (dataStore.getNumberOfChannels() > 0) {
            DataSeries channel = dataStore.getChannelData(0);
            DataSeries velocityRem = new Abs(new FilterDerivativeRem(channel));
            config.addPreviewTrace(new LineTraceConfig(), toChartData(velocityRem));
        }
        return config;

    }

    private XYData toChartData(DataSeries dataSeries) {
        FloatSeries floatSeries = new FloatSeries() {
            double gain = dataSeries.getScaling().getDataGain();
            double offset = dataSeries.getScaling().getDataOffset();

            @Override
            public long size() {
                return dataSeries.size();
            }

            @Override
            public float get(long l) {
                return (float) (dataSeries.get((int) l) * gain + offset);
            }
        };

        XYData data = new XYData();
        data.setYData(floatSeries);
        data.setXData(dataSeries.getScaling().getStart(), dataSeries.getScaling().getSamplingInterval() * 1000);
        return data;
    }


    private ChartConfig rem(RemDataStore remDataStore) {
        ChartConfig config = new ChartConfig(Theme.DARK, true);
        config.addPreviewGroupingInterval(750 * 1000 / 50);
        config.getPreviewConfig().getXConfig(0).setTickStep(30, Unit.MINUTE);
        config.getPreviewConfig().getXConfig(0).setTickMarkWidth(3);
        config.getPreviewConfig().getXConfig(0).setMinorTickMarkWidth(1);
        config.getPreviewConfig().getXConfig(0).setMinorGridLineStroke(new BStroke(1, BStroke.DOT));
        config.getPreviewConfig().getXConfig(0).setMinorTickMarkInsideSize(2);
        config.getPreviewConfig().getXConfig(0).setMinorTickMarkOutsideSize(2);
        config.getPreviewConfig().getXConfig(0).setMinorGridCounter(3);
        config.setChartAutoScaleEnable(false);


        int eogCutOffPeriod = 10; //sec. to remove steady component (cutoff_frequency = 1/cutoff_period )
        DataSeries eog1Full = remDataStore.getEog1Data();
        DataSeries eog2Full = remDataStore.getEog2Data();
        DataSeries eog1 = new HiPassCollectingFilter(eog1Full, eogCutOffPeriod);
        DataSeries eog2 = null;
        if (eog2Full != null) {
            eog2 = new HiPassCollectingFilter(eog2Full, eogCutOffPeriod);
        }
        DataSeries accMovement = remDataStore.getAccMovementData();
        DataSeries isMove = remDataStore.isMove();
        config.addTrace(new LineTraceConfig(), toChartData(eog1), "EOG", eog1.getScaling().getDataDimension());
        if (eog2 != null) {
            config.addTrace(new LineTraceConfig(), toChartData(eog2), "EOG1", eog2.getScaling().getDataDimension());
        }

        DataSeries alfa = new FilterHiPass(new FilterBandPass_Alfa(eog1Full), 2);
        config.addChartStack(6);
        config.addTrace(new LineTraceConfig(), toChartData(alfa), "Alpha", alfa.getScaling().getDataDimension());

        config.addChartStack(3);
        config.addTrace(new LineTraceConfig(LineTraceConfig.STEP), toChartData(accMovement), "Accelerometer", accMovement.getScaling().getDataDimension());
        config.addTrace(new LineTraceConfig(), toChartData(new Constant(accMovement, remDataStore.getAccMovementLimit())), "");

        DataSeries eogDiff = eog1Full;
        if (eog2Full != null) {
            eogDiff = new Minus(eog1Full, eog2Full);
        }
        FilterDerivativeRem eogDerivativeRem = new FilterDerivativeRem(eogDiff);
        DataSeries eogDerivativeRemAbs = new Abs(eogDerivativeRem);

        config.addPreviewStack(new Range(0.0, 800.0));
        XYData remData = toChartData(eogDerivativeRemAbs);
        remData.setYGroupingType(GroupingType.MAX);
        XYData isMoveData = toChartData(isMove);

        config.addPreviewTrace(new LineTraceConfig(LineTraceConfig.VERTICAL_LINES), remData, "Rem", eogDerivativeRemAbs.getScaling().getDataDimension());
        config.addPreviewTrace(new BooleanTraceConfig(), isMoveData, "Movements");

        // chartPanel.addPreviewPanel(2, false);
        // chartPanel.addPreview(eogDerivativeRemAbs, CompressionType.MAX);
        // chartPanel.addPreview(isSleep, GraphType.BOOLEAN, CompressionType.BOOLEAN);
        return config;

    }
}
