package client.ui;

import javax.swing.*;
import java.awt.*;
import client.network.ClientNetwork;

public class UserEditFrame extends JFrame {

    private JTextField txtId;
    private JPasswordField txtPw;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;

    public UserEditFrame(String currentId) {

        setTitle("회원 정보 수정");
        setSize(350, 300);
        setLayout(new GridLayout(6, 2, 5, 5));
        setLocationRelativeTo(null);

        add(new JLabel("ID"));
        txtId = new JTextField(currentId);   // 수정 시 자동 입력되도록
        add(txtId);

        add(new JLabel("비밀번호"));
        txtPw = new JPasswordField();
        add(txtPw);

        add(new JLabel("이름"));
        txtName = new JTextField();
        add(txtName);

        add(new JLabel("이메일"));
        txtEmail = new JTextField();
        add(txtEmail);

        add(new JLabel("전화번호"));
        txtPhone = new JTextField();
        add(txtPhone);

        JButton btnSave = new JButton("수정 완료");
        add(btnSave);

        JButton btnCancel = new JButton("취소");
        add(btnCancel);

        btnSave.addActionListener(e -> {
            String id    = txtId.getText();
            String pw    = new String(txtPw.getPassword());
            String name  = txtName.getText();
            String email = txtEmail.getText();
            String phone = txtPhone.getText();

            ClientNetwork.getInstance().requestUpdateUser(id, pw, name, email, phone);

            dispose();
        });

        btnCancel.addActionListener(e -> dispose());

        setVisible(true);
    }
}