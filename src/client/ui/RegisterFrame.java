package client.ui;

import javax.swing.*;

import client.network.ClientNetwork;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// import java.sql.*;

public class RegisterFrame extends JFrame{
    
    JTextField id_field, name_field, birth_field, email_field, phone_field;
    JPasswordField pw_field, re_pw_field;
    JRadioButton man, woman;
    public RegisterFrame(){
    	setTitle("회원가입");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //회원가입 창만 닫기
    	// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 전체 닫기
    	Container c = getContentPane();
    	GridLayout grid = new GridLayout(12, 2);
    	grid.setVgap(20);
    	
    	c.setLayout(grid);
    	c.setBackground(new Color(224, 224, 224));
    	
    	Font label_font = new Font("Dialog", Font.BOLD, 20);
    	
    	// (0,0)
    	JLabel join_label = new JLabel("회원가입");
    	join_label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    	join_label.setFont(label_font);
    	c.add(join_label);
    	// (0,1) ~ (1,1)
    	for (int i=0; i<3;i++) { c.add(new JLabel("")); }
    	
    	//아이디 (2,0)~(2,1)
    	JLabel id_label = new JLabel("아이디 : ");
    	id_label.setFont(label_font);
    	c.add(id_label);
    	id_field = new JTextField(20);
    	c.add(id_field);
    	
    	//비밀번호 3
    	JLabel pw_label = new JLabel("비밀번호 : ");
    	pw_label.setFont(label_font);
    	c.add(pw_label);
    	pw_field = new JPasswordField(20);
    	c.add(pw_field);
    	
    	//비밀번호 확인 4
    	JLabel re_pw_label = new JLabel("비밀번호 확인 : ");
    	re_pw_label.setFont(label_font);
    	c.add(re_pw_label);
    	re_pw_field = new JPasswordField(20);
    	c.add(re_pw_field);
    	
    	//이름 5
    	JLabel name_label = new JLabel("이름 : ");
    	name_label.setFont(label_font);
    	c.add(name_label);
    	name_field = new JTextField(20);
    	c.add(name_field);
    	
    	//성별 6
    	JLabel gender_label = new JLabel("성별 : ");
    	gender_label.setFont(label_font);
    	c.add(gender_label);
    	man = new JRadioButton("남성");
    	woman = new JRadioButton("여성");
    	man.setBackground(new Color(224, 224, 224));
    	woman.setBackground(new Color(224, 224, 224));
    	// 버튼을 하나로 묶어 하나만 선택되게 합니다.
    	ButtonGroup gender_group = new ButtonGroup();
    	gender_group.add(man);
    	gender_group.add(woman);
    	// GridLayout의 하나의 셀에 이 두 버튼을 담기 위한 JPanel 생성
    	JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	genderPanel.setBackground(new Color(224, 224, 224));
    	genderPanel.add(man);
    	genderPanel.add(woman);
    	c.add(genderPanel);
    	
    	//생년월일 7
    	JLabel birth_label = new JLabel("생년월일(8자) : ");
    	birth_label.setFont(label_font);
    	c.add(birth_label);
    	birth_field = new JTextField(20);
    	c.add(birth_field);
    	
    	//이메일 8
    	JLabel email_label = new JLabel("이메일 : ");
    	email_label.setFont(label_font);
    	c.add(email_label);
    	email_field = new JTextField(20);
    	c.add(email_field);
    	
    	//휴대전화 9
    	JLabel phone_label = new JLabel("휴대폰 번호(11자) : ");
    	phone_label.setFont(label_font);
    	c.add(phone_label);
    	phone_field = new JTextField(20);
    	c.add(phone_field);
    	//공백 
    	for (int i=0; i<3;i++) { c.add(new JLabel("")); }
    	
    	//가입하기 버튼 11
    	JButton join_membership_button = new JButton("가입하기");
    	join_membership_button.setFont(label_font);
    	c.add(join_membership_button);
    	//회원가입버튼 액션리스너
    	join_membership_button.addActionListener(new JoinMembershipActionListener());
    	
    	setSize(450, 700);
    	setLocationRelativeTo(null);
    	setVisible(true);
    }

    private class JoinMembershipActionListener implements ActionListener{
    	@Override
    	public void actionPerformed(ActionEvent e) {
            //UI 필드에서 사용자 입력 값 가져오기
            String id = id_field.getText();
            String password = new String(pw_field.getPassword());
            String re_password = new String(re_pw_field.getPassword());
            
            if (id.equals("") || password.equals("") || re_password.equals("")) {
            	JOptionPane.showMessageDialog(null, "아이디나 비밀번호가 입력되지 않았습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!password.equals(re_password)) {
                // 비밀번호가 일치하지 않을 때
                JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String name = name_field.getText();

            int genderValue = 0; // 기본값 (남성: 0, 여성: 1)
            if (woman.isSelected()) { 
            	genderValue = 1;
            }
            // 생년월일 DB Date타입으로 변환
            String birth = birth_field.getText();
            if (birth.length()!=8) {
            	JOptionPane.showMessageDialog(null, "생년월일은 8자리의 숫자로 입력해야합니다. 예) 19970123", "오류", JOptionPane.ERROR_MESSAGE);
        	    return;
            }
			// 결과 예시 ("1997-01-23")
            String format_birth = birth.substring(0, 4) + "-" + 
                    			  birth.substring(4, 6) + "-" + 
                    			  birth.substring(6, 8);
            
            String email = email_field.getText();
            String phone = phone_field.getText();
            

			// 1. 서버로 전송할 모든 데이터를 프로토콜에 맞게 문자열로 결합
            String joinData = id + ":" + password + ":" + name + ":" + genderValue + ":" + format_birth + ":" + email + ":" + phone;
            
            // 2. ClientNetwork을 통해 서버에 요청 전송
            ClientNetwork.getInstance().requestJoin(joinData);

			/*
            //SQL 쿼리 작성 (INSERT INTO user_table ...)
            String sql = "INSERT INTO "
            		+ "user_table(id, password, username, gender, birth, email, phone) "
            		+ "values(?, ?, ?, ?, ?, ?, ?)";
                
           try {
        		Connection conn = DriverManager.getConnection(URL, USER, PASS);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                
                pstmt.setString(1, id);
                pstmt.setString(2, password);
                pstmt.setString(3, name);
                pstmt.setInt(4, genderValue);
                pstmt.setString(5, format_birth);
                pstmt.setString(6, email);
                pstmt.setString(7, phone);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "회원가입 성공!");
                    RegisterFrame.this.dispose();
                } 
                else {
                    JOptionPane.showMessageDialog(null, "회원가입 실패: 데이터 삽입 오류", "경고", JOptionPane.WARNING_MESSAGE);
                }
                
            } 
           	catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "DB 처리 오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
			*/
    	}
    }
}