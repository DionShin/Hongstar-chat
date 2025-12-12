package client.ui;

import javax.swing.*;
import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

public class ChatPanel extends JPanel {

    JLabel information = new JLabel("채팅");
    TextArea text_area = new TextArea();
    JPanel input_panel = new JPanel();
    JTextField input_field = new JTextField();
    JButton input_button = new JButton("전송");

    public ChatPanel() {

        setLayout(new BorderLayout(10, 10));

        information.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        information.setPreferredSize(new Dimension(0, 70));
        add(information, BorderLayout.NORTH);

        text_area.setEditable(false);
        add(new JScrollPane(text_area), BorderLayout.CENTER);

        input_panel.setPreferredSize(new Dimension(0, 100));
        input_panel.setLayout(new BorderLayout(5, 5));
        add(input_panel, BorderLayout.SOUTH);

        input_button.setPreferredSize(new Dimension(100, 0));
        input_panel.add(input_field, BorderLayout.CENTER);
        input_panel.add(input_button, BorderLayout.EAST);

        input_button.addActionListener(new MyActionListener());
        input_field.addActionListener(new MyActionListener());

        setBackground(new Color(210, 245, 255));
        input_panel.setBackground(new Color(210, 245, 255));

        // ✅ 서버에서 오는 전체채팅 수신 바인딩
        ClientNetwork.getInstance().onChatReceived(full -> {
            // full = "sender:msg"
            String[] p = full.split(":", 2);
            if (p.length != 2) return;

            String sender = p[0];
            String msg = p[1];

            String myId = ClientNetwork.getInstance().getLoggedInId();
            if (myId != null && myId.equals(sender)) {
                // 내가 보낸 건 서버에서 다시 오면 중복이라 스킵(원하면 지워도 됨)
                return;
            }

            text_area.append("[" + sender + "]: " + msg + "\n");
        });
    }

    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String msg = input_field.getText().trim();
            if (msg.isEmpty()) return;

            String myId = ClientNetwork.getInstance().getLoggedInId();
            if (myId == null) myId = "나";

            text_area.append("[" + myId + "]: " + msg + "\n");
            ClientNetwork.getInstance().sendChat(msg);

            input_field.setText("");
        }
    }
}