package filters;

import data.Converter;
import data.DataSet;

/**
 *
 */

public class FilterDerivativeAbs extends Converter {

    public FilterDerivativeAbs(DataSet inputData) {
        super(inputData);
    }

    @Override
    public int get(int index) {
        if (index == 0) {
            return 0;
        }

        return  Math.abs(inputData.get(index) - inputData.get(index - 1));
    }
}
