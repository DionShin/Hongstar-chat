package client.ui;

import javax.swing.*;
import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

public class RoomPanel extends JPanel {

    JLabel information = new JLabel("information");
    TextArea text_area = new TextArea();
    JPanel input_panel = new JPanel();
    JTextField input_field = new JTextField();
    JButton input_button = new JButton("전송");

    public RoomPanel() {

        setLayout(new BorderLayout(10, 10));

        // 정보 창
        information.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        information.setPreferredSize(new Dimension(0, 70));
        add(information, BorderLayout.NORTH);

        // 채팅 화면
        text_area.setEditable(false);
        add(text_area, BorderLayout.CENTER);

        // 입력 패널
        input_panel.setPreferredSize(new Dimension(0, 100));
        input_panel.setLayout(new BorderLayout(5, 5));
        add(input_panel, BorderLayout.SOUTH);

        input_button.setPreferredSize(new Dimension(100, 0));
        input_panel.add(input_field, BorderLayout.CENTER);
        input_panel.add(input_button, BorderLayout.EAST);

        // 액션 리스너 등록 (엔터 / 버튼 공통)
        MyActionListener listener = new MyActionListener();
        input_button.addActionListener(listener);
        input_field.addActionListener(listener);

        setBackground(new Color(210, 245, 255));
        input_panel.setBackground(new Color(210, 245, 255));

        // 🔥 서버에서 오는 채팅 메시지 수신해서 화면에 출력
        //   서버에서 보내는 형식: "CHAT:아이디:내용"
        //   ClientNetwork.startListener()에서 "CHAT:" 떼고 "아이디:내용"만 넘겨줬다고 가정
        ClientNetwork.getInstance().onChatReceived(fullMsg -> {
            // fullMsg 형식: "아이디:내용"
            String line = fullMsg;

            String[] parts = fullMsg.split(":", 2); // 앞에서 한 번만 자르기
            if (parts.length == 2) {
                String sender = parts[0];
                String text   = parts[1];
                line = "[" + sender + "] " + text;
            }

            text_area.append(line + "\n");
        });
    }

    // ===== 입력 필드 / 버튼 액션 =====
    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String text = input_field.getText().trim();
            if (text.isEmpty()) return;

            // 🔥 서버로 메시지 전송
            // 실제 패킷은 ClientNetwork.sendChat() 내부에서
            // "CHAT_SEND:로그인아이디:내용" 형식으로 만들어서 보냄
            ClientNetwork.getInstance().sendChat(text);

            // 입력창 비우기 (내가 보낸 메시지는 서버 브로드캐스트로 다시 돌아와서 위에서 append 됨)
            input_field.setText("");
        }
    }
}