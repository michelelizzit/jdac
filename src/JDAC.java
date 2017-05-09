/*
 *  JDAC
 *  Author: Michele Lizzit - lizzit.it
 *  v1.2 - 9/5/2017
 *
 *  Please go to https://lizzit.it/jdac for more informations about the project
 *
 *
 *  Copyright (C) 2017  Michele Lizzit
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package JDAC;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import purejavacomm.*;



import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by michele on 27/01/17.
 */
public class JDAC extends JFrame implements SerialPortEventListener {
    public static XYDataset mDataset;
    public static XYSeries mSeries;
    public static XYSeries mLastSeries;
    public static JButton resetChartButton;
    public static JMenuItem connectButton;
    public static JMenuItem disconnectButton;
    public static JMenuItem scanButton;
    public static JMenuItem exportCSVButton;
    public static JMenuItem exportPNGButton;
    public static JLabel serialStatusLabel;
    public static JLabel statusLabel;
    public static JButton startButton;
    public static JButton stopButton;
    public static JSpinner lastSpinner;
    public static SerialPort serialPort;
    final static int NEW_LINE_ASCII = 10;
    public static boolean serialConnected = false;
    public static boolean collectingData = false;
    public long startTime = 0;
    public static JFreeChart mChart;
    public static JFreeChart mLastChart;
    public static int numLastValues = 10;
    public JMenuBar mMenuBar;
    public static JMenu portSubMenu;
    public static String selectedPortName = null;
    public static LabelClear clearStatus;
    public static final String mainChartPreviewTitle = "Preview";
    public static String currentSensor;


    public JDAC()
    {
        setTitle("JDAC");

        ImageIcon img = new ImageIcon("logo.png");
        setIconImage(img.getImage());

        //setLayout(new FlowLayout());

        mDataset = createDataset("A");

        mChart = ChartFactory.createXYLineChart(
                "Preview" ,
                "Time (ms)" ,
                "Value" ,
                mDataset ,
                PlotOrientation.VERTICAL ,
                false , true , false);

        mLastChart = ChartFactory.createXYLineChart(
                "Last n values" ,
                "Time (ms)" ,
                "Value" ,
                createLastDataset("B"),
                PlotOrientation.VERTICAL ,
                false , true , false);
        mChart.getXYPlot().getDomainAxis().setLowerMargin(0);
        mChart.getXYPlot().getDomainAxis().setUpperMargin(0);
        mLastChart.getXYPlot().getDomainAxis().setLowerMargin(0);
        mLastChart.getXYPlot().getDomainAxis().setUpperMargin(0);

        ChartPanel chartPanel = new ChartPanel( mChart );
        ChartPanel chartLastPanel = new ChartPanel( mLastChart );
        //chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );

        XYPlot plot = mChart.getXYPlot( );
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
        renderer.setSeriesPaint( 0 , Color.GREEN );
        renderer.setSeriesStroke( 0 , new BasicStroke( 1.0f ) );
        plot.setRenderer(renderer);

        XYPlot lastPlot = mLastChart.getXYPlot( );
        XYLineAndShapeRenderer lastRenderer = new XYLineAndShapeRenderer( );
        lastRenderer.setSeriesPaint( 0 , Color.RED );
        lastRenderer.setSeriesStroke( 0 , new BasicStroke( 1.0f ) );
        lastPlot.setRenderer(lastRenderer);


        resetChartButton = new JButton("Reset");
        resetChartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetChart();
            }
        });

        mMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu sensorMenu = new JMenu("Sensor");
        JMenu deviceMenu = new JMenu("Device");
        portSubMenu = new JMenu("Port");
        JMenu helpMenu = new JMenu("Help");

        serialStatusLabel = new JLabel("Disconnected");
        statusLabel = new JLabel("Ready");
        clearStatus = new LabelClear(statusLabel);
        clearStatus.resetTime();

        connectButton = new JMenuItem("Connect");
        disconnectButton = new JMenuItem("Disconnect");
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        scanButton = new JMenuItem("Refresh port list");
        exportCSVButton = new JMenuItem("Export data to CSV");
        exportCSVButton.setAccelerator(KeyStroke.getKeyStroke("control S"));
        exportCSVButton.setMnemonic(KeyEvent.VK_S);
        exportPNGButton = new JMenuItem("Export chart to PNG");

        JPanel optionsPanel = new JPanel(new BorderLayout());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("control X"));
        exitItem.setMnemonic(KeyEvent.VK_X);

        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem helpItem = new JMenuItem("Help");
        JMenuItem quickStartItem = new JMenuItem("Quick start");

        lastSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));


        ActionListener mSensorListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSensor(e.getActionCommand());
            }
        };
        ButtonGroup sensorGroup = new ButtonGroup();
        JRadioButtonMenuItem tmpRadioButton = new JRadioButtonMenuItem("Temperature");
        sensorGroup.add(tmpRadioButton); sensorMenu.add(tmpRadioButton);
        tmpRadioButton.addActionListener(mSensorListener);
        tmpRadioButton = new JRadioButtonMenuItem("Distance");
        sensorGroup.add(tmpRadioButton); sensorMenu.add(tmpRadioButton);
        tmpRadioButton.addActionListener(mSensorListener);
        tmpRadioButton = new JRadioButtonMenuItem("Voltage");
        sensorGroup.add(tmpRadioButton); sensorMenu.add(tmpRadioButton);
        tmpRadioButton.addActionListener(mSensorListener);
        tmpRadioButton = new JRadioButtonMenuItem("Generic");
        tmpRadioButton.setSelected(true);
        setSensor("Generic");
        sensorGroup.add(tmpRadioButton); sensorMenu.add(tmpRadioButton);
        tmpRadioButton.addActionListener(mSensorListener);

        connectButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                if (selectedPortName == null) {
                    setStatus("No port selected");
                    return;
                }
                connect(selectedPortName);
            }
        });
        disconnectButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                serialStatusLabel.setText("Disconnecting...");
                if (serialPort == null) {
                    serialStatusLabel.setText("Disconnected");
                    serialConnected = false;
                    return;
                }

                stopCollect();
                disconnect();
            }
        });
        scanButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                searchForPorts();
            }
        });
        exportCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });
        exportPNGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportPNG();
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCollect();
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopCollect();
            }
        });
        lastSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                numLastValues = (Integer)lastSpinner.getValue();
                updateLastTitle();
            }
        });
        updateLastTitle();
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HelpFrame();
            }
        });
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutFrame();
            }
        });
        quickStartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StartFrame();
            }
        });

        fileMenu.add(exportCSVButton);
        fileMenu.add(exportPNGButton);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        deviceMenu.add(connectButton);
        deviceMenu.add(disconnectButton);
        deviceMenu.addSeparator();
        deviceMenu.add(portSubMenu);
        deviceMenu.add(scanButton);
        helpMenu.add(quickStartItem);
        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);


        mMenuBar.add(fileMenu);
        mMenuBar.add(sensorMenu);
        mMenuBar.add(deviceMenu);
        mMenuBar.add(helpMenu);

        JPanel controlsPanel = new JPanel();
        controlsPanel.add(startButton);
        controlsPanel.add(stopButton);
        controlsPanel.add(resetChartButton);
        optionsPanel.add(controlsPanel, BorderLayout.LINE_START);

        JPanel lastPanel = new JPanel(new FlowLayout());
        lastPanel.add(new JLabel("Shown values: "));
        lastPanel.add(lastSpinner);
        optionsPanel.add(lastPanel);

        JPanel serialPanel = new JPanel(new FlowLayout());
        serialPanel.add(serialStatusLabel);
        optionsPanel.add(serialPanel, BorderLayout.LINE_END);

        add(optionsPanel, BorderLayout.PAGE_START);

        JPanel mainPanel = new JPanel(new GridLayout(0,2));
        mainPanel.add(chartPanel);
        mainPanel.add(chartLastPanel);
        add(mainPanel);

        add(statusLabel, BorderLayout.PAGE_END);
        setJMenuBar(mMenuBar);



        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent we) {
                                  dispose();
                              }
                          }
        );

        setSize(800, 800);
        pack();
        new StartFrame();

        //center on screen
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);

        setVisible(true);
    }

    public void updateLastTitle() {
        mLastChart.setTitle("Last " + numLastValues + " values");
    }

    public void saveData() {
        DateFormat df = new SimpleDateFormat("ddmmyy_HHmm");

        FileDialog fd = new FileDialog(this, "Export as...", FileDialog.SAVE);

        fd.setFile("Data_" + df.format(new Date()) + ".csv");
        fd.setVisible(true);

        if (fd.getFile() == null) {
            setStatus("Export CSV canceled");
            //export canceled
            return;
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fd.getDirectory()+fd.getFile()));
            out.write(dataToCSVstring());
            out.close();
            setStatus("Export CSV successful");
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "" +
                    "Error while exporting data...\n" +
                    "If the error persists please contact the system administrator");
            return;
        }
    }

    public void exportPNG() {
        DateFormat df = new SimpleDateFormat("ddmmyy_HHmm");

        FileDialog fd = new FileDialog(this, "Export as...", FileDialog.SAVE);

        fd.setFile("Chart_" + df.format(new Date()) + ".png");
        fd.setVisible(true);

        if (fd.getFile() == null) {
            setStatus("Export PNG canceled");
            //export canceled
            return;
        }

        mChart.setTitle(currentSensor);

        try {
            ChartUtilities.saveChartAsPNG(new File(fd.getDirectory() + fd.getFile()), mChart, Definitions.exportPNGwidth, Definitions.exportPNGheight);
            setStatus("Export PNG successful");
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "" +
                    "Error while exporting chart...\n" +
                    "If the error persists please contact the system administrator");
            mChart.setTitle(mainChartPreviewTitle);
            return;
        }

        mChart.setTitle(mainChartPreviewTitle);
    }

    public void startCollect() {
        if (collectingData) return;
        if (!serialConnected) {
            JOptionPane.showMessageDialog(this, "" +
                    "Unable to start collecting data\n" +
                    "Please select a serial port and press the connect button\n" +
                    "If the error persists please contact the system administrator\n" +
                    "ERR: disconnected");
            return;
        }

        if (valueBuf.size() == 0) {
            resetChart();
        }

        collectingData = true;
        setStatus("Collecting data...");
    }

    public void stopCollect() {
        if (!collectingData) return;
        collectingData = false;
        setStatus("Ready");
    }

    public String dataToCSVstring() {
        String result = "";

        result += "Value, Time (ms since epoch)";

        for (int i = 0; i < valueBuf.size(); i++) {
            result += valueBuf.get(i);
            result += ",";
            result += timeBuf.get(i).getTime();
            result += "\n";
        }
        return result;
    }

    public void resetChart() {
        valueBuf.clear();
        timeBuf.clear();
        mSeries.clear();
        mLastSeries.clear();
        startTime = (new Date()).getTime();
    }

    private XYDataset createDataset(String title)
    {
        mSeries = new XYSeries( title );
        XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( mSeries );
        return dataset;
    }

    private XYDataset createLastDataset(String title)
    {
        mLastSeries = new XYSeries( title );
        XYSeriesCollection dataset = new XYSeriesCollection( );
        dataset.addSeries( mLastSeries );
        return dataset;
    }

    public void setSensor(String sensorType) {
        stopCollect();
        resetChart();
        currentSensor = sensorType;
        //mChart.setTitle(sensorType);
        switch (sensorType) {
            case "Temperature":
                mChart.getXYPlot().getRangeAxis().setLabel("Value (°C)");
                mLastChart.getXYPlot().getRangeAxis().setLabel("Value (°C)");
                break;
            case "Distance":
                mChart.getXYPlot().getRangeAxis().setLabel("Value (mm)");
                mLastChart.getXYPlot().getRangeAxis().setLabel("Value (mm)");
                break;
            case "Voltage":
                mChart.getXYPlot().getRangeAxis().setLabel("Value (V)");
                mLastChart.getXYPlot().getRangeAxis().setLabel("Value (V)");
                break;
            case "Generic":
                mChart.getXYPlot().getRangeAxis().setLabel("Value");
                mLastChart.getXYPlot().getRangeAxis().setLabel("Value");
                break;
            default:
                JOptionPane.showMessageDialog(this, "" +
                        "Something strange happened, either caused by a bug or a modification in the source code\n" +
                        "If the error persists please contact the system administrator");
                break;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    //UIManager.getSystemLookAndFeelClassName());
                    UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(new Frame(), "" +
                    "Something strange happened\n" +
                    "If the error persists please contact the system administrator\n" +
                    "ERR: theme error");
        }

        JDAC demo = new JDAC();

        RefineryUtilities.centerFrameOnScreen( demo );
        Runnable tmp = new Runnable() {
            public void run() {
                System.out.println("JDAC");
                System.out.println("Credits: Michele Lizzit - https://lizzit.it/jdac");

                searchForPorts();
            }
        };
        tmp.run();
    }
    public static HashMap portMap = new HashMap();
    public static void searchForPorts()
    {
        portSubMenu.removeAll();

        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        ActionListener mPortListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPortName = e.getActionCommand();
            }
        };
        ButtonGroup sensorGroup = new ButtonGroup();

        while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                JRadioButtonMenuItem tmpRadioButton = new JRadioButtonMenuItem(curPort.getName());
                sensorGroup.add(tmpRadioButton); portSubMenu.add(tmpRadioButton);
                tmpRadioButton.addActionListener(mPortListener);

                portMap.put(curPort.getName(), curPort);
            }
        }
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
        clearStatus.resetTime();
    }

    public void connect(String selectedPort)
    {
        serialStatusLabel.setText("Connecting...");
        CommPortIdentifier selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);

        CommPort commPort = null;

        try
        {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("Port", 10);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;

            //set serial speed
            serialPort.setSerialPortParams( Definitions.serialSpeed,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE );
        }
        catch (PortInUseException e)
        {
            JOptionPane.showMessageDialog(this, "" +
                    "Port already in use, please close other applications that use this port and try again.\n" +
                    "If the error persists please contact the system administrator");
            return;
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "" +
                    "Something strange happened, likely to be cause by a wrong serial port selected. Please select another port and try again.\n" +
                    "If the error persists please contact the system administrator");
            return;
        }
        initIOStream();
    }

    private InputStream input = null;
    private OutputStream output = null;
    public void initIOStream()
    {
        try {
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            //writeData(0, 0);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "" +
                    "Something strange happened, please restart this application, otherwise it could misbehave, and possibly create a kugelblitz.\n" +
                    "If the error persists please contact the system administrator\n" +
                    "ERR: Unable to open stream");
            return;
        }
        initListener();
    }

    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            JOptionPane.showMessageDialog(this, "" +
                    "Something strange happened, please restart this application, otherwise it could misbehave, and possibly create a kugelblitz.\n" +
                    "If the error persists please contact the system administrator\n" +
                    "ERR: Too many listeners");
            return;
        }
        serialStatusLabel.setText("Connected");
        serialConnected = true;
    }

    public void disconnect()
    {
        //close the serial port
        try
        {
            //writeData(0, 0);

            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "" +
                    "Failed to close serial port");
            return;
        }
        serialStatusLabel.setText("Disconnected");
        serialConnected = false;
    }
    public ArrayList<Byte> serialBuf = new ArrayList<Byte>();
    public ArrayList<Double> valueBuf = new ArrayList<Double>();
    public ArrayList<Date> timeBuf = new ArrayList<Date>();
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            byte singleData = 0;
            try
            {
                singleData = (byte)input.read();
                if (!collectingData) return;
                serialBuf.add(singleData);
                //if (singleData == (byte)NEW_LINE_ASCII) {
                    checkBuffer();
                //}
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(this, "" +
                        "Something strange happened, please restart this application, otherwise it could misbehave, and possibly create a kugelblitz.\n" +
                        "If the error persists please contact the system administrator\n" +
                        "ERR: Unable to read from serial");
                e.printStackTrace();
                return;
            }

        }
    }

    public void checkBuffer() {
        if (!serialBuf.contains((byte)NEW_LINE_ASCII)) return;
        //System.out.println("C: " + serialBuf.get(serialBuf.size() - 1));
        String result = "";
        Double value = 0d;
            //System.out.println("N");
            //int cnt = 0;
            for (int cnt = 0; cnt < serialBuf.size(); cnt++) {
                byte currentByte = serialBuf.get(cnt);
                if (currentByte == (byte)NEW_LINE_ASCII) {
                    serialBuf.remove(0);
                    break;
                }
                if (currentByte == 13) continue;

                result += (char)((int)currentByte);
                serialBuf.remove(cnt);
                cnt--;
            }
            if (result.length() > 0) {
                try {
                    value = Double.parseDouble(result);
                }
                catch (NumberFormatException e) {
                    System.err.print("ERR: received corrupted data, discarding packet: ");
                    System.err.println(result);
                    return;
                }
                valueBuf.add(value);
                Date time = new Date();
                timeBuf.add(time);
                mSeries.add(time.getTime() - startTime, value);

                mLastSeries.add(time.getTime() - startTime, value);
                while (mLastSeries.getItemCount() > numLastValues) {
                    mLastSeries.remove(0);
                }
                //System.out.println("V: " + value);
            }
            //System.out.println("R: " + result);

    }
}
