package ui;


import controller.MainFrameController;
import moonbuggy.Point3d;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MoonBuggyControlFrame extends ApplicationFrame {
    private final MainFrameController controller;
    private final UltrasonicRadarPanel ultrasonicRadarPanel;

    private ChartPanel frontLeftDistancePanel;
    private ChartPanel frontRightDistancePanel;
    private ChartPanel behindDistancePanel;

    private final ChartPanel pitchPanel;
    private final ChartPanel rollPanel;
    private final ChartPanel compassPanel;

    private final MouseMotorControl drivingByMousePanel;
    private final JPanel drivingByButtonsPanel;

    private final NetworkToolsPanel networkTools;
    private final CameraAngleAndMotorPower cameraAngleAndMotorPower;

    private Timer monitoringTimer;

    private TimeSeries frontLeftDistanceTimeSeries = new TimeSeries("front left");
    private TimeSeries frontRightDistanceTimeSeries = new TimeSeries("front right");
    private TimeSeries behindDistanceTimeSeries = new TimeSeries("behind");

    private DefaultValueDataset compassValue;
    private DefaultValueDataset pitchValue;
    private DefaultValueDataset rollValue;

    private class MotorDriver{
        private int xPercents = 0;
        private int yPercents = 0;

        public void setXPercents(int xPercents){
            this.xPercents = xPercents;
            updateMotors();
        }

        public void setYPercents( int yPercents){
            this.yPercents = yPercents;
            updateMotors();
        }

        private void updateMotors(){
            controller.setDirectionVector( this.xPercents, this.yPercents);
        }
    }

    private MotorDriver motorDriver = new MotorDriver();

    public MoonBuggyControlFrame(String title, final MainFrameController controller)
    {
        super(title);
        this.controller = controller;

        ultrasonicRadarPanel = UltrasonicRadarPanel.create();
        ultrasonicRadarPanel.setPreferredSize(new Dimension(100,300));

        frontLeftDistancePanel = createFrontLeftDistanceChartPanel();
        frontLeftDistancePanel.setPreferredSize(new Dimension(400, 100));

        frontRightDistancePanel = createFrontRightDistanceChartPanel();
        frontRightDistancePanel.setPreferredSize(new Dimension(400, 100));

        behindDistancePanel = createBehindDistanceChartPanel();
        behindDistancePanel.setPreferredSize(new Dimension(400, 100));

        pitchValue = new DefaultValueDataset(0);
        pitchPanel = createDial(pitchValue);
        pitchPanel.setPreferredSize(new Dimension(200,200));

        rollValue = new DefaultValueDataset(0);
        rollPanel = createDial(rollValue);
        rollPanel.setPreferredSize(new Dimension(200,200));

        compassPanel = createCreateCompass();
        compassPanel.setPreferredSize(new Dimension(200, 200));

        drivingByMousePanel = new MouseMotorControl(new MouseMotorControl.Listener() {
            @Override
            public void setXPercents(int percents) {
                motorDriver.setXPercents(percents);
            }

            @Override
            public void setYPercents(int percents) {
                motorDriver.setYPercents(percents);
            }

            @Override
            public void end() {
                controller.stop();
                if( null != monitoringTimer ){
                    monitoringTimer.start();
                }
            }

            @Override
            public void begin() {
                if( null != monitoringTimer ){
                    monitoringTimer.stop();
                }
            }
        });

        drivingByButtonsPanel = new JPanel();
        drivingByButtonsPanel.setPreferredSize(new Dimension(200, 200));
        drivingByButtonsPanel.setBackground(Color.WHITE);

        networkTools = new NetworkToolsPanel(new NetworkToolsPanel.Listener() {
            @Override
            public void connect(String address) {
                controller.connect(address);
                networkTools.appendLog("Connected");
                if( null != monitoringTimer ){
                    monitoringTimer.stop();
                }

                monitoringTimer = new Timer(300, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        updateTelemetry();
                    }
                });
                monitoringTimer.start();
            }
        });
        networkTools.setPreferredSize(new Dimension(200,200));

        controller.setLogListener(new MainFrameController.LogListener() {
            @Override
            public void appendLog(String text) {
                networkTools.appendLog(text);
            }
        });

        cameraAngleAndMotorPower = new CameraAngleAndMotorPower(new CameraAngleAndMotorPower.Listener() {
            @Override
            public void setMotorPower(int value) {
                controller.setSpeed(value);
            }

            @Override
            public void setCameraAngle(int angle) {
                controller.setAngle(angle);
            }

            @Override
            public void forward() {
                controller.forward();
            }

            @Override
            public void backward() {
                controller.backward();
            }

            @Override
            public void turnLeft() {
                controller.turnLeft();
            }

            @Override
            public void turnRight() {
                controller.turnRight();
            }

            @Override
            public void stop() {
                controller.stop();
            }

            @Override
            public void setYaw(int value) {
            }

            @Override
            public void turnOnLights() {
                controller.turnOnLights();
            }

            @Override
            public void turnOfLights() {
                controller.turnOfLights();
            }
        });

        cameraAngleAndMotorPower.setPreferredSize(new Dimension(200,200));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(ultrasonicRadarPanel)
                                        .addComponent(frontLeftDistancePanel)
                                        .addComponent(frontRightDistancePanel)
                                        .addComponent(behindDistancePanel))
                        .addGroup(layout.createParallelGroup()
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(pitchPanel)
                                                .addComponent(rollPanel)
                                                .addComponent(compassPanel)
                                )
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(drivingByMousePanel)
                                                .addComponent(cameraAngleAndMotorPower)
                                )
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(networkTools)
                                )
                        )
        );

        layout.setVerticalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(ultrasonicRadarPanel)
                        .addComponent(frontLeftDistancePanel)
                        .addComponent(frontRightDistancePanel)
                        .addComponent(behindDistancePanel))
                .addGroup(
                        layout.createSequentialGroup()
                                .addGroup(
                                        layout.createParallelGroup()
                                                .addComponent(pitchPanel)
                                                .addComponent(rollPanel)
                                                .addComponent(compassPanel))
                                .addGroup(
                                        layout.createParallelGroup()
                                                .addComponent(drivingByMousePanel)
                                                .addComponent(cameraAngleAndMotorPower))
                                .addGroup(
                                        layout.createParallelGroup()
                                                .addComponent(networkTools)
                                )));

        pack();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                    if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                        if(KeyEvent.VK_ALT == keyEvent.getKeyCode()){
                            updateDistance();
                        }
                    }
                    return false;
                }
        });
    }



    private ChartPanel createCreateCompass() {
        compassValue = new DefaultValueDataset(0);
        CompassPlot plot = new CompassPlot(compassValue);

        plot.setSeriesNeedle(0, 0);
        plot.setSeriesPaint(0, Color.BLACK);
        plot.setSeriesOutlinePaint(0, Color.BLACK);
        plot.setRoseCenterPaint(Color.WHITE);
        plot.setRoseHighlightPaint(Color.gray);

        return new ChartPanel(new JFreeChart(plot));
    }

    private ChartPanel createDial(DefaultValueDataset valueDataset){
        DialPlot dialPlot = new DialPlot(valueDataset);
        dialPlot.addScale(0, new StandardDialScale(-180, 180, 300, 300, 30, 4));
        dialPlot.setOutlineStroke(new BasicStroke(2f));
        dialPlot.mapDatasetToScale(0, 0);

        DialValueIndicator dialValueIndicator = new DialValueIndicator(0);
        dialValueIndicator.setNumberFormat(new DecimalFormat("#.# °"));
        dialValueIndicator.setRadius(0.85);
        dialValueIndicator.setPaint(Color.WHITE);
        dialValueIndicator.setOutlinePaint(Color.RED);
        dialValueIndicator.setBackgroundPaint(Color.GRAY);

        dialPlot.addLayer(new DialValueIndicator(0));
        dialPlot.addPointer(new org.jfree.chart.plot.dial.DialPointer.Pin());

        return new ChartPanel(new JFreeChart(dialPlot));
    }

    private static JFreeChart createDistanceChart(XYDataset xydataset)
    {
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("", "", "", xydataset, true, true, false);
        XYPlot xyplot = (XYPlot)jfreechart.getPlot();
        xyplot.setDomainCrosshairVisible(true);
        xyplot.setRangeCrosshairVisible(true);

        org.jfree.chart.renderer.xy.XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        if (xyitemrenderer instanceof XYLineAndShapeRenderer)
        {
                XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer)xyitemrenderer;
                xylineandshaperenderer.setBaseShapesVisible(false);
        }
        DateAxis dateaxis = (DateAxis)xyplot.getDomainAxis();
        dateaxis.setDateFormatOverride(new SimpleDateFormat("S"));

        xyplot.getRangeAxis().setAutoRange(false);
        xyplot.getRangeAxis().setRange(0, 350);

        return jfreechart;
    }

    public ChartPanel createFrontLeftDistanceChartPanel()
    {
        frontLeftDistanceTimeSeries = new TimeSeries("front left", Millisecond.class);
        frontLeftDistanceTimeSeries.setMaximumItemCount(20);

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        timeSeriesCollection.addSeries(frontLeftDistanceTimeSeries);

        JFreeChart jfreechart = createDistanceChart(timeSeriesCollection);
        return new ChartPanel(jfreechart);
    }

    public ChartPanel createFrontRightDistanceChartPanel()
    {
        frontRightDistanceTimeSeries = new TimeSeries("front right", Millisecond.class);
        frontRightDistanceTimeSeries.setMaximumItemCount(20);

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        timeSeriesCollection.addSeries(frontRightDistanceTimeSeries);


        JFreeChart jfreechart = createDistanceChart(timeSeriesCollection);
        return new ChartPanel(jfreechart);
    }


    public ChartPanel createBehindDistanceChartPanel()
    {
        behindDistanceTimeSeries = new TimeSeries("behind", Millisecond.class);
        behindDistanceTimeSeries.setMaximumItemCount(20);

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        timeSeriesCollection.addSeries(behindDistanceTimeSeries);

        JFreeChart jfreechart = createDistanceChart(timeSeriesCollection);
        return new ChartPanel(jfreechart);
    }

    private void updateTelemetry(){
        //updateDistance();
        updateCompass();
        updateRollAndPitch();
    }

    private void updateCompass() {
        short heading = controller.measureCompass();
        compassValue.setValue(heading);
    }

    private void updatePitch(Point3d accelMeasureResult){
        double fYg = accelMeasureResult.getY();
        double fZg = accelMeasureResult.getZ();

        double pitch  = (Math.atan2(-fYg, -fZg)*180.0)/Math.PI;
        pitchValue.setValue(pitch);
    }

    private void updateRoll(Point3d accelMeasureResult){
        double fXg = accelMeasureResult.getX();
        double fYg = accelMeasureResult.getY();
        double fZg = accelMeasureResult.getZ();

        double roll = (Math.atan2(fXg, Math.sqrt(fYg * fYg + fZg * fZg))*180.0)/Math.PI;
        rollValue.setValue(roll);
    }

    private void updateDistance() {
        int frontLeftDistance = controller.measureDistance(MainFrameController.UltrasonicSensor.FRONT_LEFT);
        frontLeftDistanceTimeSeries.add(new Millisecond(new Date()), (double) frontLeftDistance, true);

        int frontRightDistance = controller.measureDistance(MainFrameController.UltrasonicSensor.FRONT_RIGHT);
        frontRightDistanceTimeSeries.add(new Millisecond(new Date()), (double) frontRightDistance, true);

        int behindDistance = controller.measureDistance(MainFrameController.UltrasonicSensor.BACK);
        behindDistanceTimeSeries.add(new Millisecond(new Date()), (double) behindDistance, true);

        int frontCenter = controller.measureDistance(MainFrameController.UltrasonicSensor.FRONT_CENTER);

        ultrasonicRadarPanel.append( frontCenter, frontLeftDistance, frontRightDistance, behindDistance);
    }

    private void updateRollAndPitch() {
        Point3d accelMeasureResult = controller.measureAcceleration();
        updatePitch(accelMeasureResult);
        updateRoll(accelMeasureResult);
    }
}
