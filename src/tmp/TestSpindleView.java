package tmp;


import data.DataSet;
import filters.*;
import graph.GraphsViewer;

import javax.swing.*;
import java.awt.*;

public class TestSpindleView extends JFrame {
    private String title = "Spindle Test";
    private GraphsViewer graphsViewer;
    private  JMenuBar menu = new JMenuBar();


    public TestSpindleView() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        graphsViewer = new GraphsViewer();
        graphsViewer.setPreferredSize(getWorkspaceDimention());

        graphsViewer.addGraphPanel(8, true);
        graphsViewer.addGraphPanel(8, true);
        graphsViewer.addPreviewPanel(1, true);


        DataSet testData = new TestSpindle().getDataStream();
        DataSet filteredData = new FilterBandPass_Alfa(testData);
        graphsViewer.addGraphs(0, testData);
        graphsViewer.addGraphs(1, filteredData);

        graphsViewer.addPreviews(0, new CompressorMaximizing(filteredData, graphsViewer.getCompression()));

        add(graphsViewer, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    public void syncView() {
        graphsViewer.syncView();
    }

    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

   /* public void setStart(long starTime, int period_msec) {
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
        TestSpindleView testWindow = new TestSpindleView();
        testWindow.syncView();
    }
}
