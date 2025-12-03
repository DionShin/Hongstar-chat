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
            String id = JOptionPane.showInputDialog("ID 입력");
            String pw = JOptionPane.showInputDialog("PW 입력");
            String name = JOptionPane.showInputDialog("이름 입력");
            String gender = JOptionPane.showInputDialog("성별 0/1 입력");
            String birth = JOptionPane.showInputDialog("YYYY-MM-DD");
            String email = JOptionPane.showInputDialog("Email");
            String phone = JOptionPane.showInputDialog("Phone");

            String updateData = id+":"+pw+":"+name+":"+gender+":"+birth+":"+email+":"+phone;
            ClientNetwork.getInstance().requestUpdateUser(updateData);

        } else if (e.getSource() == logout_bt) {
            ClientNetwork.getInstance().requestLogout();
            JOptionPane.showMessageDialog(null, "로그아웃 완료. 앱을 재시작하세요.");

        } else if (e.getSource() == leave_bt) {
            String id = JOptionPane.showInputDialog("ID 입력");
            String pw = JOptionPane.showInputDialog("PW 입력");
            ClientNetwork.getInstance().requestDeleteUser(id, pw);
        }
    }
}

}