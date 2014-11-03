package dreamrec;

import graph.GraphsViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class View extends JFrame implements DataStoreListener {
    private String title = "Dream Recorder";
    private  JMenuBar menu = new JMenuBar();
    private Controller controller;
    private boolean isStartUpdating = false;

    protected DataStore model;
    protected GraphsViewer graphsViewer;


    public View(Controller controller) {
        this.controller = controller;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        formMenu();

        graphsViewer = new GraphsViewer();
        graphsViewer.setPreferredSize(getWorkspaceDimension());
        add(graphsViewer, BorderLayout.CENTER);

        addPanels();

        pack();
        setVisible(true);
    }


    public void showMessage(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    public void setDataStore(DataStore dataStore)  {
        model = dataStore;
        dataStore.addListener(this);
        addGraphs();
    }

    protected abstract void addPanels();

    protected abstract void addGraphs();

    @Override
    public void onDataStoreUpdate() {
        if(!isStartUpdating) {
            graphsViewer.setStart(model.getStartTime(), model.getSignalFrequency(0));
            isStartUpdating = true;
        }

        graphsViewer.syncView();
    }


    private Dimension getWorkspaceDimension() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int width = dimension.width - 20;
        int height = dimension.height - 150;
        return new Dimension(width, height);
    }


    private void formMenu() {
        JMenu fileMenu = new JMenu("File");
        menu.add(fileMenu);
        JMenuItem open = new JMenuItem("Open");
        fileMenu.add(open);

        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.readFromFile();
            }
        });

        JMenu recordMenu = new JMenu("Record");
        menu.add(recordMenu);
        JMenuItem start = new JMenuItem("Start");
        JMenuItem stop = new JMenuItem("Stop");
        recordMenu.add(start);
        recordMenu.add(stop);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.startRecording();
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.stopRecording();
            }
        });

        add(menu, BorderLayout.NORTH);
    }
}