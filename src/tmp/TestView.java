package tmp;

import data.DataSet;
import filters.*;
import graph.GraphsView;

import javax.swing.*;
import java.awt.*;

/**
 * Main Window of our program...
 */
public class TestView extends JFrame {
    private String title = "Dream Recorder";
    private GraphsView graphsView;
    private  JMenuBar menu = new JMenuBar();
    private int COMPRESSION = 50;

    public TestView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        graphsView = new GraphsView();
        graphsView.setPreferredSize(getWorkspaceDimention());

        graphsView.addGraphPanel(1, true);
        graphsView.addGraphPanel(1, true);
//        graphsViewer.addGraphPanel(1, true);

        graphsView.addPreviewPanel(1, false);
        graphsView.setCompression(COMPRESSION);

        TestData data = new TestData();
        DataSet testData = data.getCosStream();

        DataSet filteredData = new FilterBandPass_Alfa(testData);
//        DataStream filteredData = new FilterBandPass_Delta_1(testData);

     //   DataSet compressedFilteredData = new CompressorMaximizing(filteredData, graphsView.getCompression());

/*        graphsViewer.addGraph(0, testData);
        graphsViewer.addGraph(0, data.getPeriodStream());

        graphsViewer.addGraph(1, filteredData);

        graphsViewer.addPreview(0, compressedFilteredData);

        add(graphsViewer, BorderLayout.CENTER);*/

        pack();
       // setFocusable(true);
        setVisible(true);
    }

    public void syncView() {
       // graphsView.syncView();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

  /*  public void setStart(long starTime, int period_msec) {
        graphsViewer.setStart(starTime, period_msec);
    }*/

    private Dimension getWorkspaceDimention() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dimension.width - 20;
        int height = dimension.height - 150;
        return new Dimension(width, height);
    }

    public static void main(String[] args) {
        TestView testWindow = new TestView();
        testWindow.syncView();
    }
}
