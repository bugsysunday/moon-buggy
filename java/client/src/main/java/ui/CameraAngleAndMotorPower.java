package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CameraAngleAndMotorPower extends JPanel {

    public static interface Listener{
        void setMotorPower(int value);
        void setCameraAngle(int angle);
        void forward();
        void backward();
        void turnLeft();
        void turnRight();
        void stop();
        void setYaw(int value);
        void turnOnLights();
        void turnOfLights();
    }

    private final Listener listener;
    private final JSlider motorPower;
    private final JSlider cameraAngle;
    private final JLabel motorPowerValue;
    private final JLabel cameraAngleValue;
    private final JButton forward;
    private final JButton backward;
    private final JButton turnLeft;
    private final JButton turnRight;
    private final JSlider yaw;
    private final JLabel yawValue;
    private final JCheckBox lights;


    public CameraAngleAndMotorPower(Listener theListener){
        this.listener = theListener;

        motorPower = new JSlider();
        motorPower.setMaximum(100);
        motorPower.setMinimum(0);
        motorPower.setValue(0);
        motorPower.setPreferredSize(new Dimension(100, 20));
        motorPower.setMaximumSize(new Dimension(350, 20));

        motorPower.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                motorPowerValue.setText(Integer.toString(motorPower.getValue()) + "%");
                listener.setMotorPower(motorPower.getValue());
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        motorPowerValue = new JLabel("0%");
        motorPowerValue.setPreferredSize(new Dimension(40, 20));
        motorPowerValue.setMinimumSize(new Dimension(40, 20));

        cameraAngle = new JSlider();
        cameraAngle.setMaximum(180);
        cameraAngle.setMinimum(0);
        cameraAngle.setValue(0);
        cameraAngle.setPreferredSize(new Dimension(100, 20));
        cameraAngle.setMaximumSize(new Dimension(350, 20));

        cameraAngle.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                cameraAngleValue.setText(Integer.toString(cameraAngle.getValue()));
                listener.setCameraAngle(cameraAngle.getValue());
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        cameraAngleValue = new JLabel("0");
        cameraAngleValue.setPreferredSize(new Dimension(40, 20));
        cameraAngleValue.setMinimumSize(new Dimension(40, 20));

        forward = new JButton("Forward");
        forward.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                listener.forward();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                listener.stop();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        backward = new JButton("Backward");
        backward.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                listener.backward();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                listener.stop();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
        turnLeft = new JButton("Left");
        turnLeft.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                listener.turnLeft();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                listener.stop();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        turnRight = new JButton("Right");
        turnRight.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                listener.turnRight();
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                listener.stop();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        yaw = new JSlider();
        yaw.setMaximum(100);
        yaw.setMinimum(0);
        yaw.setValue(0);
        yaw.setPreferredSize(new Dimension(100, 20));
        yaw.setMaximumSize(new Dimension(350, 20));

        yawValue = new JLabel("0");
        yawValue.setPreferredSize(new Dimension(40, 20));
        yawValue.setMinimumSize(new Dimension(40, 20));

        yaw.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                yawValue.setText(Integer.toString(yaw.getValue()));
                listener.setYaw(yaw.getValue());
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        lights = new JCheckBox();
        lights.setText("Lights");
        lights.setPreferredSize(new Dimension(40,20));
        lights.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if(lights.isSelected()){
                    listener.turnOnLights();
                }else{
                    listener.turnOfLights();
                }
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);


        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(motorPower)
                                        .addComponent(motorPowerValue)
                        )
                        .addGroup(
                                layout.createSequentialGroup()
                                .addComponent(cameraAngle)
                                .addComponent(cameraAngleValue)
                        )
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(yaw)
                                        .addComponent(yawValue)
                        )
                        .addComponent(forward)
                        .addGroup(
                                layout.createSequentialGroup()
                                    .addComponent(turnLeft)
                                    .addComponent(backward)
                                    .addComponent(turnRight)
                                    .addComponent(lights)
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(
                            layout.createParallelGroup()
                                    .addComponent(motorPower)
                                    .addComponent(motorPowerValue)
                    )
                    .addGroup(
                            layout.createParallelGroup()
                                .addComponent(cameraAngle)
                                .addComponent(cameraAngleValue)
                    )
                    .addGroup(
                            layout.createParallelGroup()
                                    .addComponent(yaw)
                                    .addComponent(yawValue)
                    )
                    .addComponent(forward)
                    .addGroup(
                            layout.createParallelGroup()
                                    .addComponent(turnLeft)
                                    .addComponent(backward)
                                    .addComponent(turnRight)
                                    .addComponent(lights)
                    )
        );
    }
}
