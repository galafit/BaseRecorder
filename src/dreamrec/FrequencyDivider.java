package dreamrec;

import device.BdfDataListener;
import device.BdfDataSource;

import java.util.LinkedList;
import java.util.Queue;

public abstract class FrequencyDivider implements BdfDataSource, BdfDataListener {
    private Queue<int[]> dataRecordsBuffer = new LinkedList<int[]>();
    private int divider;
    private int counter;

    public FrequencyDivider(int divider) {
        this.divider = divider;
    }

    public void add(Integer value) {
        counter++;
     //   filteredData.offer(value);
        if (counter == divider) {
            int sum = 0;
            for (int i = 0; i < divider; i++) {
          //      sum += filteredData.poll();
            }
          //  notifyListeners(sum / divider);
            counter = 0;
        }
    }
}