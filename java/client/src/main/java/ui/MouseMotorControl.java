package ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseMotorControl extends JPanel {
    private final Listener listener;
    private JLabel xyPercents;
    private boolean notifiedBegin = false;

    public static interface Listener {
        void setXPercents(int percents);
        void setYPercents(int percents);
        void end();
        void begin();
    }

    public MouseMotorControl(Listener theListener){

        this.listener = theListener;

        setMaximumSize(new Dimension(300,300));
        setMinimumSize(new Dimension(300,300));
        setPreferredSize(new Dimension(300,300));

        xyPercents = new JLabel("0%,0%");

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                if( !notifiedBegin ){
                    listener.begin();
                    notifiedBegin = true;
                }

                Point mousePoint = mouseEvent.getPoint();
                handleMouseEvent(mousePoint);
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                handleMouseEvent(mouseEvent.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                xyPercents.setText("0%,0%");
                listener.end();
                notifiedBegin = false;
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                    .addComponent(xyPercents)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addComponent(xyPercents)
        );
    }

    private void handleMouseEvent(Point mousePoint) {
        mousePoint.x = -mousePoint.x + getWidth() / 2;
        mousePoint.y = -mousePoint.y + getHeight() / 2;

        int xPercents =  (mousePoint.x * 100) / (getWidth() / 2);
        int yPercents =  (mousePoint.y * 100) / (getHeight() / 2);

        xPercents = (xPercents > 100) ? 100 : xPercents;
        yPercents = (yPercents > 100) ? 100 : yPercents;

        xPercents = (xPercents < -100) ? -100 : xPercents;
        yPercents = (yPercents < -100) ? -100 : yPercents;

        xyPercents.setText(String.format("%d%%,%d%%", xPercents, yPercents));
        listener.setXPercents(xPercents);
        listener.setYPercents(yPercents);
    }

    @Override
    public void paint(Graphics graphics){
        super.paint(graphics);

        Graphics2D g = (Graphics2D)graphics;
        g.translate(getWidth()/2, getHeight()/2);
        g.drawLine(-getWidth()/2, 0, getWidth()/2, 0);
        g.drawLine(0, -getHeight()/2, 0, getHeight()/2);
    }
}
