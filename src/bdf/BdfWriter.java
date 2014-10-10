package bdf;

import device.BdfDataListener;
import com.crostec.ads.AdsUtils;
import device.BdfConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class BdfWriter implements BdfDataListener {

    private static final Log LOG = LogFactory.getLog(BdfWriter.class);
    private final BdfConfig bdfConfig;
    private RandomAccessFile fileToSave;
    private long startRecordingTime;
    private long stopRecordingTime;
    private int numberOfDataRecords;
    private boolean stopRecordingRequest;

    public BdfWriter(BdfConfig bdfConfig) {
        this.bdfConfig = bdfConfig;
        try {
            this.fileToSave = new RandomAccessFile(bdfConfig.getFileNameToSave(), "rw");
        } catch (FileNotFoundException e) {
            LOG.error(e);
        }
    }

    @Override
    public synchronized void onAdsDataReceived(int[] dataFrame) {
        if (!stopRecordingRequest) {
            if (numberOfDataRecords == 0) {
                startRecordingTime = System.currentTimeMillis() - (long)bdfConfig.getDurationOfADataRecord(); //1 second (1000 msec) duration of a data record
                bdfConfig.setStartTime(startRecordingTime);
                try {
                    fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfConfig));
                } catch (IOException e) {
                    LOG.error(e);
                    throw new RuntimeException(e);
                }
            }
            numberOfDataRecords++;
            stopRecordingTime = System.currentTimeMillis();
            for (int i = 0; i < dataFrame.length; i++) {
                try {
                    fileToSave.write(AdsUtils.to24BitLittleEndian(dataFrame[i]));
                } catch (IOException e) {
                    LOG.error(e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public synchronized void onStopRecording() {
        if (stopRecordingRequest) return;
        stopRecordingRequest = true;
        double durationOfDataRecord = (stopRecordingTime - startRecordingTime) * 0.001 / numberOfDataRecords;
        bdfConfig.setDurationOfADataRecord(durationOfDataRecord);
        bdfConfig.setNumberOfDataRecords(numberOfDataRecords);
        try {
            fileToSave.seek(0);
            fileToSave.write(BdfHeaderWriter.createBdfHeader(bdfConfig));
            fileToSave.close();
        } catch (IOException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SS");
        LOG.info("Start recording time = " + startRecordingTime + " (" + dateFormat.format(new Date(startRecordingTime)));
        LOG.info("Stop recording time = " + stopRecordingTime + " (" + dateFormat.format(new Date(stopRecordingTime)));
        LOG.info("Number of data records = " + numberOfDataRecords);
        LOG.info("Duration of a data record = " + durationOfDataRecord);
    }
}
