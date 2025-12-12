package client.ui;

import javax.swing.*;
import client.network.ClientNetwork;
import java.awt.*;
import java.awt.event.*;

public class SettingPanel extends JPanel{
    JButton user_edit_bt;
    JButton logout_bt;
    JButton leave_bt;
    public SettingPanel(){
        
    	setLayout(null);

        user_edit_bt = new JButton("정보 수정");
        logout_bt = new JButton("로그아웃");
        leave_bt = new JButton("회원 탈퇴");
        
        user_edit_bt.setBounds(112, 80, 225, 30);
        logout_bt.setBounds(112, 180, 225, 30);
        leave_bt.setBounds(112, 280, 225, 30);

        add(user_edit_bt);
        add(logout_bt);
        add(leave_bt);

        user_edit_bt.addActionListener(new MyActionListener());
        logout_bt.addActionListener(new MyActionListener());
        leave_bt.addActionListener(new MyActionListener());

        setBackground(new Color(190, 225, 255));

    }
    private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == user_edit_bt) {
    // 현재 로그인된 ID 전달 (네 프로젝트에 login 저장 방식에 따라 수정 가능)
    String id = JOptionPane.showInputDialog("수정할 사용자 ID 입력");

    new UserEditFrame(id);
}else if (e.getSource() == leave_bt) {
            String id = JOptionPane.showInputDialog("ID 입력");
            String pw = JOptionPane.showInputDialog("PW 입력");
            ClientNetwork.getInstance().requestDeleteUser(id, pw);
        }else if (e.getSource() == logout_bt) {
        // 메인 화면 닫기
        SwingUtilities.getWindowAncestor(SettingPanel.this).dispose();
        // 로그인 화면 다시 띄우기
        new LoginFrame();
        }
    }
}

}