package bdf;

import device.BdfConfig;
import device.BdfSignalConfig;
import dreamrec.ApplicationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
HEADER RECORD
8 ascii : version of this data format (0)
80 ascii : local patient identification (mind item 3 of the additional EDF+ specs)
80 ascii : local recording identification (mind item 4 of the additional EDF+ specs)
8 ascii : startdate of recording (dd.mm.yy) (mind item 2 of the additional EDF+ specs)
8 ascii : starttime of recording (hh.mm.ss)
8 ascii : number of bytes in header record
44 ascii : reserved
8 ascii : number of data records (-1 if unknown, obey item 10 of the additional EDF+ specs)
8 ascii : duration of a data record, in seconds
4 ascii : number of signals (ns) in data record
ns * 16 ascii : ns * label (e.g. EEG Fpz-Cz or Body temp) (mind item 9 of the additional EDF+ specs)
ns * 80 ascii : ns * transducer type (e.g. AgAgCl electrode)
ns * 8 ascii : ns * physical dimension (e.g. uV or degreeC)
ns * 8 ascii : ns * physical minimum (e.g. -500 or 34)
ns * 8 ascii : ns * physical maximum (e.g. 500 or 40)
ns * 8 ascii : ns * digital minimum (e.g. -2048)
ns * 8 ascii : ns * digital maximum (e.g. 2047)
ns * 80 ascii : ns * prefiltering (e.g. HP:0.1Hz LP:75Hz)
ns * 8 ascii : ns * nr of samples in each data record
ns * 32 ascii : ns * reserved
DATA RECORD
nr of samples[1] * integer : first signal in the data record
nr of samples[2] * integer : second signal
..
..
nr of samples[ns] * integer : last signal


 */

public class BdfHeaderReader {
    private BdfConfig bdfConfig = new BdfConfig();
    private static final Log log = LogFactory.getLog(BdfHeaderReader.class);
    private long startTime;
    private int numberOfDataRecords;

    public BdfHeaderReader(File file) throws ApplicationException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            int VERSION_LENGTH = 8;
            int PATIENT_LENGTH = 80;
            int RECORD_LENGTH = 80;
            int STARTDATE_LENGTH = 8;
            int STARTTIME_LENGTH = 8;
            int NUMBER_OF_BYTES_LENGTH = 8;
            int RESERVED_LENGTH = 44;
            int NUMBER_Of_DATARECORDS_LENGTH = 8;
            int DURATION_OF_DATARECORD_LENGTH = 8;
            int NUMBER_OF_SIGNALS_LENGTH = 4;

            int SIGNAL_LABEL_LENGTH = 16;
            int SIGNAL_TRANSDUCER_TYPE_LENGTH = 80;
            int SIGNAL_PHYSICAL_DIMENSION_LENGTH = 8;
            int SIGNAL_PHYSICAL_MIN_LENGTH = 8;
            int SIGNAL_PHYSICAL_MAX_LENGTH = 8;
            int SIGNAL_DIGITAL_MIN_LENGTH = 8;
            int SIGNAL_DIGITAL_MAX_LENGTH = 8;
            int SIGNAL_PREFILTERING_LENGTH = 80;
            int SIGNAL_NUMBER_OF_SAMPLES_LENGTH = 8;
            int SIGNAL_RESERVED_LENGTH = 32;

            char[] buffer;
            buffer = new char[VERSION_LENGTH];
            reader.read(buffer, 0, VERSION_LENGTH);

            buffer = new char[PATIENT_LENGTH];
            reader.read(buffer, 0, PATIENT_LENGTH);
            bdfConfig.setLocalPatientIdentification(new String(buffer).trim());

            buffer = new char[RECORD_LENGTH];
            reader.read(buffer, 0, RECORD_LENGTH);
            bdfConfig.setLocalRecordingIdentification(new String(buffer).trim());

            buffer = new char[STARTDATE_LENGTH];
            reader.read(buffer, 0, STARTDATE_LENGTH);
            String startDateStr = new String(buffer);

            buffer = new char[STARTTIME_LENGTH];
            reader.read(buffer, 0, STARTTIME_LENGTH);
            String startTimeStr = new String(buffer);

            String dateFormat = "dd.MM.yy HH.mm.ss";
            String startDateTimeStr = startDateStr + " " + startTimeStr;

            try{
                Date date = new SimpleDateFormat(dateFormat).parse(startDateTimeStr);
                startTime = date.getTime();
            } catch (Exception e) {
                throw new ApplicationException("Error while parsing startDateTimeStr " + startDateTimeStr);
            }

            buffer = new char[NUMBER_OF_BYTES_LENGTH];
            reader.read(buffer, 0, NUMBER_OF_BYTES_LENGTH);

            buffer = new char[RESERVED_LENGTH];
            reader.read(buffer, 0, RESERVED_LENGTH);

            buffer = new char[NUMBER_Of_DATARECORDS_LENGTH];
            reader.read(buffer, 0, NUMBER_Of_DATARECORDS_LENGTH);
            numberOfDataRecords = stringToInt(new String(buffer));

            buffer = new char[DURATION_OF_DATARECORD_LENGTH];
            reader.read(buffer, 0, DURATION_OF_DATARECORD_LENGTH);
            Double durationOfDataRecord = stringToDouble(new String(buffer));
            bdfConfig.setDurationOfADataRecord(durationOfDataRecord);

            buffer = new char[NUMBER_OF_SIGNALS_LENGTH];
            reader.read(buffer, 0, NUMBER_OF_SIGNALS_LENGTH);
            int numberOfSignals =  stringToInt(new String(buffer));

            ArrayList<BdfSignalConfig> bdfSignalConfigList = new ArrayList<BdfSignalConfig>();

            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                BdfSignalConfig bdfSignalConfig = new BdfSignalConfig();
                bdfSignalConfigList.add(bdfSignalConfig);
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_LABEL_LENGTH];
                reader.read(buffer, 0, SIGNAL_LABEL_LENGTH);
                bdfSignalConfigList.get(signalNumber).setLabel(new String(buffer).trim());
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_TRANSDUCER_TYPE_LENGTH];
                reader.read(buffer, 0, SIGNAL_TRANSDUCER_TYPE_LENGTH);
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_PHYSICAL_DIMENSION_LENGTH];
                reader.read(buffer, 0, SIGNAL_PHYSICAL_DIMENSION_LENGTH);
                bdfSignalConfigList.get(signalNumber).setPhysicalDimension(new String(buffer).trim());
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_PHYSICAL_MIN_LENGTH];
                reader.read(buffer, 0, SIGNAL_PHYSICAL_MIN_LENGTH);
                int physicalMin =  stringToInt(new String(buffer));
                bdfSignalConfigList.get(signalNumber).setPhysicalMin(physicalMin);
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_PHYSICAL_MAX_LENGTH];
                reader.read(buffer, 0, SIGNAL_PHYSICAL_MAX_LENGTH);
                int physicalMax =  stringToInt(new String(buffer));
                bdfSignalConfigList.get(signalNumber).setPhysicalMax(physicalMax);
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_DIGITAL_MIN_LENGTH];
                reader.read(buffer, 0, SIGNAL_DIGITAL_MIN_LENGTH);
                int digitalMin =  stringToInt(new String(buffer));
                bdfSignalConfigList.get(signalNumber).setDigitalMin(digitalMin);
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_DIGITAL_MAX_LENGTH];
                reader.read(buffer, 0, SIGNAL_DIGITAL_MAX_LENGTH);
                int digitalMax =  stringToInt(new String(buffer));
                bdfSignalConfigList.get(signalNumber).setDigitalMax(digitalMax);
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_PREFILTERING_LENGTH];
                reader.read(buffer, 0, SIGNAL_PREFILTERING_LENGTH);
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_NUMBER_OF_SAMPLES_LENGTH];
                reader.read(buffer, 0, SIGNAL_NUMBER_OF_SAMPLES_LENGTH);
                int numberOfSamplesInDataRecord =  stringToInt(new String(buffer));
                bdfSignalConfigList.get(signalNumber).setNrOfSamplesInEachDataRecord(numberOfSamplesInDataRecord);
            }
            for(int signalNumber = 0; signalNumber < numberOfSignals; signalNumber++) {
                buffer = new char[SIGNAL_RESERVED_LENGTH];
                reader.read(buffer, 0, SIGNAL_RESERVED_LENGTH);
            }

            bdfConfig.setSignalConfigList(bdfSignalConfigList);

        } catch (Exception e) {
            log.error(e);
            throw new ApplicationException("Error while reading from file " + file.getName());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    public BdfConfig getBdfConfig() {
        return bdfConfig;
    }
      
    public long getStartTime() {
        return startTime;
    }

    private Integer stringToInt(String str) {
        if(str == null || str.length()==0){
            return 0;
        }
        str = str.trim();
        return Integer.valueOf(str);
    }

    private Double stringToDouble(String str) {
        if(str == null || str.length()==0){
            return 0.0;
        }
        str = str.trim();
        return Double.valueOf(str);
    }
}

