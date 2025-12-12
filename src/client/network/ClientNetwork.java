package client.network;

import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import common.Protocol;

public class ClientNetwork {

    private static final String SERVER_IP = "localhost"; // 필요 시 변경
    private static final int PORT = 8080;

    private boolean loggedIn = false;
    public String loggedInId = null; // 기존 코드 호환 위해 public 유지

    public boolean isLoggedIn() { return loggedIn; }
    public String getLoggedInId() { return loggedInId; }

    // ======== 싱글톤 ========
    private static ClientNetwork instance = new ClientNetwork();
    public static ClientNetwork getInstance() { return instance; }
    private ClientNetwork() {}

    // ======== (중요) 로그인 후 유지할 소켓/스트림 ========
    private Socket liveSocket;
    private PrintWriter liveOut;
    private BufferedReader liveIn;
    private Thread listenerThread;

    // ========= 채팅 listener =========
    public interface ChatListener { void onMessage(String msg); }
    public interface GroupChatListener { void onGroupMessage(String room, String sender, String msg); }
    public interface DirectMessageListener { void onDM(String toId, String fromId, String msg); }

    private ChatListener chatListener;
    private GroupChatListener groupChatListener;
    private DirectMessageListener dmListener;

    // 기존 패널들이 부르는 이름 맞춤
    public void onChatReceived(ChatListener listener) { this.chatListener = listener; }
    public void onGroupChatReceived(GroupChatListener listener) { this.groupChatListener = listener; }
    public void onDirectMessageReceived(DirectMessageListener listener) { this.dmListener = listener; }

    // ================= 로그인 (여기서부터 실시간 소켓 유지) =================
    public boolean requestLogin(String id, String pw) {
        System.out.println("[클라] requestLogin: " + id);

        try {
            // liveSocket 오픈
            liveSocket = new Socket(SERVER_IP, PORT);
            liveOut = new PrintWriter(new OutputStreamWriter(liveSocket.getOutputStream(), "UTF-8"), true);
            liveIn  = new BufferedReader(new InputStreamReader(liveSocket.getInputStream(), "UTF-8"));

            // 로그인 패킷
            liveOut.println(Protocol.LOGIN_REQUEST + id + ":" + pw);

            String resp = liveIn.readLine();
            System.out.println("[클라] 로그인 응답: " + resp);

            if (resp != null && resp.startsWith(Protocol.SUCCESS_RESPONSE)) {
                loggedIn = true;
                loggedInId = id;

                startListener(); // ✅ 같은 소켓에서 계속 수신
                return true;
            }

            // 로그인 실패면 소켓 닫기
            closeLiveConnection();
            return false;

        } catch (IOException e) {
            closeLiveConnection();
            JOptionPane.showMessageDialog(null,
                "서버 연결에 실패했습니다. 서버가 실행 중인지 확인하세요.",
                "연결 오류",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    // ================= 회원가입 (기존 유지) =================
    public void requestJoin(String joinData) {
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))
            ) {
                out.println(Protocol.JOIN_REQUEST + joinData);
                String resp = in.readLine();

                SwingUtilities.invokeLater(() -> {
                    if (resp != null && resp.startsWith(Protocol.SUCCESS_RESPONSE)) {
                        JOptionPane.showMessageDialog(null, "🎉 회원가입 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "회원가입 실패", "실패", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버 연결 실패", "경고", JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }

    // ================= 로그아웃 (liveSocket 닫기) =================
    public void requestLogout() {
        new Thread(() -> {
            try {
                if (liveOut != null) {
                    liveOut.println(Protocol.LOGOUT_REQUEST);
                }
            } catch (Exception ignored) {}
            closeLiveConnection();
        }).start();
    }

    // ================= 회원정보 수정 (기존 유지) =================
    public void requestUpdateUser(String id, String pw, String name, String email, String phone) {
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))
            ) {
                String packet = Protocol.UPDATE_USER_REQUEST
                        + id + ":" + pw + ":" + name + ":" + email + ":" + phone;

                out.println(packet);

                String response = in.readLine();

                SwingUtilities.invokeLater(() -> {
                    if (response != null && response.startsWith(Protocol.SUCCESS_RESPONSE)) {
                        JOptionPane.showMessageDialog(null, "회원 정보 수정 완료!", "성공", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "수정 실패", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버 연결 실패.", "연결 오류", JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }

    // ================= 회원탈퇴 (기존 유지) =================
    public void requestDeleteUser(String id, String pw) {
        new Thread(() -> {
            try (
                Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))
            ) {
                out.println(Protocol.DELETE_USER_REQUEST + id + ":" + pw);
                String response = in.readLine();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, response));

            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, "서버 연결 실패", "오류", JOptionPane.ERROR_MESSAGE)
                );
            }
        }).start();
    }

    // ================== 실시간 수신 스레드 (liveSocket) ==================
    private void startListener() {
        if (listenerThread != null && listenerThread.isAlive()) return;

        listenerThread = new Thread(() -> {
            try {
                String msg;
                while (liveIn != null && (msg = liveIn.readLine()) != null) {
                    System.out.println("[수신] " + msg);

                    // 전체 채팅 브로드캐스트
                    if (msg.startsWith(Protocol.CHAT_BROADCAST)) {
                        String body = msg.substring(Protocol.CHAT_BROADCAST.length()); // "sender:message"
                        if (chatListener != null) {
                            String finalBody = body;
                            SwingUtilities.invokeLater(() -> chatListener.onMessage(finalBody));
                        }
                    }

                    // 그룹 채팅
                    else if (msg.startsWith("GROUP:")) {
                        String[] arr = msg.split(":", 4); // GROUP:room:sender:msg
                        if (arr.length == 4 && groupChatListener != null) {
                            String room = arr[1], sender = arr[2], text = arr[3];
                            SwingUtilities.invokeLater(() -> groupChatListener.onGroupMessage(room, sender, text));
                        }
                    }

                    // 1:1 DM
                    else if (msg.startsWith(Protocol.DIRECT_MESSAGE_PREFIX)) {
                        // DM:toId:fromId:msg
                        String body = msg.substring(Protocol.DIRECT_MESSAGE_PREFIX.length());
                        String[] arr = body.split(":", 3);
                        if (arr.length == 3 && dmListener != null) {
                            String toId = arr[0], fromId = arr[1], text = arr[2];
                            SwingUtilities.invokeLater(() -> dmListener.onDM(toId, fromId, text));
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("[클라] 수신 종료");
            } finally {
                closeLiveConnection();
            }
        });

        listenerThread.start();
    }

    // ================== 채팅 송신 (liveSocket 사용) ==================
    public void sendChat(String chatData) {
        if (!loggedIn || liveOut == null) return;
        // CHAT_SEND:sender:msg
        liveOut.println(Protocol.CHAT_MESSAGE_SEND + loggedInId + ":" + chatData);
    }

    public void joinGroup(String roomName) {
        if (!loggedIn || liveOut == null) return;
        liveOut.println(Protocol.GROUP_JOIN + roomName);
    }

    public void sendGroupChat(String roomName, String msg) {
        if (!loggedIn || liveOut == null) return;
        // GROUP_CHAT:room:sender:msg
        liveOut.println(Protocol.GROUP_CHAT + roomName + ":" + loggedInId + ":" + msg);
    }

    public void sendDirectMessage(String toId, String msg) {
        if (!loggedIn || liveOut == null) return;
        // DM_SEND:toId:fromId:msg
        liveOut.println(Protocol.DIRECT_MESSAGE_REQUEST + toId + ":" + loggedInId + ":" + msg);
    }

    // ================== 연결 정리 ==================
    private synchronized void closeLiveConnection() {
        loggedIn = false;
        loggedInId = null;

        try { if (liveIn != null) liveIn.close(); } catch (Exception ignored) {}
        try { if (liveOut != null) liveOut.close(); } catch (Exception ignored) {}
        try { if (liveSocket != null) liveSocket.close(); } catch (Exception ignored) {}

        liveIn = null;
        liveOut = null;
        liveSocket = null;
    }
}