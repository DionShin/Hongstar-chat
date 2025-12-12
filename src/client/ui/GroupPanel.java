package client.ui;

import javax.swing.*;
import client.network.ClientNetwork;
import java.awt.*;

public class GroupPanel extends JPanel {

    JTextField roomField = new JTextField("room1");
    JButton joinBtn = new JButton("입장");

    TextArea groupArea = new TextArea();
    JTextField groupInput = new JTextField();
    JButton groupSendBtn = new JButton("전송");

    String currentRoom = null;

    public GroupPanel() {

        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(new JLabel("그룹 이름:"), BorderLayout.WEST);
        topPanel.add(roomField, BorderLayout.CENTER);
        topPanel.add(joinBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        groupArea.setEditable(false);
        add(groupArea, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(groupInput, BorderLayout.CENTER);
        bottomPanel.add(groupSendBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        joinBtn.addActionListener(e -> joinRoom());
        groupSendBtn.addActionListener(e -> sendGroupMessage());
        groupInput.addActionListener(e -> sendGroupMessage());

        setBackground(new Color(230, 240, 255));

        // ✅ 서버에서 그룹 메시지 수신
        ClientNetwork.getInstance().onGroupChatReceived((room, sender, text) -> {
            if (currentRoom != null && currentRoom.equals(room)) {
                String myId = ClientNetwork.getInstance().getLoggedInId();
                if (myId != null && myId.equals(sender)) return; // 중복 방지
                groupArea.append("[" + sender + "]: " + text + "\n");
            }
        });
    }

    private void joinRoom() {
        String room = roomField.getText().trim();
        if (room.isEmpty()) return;

        currentRoom = room;
        groupArea.append(">>> 그룹 [" + room + "] 에 입장했습니다.\n");
        ClientNetwork.getInstance().joinGroup(room);
    }

    private void sendGroupMessage() {
        if (currentRoom == null) {
            JOptionPane.showMessageDialog(this, "먼저 방에 입장하세요!");
            return;
        }

        String msg = groupInput.getText().trim();
        if (msg.isEmpty()) return;

        String myId = ClientNetwork.getInstance().getLoggedInId();
        if (myId == null) myId = "나";

        groupArea.append("[" + myId + "]: " + msg + "\n");
        ClientNetwork.getInstance().sendGroupChat(currentRoom, msg);

        groupInput.setText("");
    }
}