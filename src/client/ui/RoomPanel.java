package client.ui;

import javax.swing.*;

import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

public class RoomPanel extends JPanel{

    JLabel information = new JLabel("information");
    TextArea text_area = new TextArea();
    JPanel input_panel = new JPanel();
    JTextField input_field = new JTextField();
    JButton input_button = new JButton("전송");

    public RoomPanel(){
        
    	setLayout(new BorderLayout(10, 10));
        
        // 정보 창
        information.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        information.setPreferredSize(new Dimension(0, 70));
        add(information, BorderLayout.NORTH);

        // 채팅 화면
        text_area.setEditable(false);
        new JScrollPane(text_area);
        add(text_area, BorderLayout.CENTER);
        
        // 입력 패널
        input_panel.setPreferredSize(new Dimension(0, 100));
        input_panel.setLayout(new BorderLayout(5, 5));
        add(input_panel, BorderLayout.SOUTH);

        //"전송" 버튼
        input_button.setPreferredSize(new Dimension(100, 0));
        input_button.addActionListener(new MyActionListener()); // 액션리스너 enter입력가능
        
        input_panel.add(input_field, BorderLayout.CENTER);
        input_panel.add(input_button, BorderLayout.EAST);
        
        input_field.addActionListener(new MyActionListener()); // 액션리스너 "전송" 버튼 클릭


        setBackground(new Color(210, 245, 255));
        input_panel.setBackground(new Color(210, 245, 255));

    }
    private class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            if (e.getSource() == input_field) {
                text_area.append("[내 이름]: " + input_field.getText()+"\n");
                // ClientNetwork.getInstance().sendChat(input_field.getText());  // 서버 전송
                input_field.setText("");
                
            }
            else if (e.getSource() == input_button) {
                // JButton b = (JButton)e.getSource();
                text_area.append("[내 이름]: " + input_field.getText()+"\n");
                // ClientNetwork.getInstance().sendChat(input_field.getText()); // 서버 전송
                input_field.setText("");
            }
        }
    }
}
