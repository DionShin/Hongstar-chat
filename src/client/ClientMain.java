package client;

import javax.swing.SwingUtilities;

import client.ui.LoginFrame;

public class ClientMain {

    public static void main(String[] args) {
        
		SwingUtilities.invokeLater(() -> {
            new LoginFrame(); // LoginFrame 인스턴스 생성 및 화면 표시
        });
	}
}