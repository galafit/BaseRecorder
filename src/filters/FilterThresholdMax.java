package filters;

import data.Converter;
import data.DataSet;

/**
 *
 */
public class FilterThresholdMax extends Converter {
    int bufferSize;
 
    public FilterThresholdMax(DataSet inputData) {
        super(inputData);
        bufferSize = 10;
    }

    @Override
    public int get(int index) {
        int max = 0;
        int indexBegin = Math.max(0, index-bufferSize);

        for (int i = indexBegin; i <= index; i++) {
            max = Math.max(max, Math.abs(inputData.get(i)));
        }
        return max;
    }
}
