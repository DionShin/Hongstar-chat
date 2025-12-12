package client.network;

import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;

import common.Protocol;

import javax.swing.JOptionPane;

public class ClientNetwork {
    // 1. 서버 접속 정보 
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8080;

    //172.20.10.4

    private boolean loggedIn = false;
    private String loggedInId = null;

    public boolean isLoggedIn() {
    return loggedIn;
}   



    
    // 싱글톤 패턴 (ClientNetwork 객체를 하나만 유지)
    private static ClientNetwork instance = new ClientNetwork();
    public static ClientNetwork getInstance() {
        return instance;
    }
    private ClientNetwork() {}
     

    /*
     * 로그인 요청을 서버에 전송하고 응답을 처리합니다.
     */
    // ✅ 로그인: 성공이면 true, 실패면 false 리턴
    public boolean requestLogin(String id, String pw) {
        System.out.println("[클라] requestLogin 호출: " + id + "/" + pw);
        try (
            Socket socket = new Socket(SERVER_IP, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"))
        ) {
            // 서버에 로그인 정보 전송
            out.println(Protocol.LOGIN_REQUEST + id + ":" + pw);

            // 서버 응답 수신
            String serverResponse = in.readLine();
            System.out.println("[클라] 로그인 응답: " + serverResponse);

            if (serverResponse != null && serverResponse.startsWith(Protocol.SUCCESS_RESPONSE)) {
                return true; // 로그인 성공
            } else {
                return false; // 로그인 실패
            }

        } catch (IOException e) {
            System.out.println("[클라] 로그인 중 오류: " + e.getMessage());
            JOptionPane.showMessageDialog(
                null,
                "서버 연결에 실패했습니다. 서버가 실행 중인지 확인하세요.",
                "연결 오류",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
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

    public void requestLogout() {
    new Thread(() -> {
        try (
            Socket socket = new Socket(SERVER_IP, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            out.println(Protocol.LOGOUT_REQUEST);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "로그아웃 실패");
        }
    }).start();
}

    

    private BufferedReader listenerInput;
    private Thread listenerThread;

    public void startListener() {
        new Thread(() -> {
            try {
                Socket socket = new Socket(SERVER_IP, PORT);
                listenerInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                listenerThread = new Thread(() -> {
                    try {
                        String msg;
                        while ((msg = listenerInput.readLine()) != null) {
                            System.out.println("[수신] " + msg);
                        }
                    } catch (IOException e) {}
                });

                listenerThread.start();

            } catch (IOException e) {
                System.out.println("[Listen 연결 실패]");
            }
        }).start();
    }

    private void sendSimple(String msg) {
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println(msg);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "서버 연결 실패");
            }
        }).start();
    }

    public void requestDeleteUser(String id, String pw) {
        sendSimple(Protocol.DELETE_USER_REQUEST + id + ":" + pw);
    }

    // 메세지 송신
    public void sendChat(String chatData) {
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) {
                if (out != null) {
                    out.println(Protocol.CHAT_MESSAGE_SEND + chatData);
                }
            }
            catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, 
                        "채팅 전송 실패: 서버 연결 오류", "오류", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    public void requestUpdateUser(String id, String pw, String name, String email, String phone) {
    new Thread(() -> {
        try (
            Socket socket = new Socket(SERVER_IP, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // 패킷 형식: UPDATE_USER:id:pw:name:email:phone
            String packet = Protocol.UPDATE_USER_REQUEST
                          + id + ":" + pw + ":" + name + ":" + email + ":" + phone;

            System.out.println("[클라] UPDATE 패킷 = " + packet);

            out.println(packet);

            String response = in.readLine();
            System.out.println("[클라] UPDATE 응답 = " + response);

            SwingUtilities.invokeLater(() -> {
                if (response != null && response.startsWith(Protocol.SUCCESS_RESPONSE)) {
                    JOptionPane.showMessageDialog(null, "회원 정보 수정 완료!", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "수정 실패: 서버 오류 또는 형식 오류", "오류", JOptionPane.ERROR_MESSAGE);
                }
            });

        } catch (IOException e) {
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, "서버 연결 실패.", "연결 오류", JOptionPane.ERROR_MESSAGE)
            );
        }
    }).start();
}

}

