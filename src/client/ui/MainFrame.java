package client.ui;


// import client.network.ClientNetwork;

import javax.swing.*;

public class MainFrame extends JFrame{
    JTabbedPane tp;
    public MainFrame(){
        setTitle("메인 화면");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tp = new JTabbedPane();
        setTabs();

        setSize(450, 700);
        setLocation(100, 100);
    	setVisible(true);
    }
 
    private void setTabs() {
        
        // 1) 사진판 (PhotoManager와 통신)
        JPanel photoPanel = new PhotoPanel(); // PhotoPanel.java
        tp.addTab("사진판", photoPanel); 
        
        // 2) 1:1 채팅 및 친구 목록 (ChatManager와 통신)
        JPanel chatPanel = new ChatPanel(); // ChatPanel.java
        tp.addTab("채팅", chatPanel); 
        
        // 3) 그룹 채팅방 목록 (RoomManager와 통신)
        JPanel roomPanel = new RoomPanel(); // RoomPanel.java
        tp.addTab("그룹 채팅", roomPanel);

        // 4) 설정 탭 (유저 수정, 삭제 등)
        JPanel settingPanel = new SettingPanel(); //SettingPanel.java
        tp.addTab("설정", settingPanel);
        add(tp);
    }
    
    //public static void main(String[] args) { new MainFrame(); } // 테스트용
}
