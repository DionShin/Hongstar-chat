package client.ui;

import javax.swing.*;

import client.network.ClientNetwork;

import java.awt.*;
import java.awt.event.*;
// import java.io.*;
// import java.net.*;

public class LoginFrame extends JFrame {
    
    private JTextField id_field;
	private JPasswordField pw_field;

    public LoginFrame() {
    	setTitle("홍스타 로그인");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	Container c = getContentPane();
    	c.setLayout(null);
    	
    	c.setBackground(new Color(220, 255, 255));
    	
    	//로그인 화면 아이콘
    	ImageIcon login_img = new ImageIcon("src\\client\\ui\\example_img.png");
    	
    	//아이콘 JLabel로 원하는 위치 크기 조정
    	Image original_img = login_img.getImage();
        Image scaled_img = original_img.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon scaled_icon = new ImageIcon(scaled_img);
        JLabel loginImg = new JLabel(scaled_icon);
        loginImg.setBounds(100, 50, 250, 250);
        c.add(loginImg);
    	
        //아이디
    	JLabel id_label = new JLabel("ID: ");
    	id_label.setBounds(100,300,30,30);
    	c.add(id_label);
    	id_field = new JTextField(20);
    	id_field.setBounds(130, 300, 200, 30);
    	c.add(id_field);
    	
    	//비밀번호
    	JLabel pw_label = new JLabel("PW: ");
    	pw_label.setBounds(100,350,30,30);
    	c.add(pw_label);
    	pw_field = new JPasswordField(20);  //비밀번호는 안보이게
    	pw_field.setBounds(130, 350, 200, 30);
    	c.add(pw_field);
    	
    	//로그인 버튼
    	JButton login_bt = new JButton("로그인");
    	login_bt.setBounds(130, 400, 200, 30);
    	login_bt.addActionListener(new MyActionListener());
    	c.add(login_bt);
    	
    	//회원가입 버튼
    	JButton join_membership_bt = new JButton("회원가입");
    	join_membership_bt.setBounds(230, 480, 100, 30);
    	join_membership_bt.addActionListener(new MyActionListener());
    	c.add(join_membership_bt);
    	
    	// 사이즈
    	setSize(450, 700);
    	setVisible(true);
    }

    // 액션 리스너
    private class MyActionListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		JButton b = (JButton)e.getSource();
    		String userId = id_field.getText();
    		String password = new String(pw_field.getPassword());
    		
    		if (b.getText().equals("로그인")) { // 로그인을 누르면
    			/* 
				// 아이디나 비밀번호가 비어있으면 경고
        		if (userId.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "ID와 비밀번호를 모두 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 네트워크 클래스의 인스턴스를 가져와서 서버 연결 요청
				ClientNetwork.getInstance().requestLogin(userId, password);
				*/

				// 요청 받으면 메인 프레임 열기
				LoginFrame.this.dispose(); // 로그인 창 닫기
				new MainFrame();

				//ClientNetwork clientnetwork = new ClientNetwork();
				//clientnetwork.requestLogin(userId, password);

        		//requestLogin(userId, password);

    		}
    		//회원가입
    		else if (b.getText().equals("회원가입")){
    			SwingUtilities.invokeLater(() -> {
                    new RegisterFrame();
                });
    		}
    	}
    }
}