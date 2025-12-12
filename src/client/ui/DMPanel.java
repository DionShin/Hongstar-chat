package client.ui;

import javax.swing.*;
import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

public class DMPanel extends JPanel {

    // ===== 상단 =====
    private JTextField targetIdField = new JTextField();
    private JButton connectBtn = new JButton("연결");

    // ===== 채팅 영역 =====
    private TextArea chatArea = new TextArea();
    private JTextField inputField = new JTextField();
    private JButton sendBtn = new JButton("전송");

    private String targetId = null; // 현재 대화 상대

    public DMPanel() {

        setLayout(new BorderLayout(10, 10));

        // ===== 상단 패널 (상대 ID 입력) =====
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(new JLabel("상대 ID:"), BorderLayout.WEST);
        topPanel.add(targetIdField, BorderLayout.CENTER);
        topPanel.add(connectBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ===== 채팅 영역 =====
        chatArea.setEditable(false);
        add(chatArea, BorderLayout.CENTER);

        // ===== 입력 영역 =====
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== 이벤트 =====
        connectBtn.addActionListener(e -> connectTarget());
        sendBtn.addActionListener(e -> sendDM());
        inputField.addActionListener(e -> sendDM());

        // ===== 서버에서 DM 수신 =====
        ClientNetwork.getInstance().onChatReceived(msg -> {
            // DM 수신은 "DM:to:from:msg" 형식
            if (!msg.startsWith("DM:")) return;

            String[] arr = msg.split(":", 4);
            if (arr.length != 4) return;

            String to   = arr[1];
            String from = arr[2];
            String text = arr[3];

            String myId = ClientNetwork.getInstance().loggedInId;

            // 나와 관련된 DM만 출력
            if (myId.equals(to) || myId.equals(from)) {
                chatArea.append("[" + from + "] " + text + "\n");
            }
        });
    }

    // ===== 상대 ID 설정 =====
    private void connectTarget() {
        String id = targetIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "상대 ID를 입력하세요");
            return;
        }

        targetId = id;
        chatArea.append(">>> [" + id + "] 님과의 1:1 채팅 시작\n");
    }

    // ===== DM 전송 =====
    private void sendDM() {
        if (targetId == null) {
            JOptionPane.showMessageDialog(this, "먼저 상대 ID를 입력하세요");
            return;
        }

        String msg = inputField.getText().trim();
        if (msg.isEmpty()) return;

        String myId = ClientNetwork.getInstance().loggedInId;

        // 내 화면에 먼저 출력
        chatArea.append("[나 → " + targetId + "] " + msg + "\n");

        // 서버로 전송
        ClientNetwork.getInstance().sendDirectMessage(targetId, msg);

        inputField.setText("");
    }
}