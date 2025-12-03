package client.ui;

import javax.swing.*;

import client.network.ClientNetwork;

import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField id_field;
    private JPasswordField pw_field;

    public LoginFrame() {
        setTitle("홍스타 로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(null);
        c.setBackground(new Color(220, 255, 255));

        // 아이콘
        ImageIcon login_img = new ImageIcon("src\\client\\ui\\example_img.png");
        Image original_img = login_img.getImage();
        Image scaled_img = original_img.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon scaled_icon = new ImageIcon(scaled_img);
        JLabel loginImg = new JLabel(scaled_icon);
        loginImg.setBounds(100, 50, 250, 250);
        c.add(loginImg);

        // ID
        JLabel id_label = new JLabel("ID: ");
        id_label.setBounds(100,300,30,30);
        c.add(id_label);
        id_field = new JTextField(20);
        id_field.setBounds(130, 300, 200, 30);
        c.add(id_field);

        // PW
        JLabel pw_label = new JLabel("PW: ");
        pw_label.setBounds(100,350,30,30);
        c.add(pw_label);
        pw_field = new JPasswordField(20);
        pw_field.setBounds(130, 350, 200, 30);
        c.add(pw_field);

        // 로그인 버튼
        JButton login_bt = new JButton("로그인");
        login_bt.setBounds(130, 400, 200, 30);
        login_bt.addActionListener(new MyActionListener());
        c.add(login_bt);

        // 회원가입 버튼
        JButton join_membership_bt = new JButton("회원가입");
        join_membership_bt.setBounds(230, 480, 100, 30);
        join_membership_bt.addActionListener(new MyActionListener());
        c.add(join_membership_bt);

        setSize(450, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton)e.getSource();

            if (b.getText().equals("로그인")) {
                String userId = id_field.getText();
                String password = new String(pw_field.getPassword());

                // 1) 빈칸 체크
                if (userId.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "ID와 비밀번호를 모두 입력하세요.",
                        "경고",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // 2) 서버에 로그인 요청
                boolean ok = ClientNetwork.getInstance().requestLogin(userId, password);
                System.out.println("[로그인 버튼] 결과 ok = " + ok);

                // 3) 결과에 따라 분기
                if (ok) {
                    JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "로그인 성공!",
                        "성공",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    // 메인 화면으로 이동
                    LoginFrame.this.dispose();
                    new MainFrame();
                } else {
                    JOptionPane.showMessageDialog(
                        LoginFrame.this,
                        "로그인 실패: ID 또는 비밀번호가 올바르지 않습니다.",
                        "실패",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            else if (b.getText().equals("회원가입")) {
                new RegisterFrame();
            }
        }
    }
}
