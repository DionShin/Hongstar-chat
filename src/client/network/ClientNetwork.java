package client.network;

import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

import common.Protocol;

import javax.swing.JOptionPane;

public class ClientNetwork {
    // 1. 서버 접속 정보 (상수)
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8080;
    
    
    // 싱글톤 패턴 (ClientNetwork 객체를 하나만 유지)
    private static ClientNetwork instance = new ClientNetwork();
    public static ClientNetwork getInstance() {
        return instance;
    }
    private ClientNetwork() {}
     

    /*
     * 로그인 요청을 서버에 전송하고 응답을 처리합니다.
     */
    public void requestLogin(String id, String pw) {
        new Thread(() -> {
            try (
                // 2. 소켓 연결 및 스트림 생성
                Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                // 3. 서버에 로그인 정보 전송 (Protocol 사용)
                out.println(Protocol.LOGIN_REQUEST + id + ":" + pw);
                
                // 4. 서버 응답 수신
                String serverResponse = in.readLine();
                
                // 5. 응답 처리 (UI 스레드에서 실행 - UI 처리는 Frame에 맡기는 것이 좋으나, 
                //    여기서는 통신 결과를 UI에 바로 전달하는 예시로 유지)
                SwingUtilities.invokeLater(() -> {
                    if (serverResponse != null && serverResponse.startsWith(Protocol.SUCCESS_RESPONSE)) {
                        String loggedInId = serverResponse.substring(Protocol.SUCCESS_RESPONSE.length());
                        JOptionPane.showMessageDialog(null, loggedInId + "님, 로그인 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                        
                        // 성공 후 메인 화면 띄우기 등의 로직 (LoginFrame에서 처리하도록 변경할 수 있음)
                        // new MainFrame(); 
                        
                    } else if (serverResponse != null && serverResponse.startsWith(Protocol.FAIL_RESPONSE)) {
                        JOptionPane.showMessageDialog(null, "로그인 실패: ID 또는 비밀번호 오류입니다.", "실패", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "서버 응답 오류 발생 또는 연결 끊김.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (UnknownHostException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "서버 IP 주소를 찾을 수 없습니다.", "연결 오류", JOptionPane.ERROR_MESSAGE));
            } catch (IOException e) {
                 SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "서버 연결에 실패했습니다. 서버가 실행 중인지 확인하세요.", "연결 오류", JOptionPane.ERROR_MESSAGE));
            }
        }).start();

    }

    public void requestJoin(String joinData) {
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT); // 서버 접속 정보 재사용
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                // 1. 서버에 회원가입 정보 전송 (형식: JOIN:ID:PW:NAME:...)
                out.println(Protocol.JOIN_REQUEST + joinData); 
                
                // 2. 서버 응답 수신
                String serverResponse = in.readLine();
                
                // 3. 응답 처리 (UI 스레드에서 실행)
                SwingUtilities.invokeLater(() -> {
                    if (serverResponse != null && serverResponse.startsWith(Protocol.SUCCESS_RESPONSE)) {
                        JOptionPane.showMessageDialog(null, "🎉 회원가입 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                        // 성공 후 로그인 화면으로 돌아가는 로직 추가 가능 (RegisterFrame 닫기)
                        
                    } else if (serverResponse != null && serverResponse.startsWith(Protocol.FAIL_RESPONSE)) {
                        String failReason = serverResponse.substring(Protocol.FAIL_RESPONSE.length());
                        JOptionPane.showMessageDialog(null, "회원가입 실패: " + failReason, "실패", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "서버 응답 오류 발생.", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (IOException e) {
                 SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "서버 연결에 실패했습니다. 서버가 실행 중인지 확인하세요.", "연결 오류", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}