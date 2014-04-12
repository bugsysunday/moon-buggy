package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class NetworkToolsPanel extends JPanel {

    private final JButton connect;
    private final JTextField ipAddress;
    private final JTextArea log;
    private final StringBuffer logContent;
    private final Listener listener;
    private final JScrollPane logScrollPane;

    public static interface Listener {
        void connect(String address);
    }

    public NetworkToolsPanel(Listener networkToolsListener){
        connect = new JButton("Connect");
        connect.setPreferredSize(new Dimension(50,20));
        connect.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                listener.connect(ipAddress.getText());
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        ipAddress = new JTextField("localhost");
        ipAddress.setPreferredSize(new Dimension(70, 20));

        log = new JTextArea();
        log.setEditable(false);

        logScrollPane= new JScrollPane(log);
        logScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setPreferredSize(new Dimension(200, 290));

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                    .addGroup(
                            layout.createSequentialGroup()
                                .addComponent(connect)
                                .addComponent(ipAddress)
                            )
                    .addComponent(logScrollPane)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(
                            layout.createParallelGroup()
                                .addComponent(connect)
                                .addComponent(ipAddress)
                    )
                    .addComponent(logScrollPane)
        );

        logContent = new StringBuffer();

        this.listener = networkToolsListener;
    }

    public void appendLog(String text){
        logContent.append(text + "\r\n");
        log.setText(logContent.toString());
    }

}
