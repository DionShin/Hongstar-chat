package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.Protocol;

public class ClientHandler extends Thread {

    // ===== 접속한 모든 클라이언트(전체 채팅) =====
    private static final Set<ClientHandler> clients = new HashSet<>();

    // ===== 로그인ID -> ClientHandler (DM 라우팅용) =====
    private static final Map<String, ClientHandler> onlineUsers = new HashMap<>();

    // ===== 그룹 채팅: room -> handlers =====
    private static final Map<String, Set<ClientHandler>> groupRooms = new HashMap<>();

    private final Socket socket;
    private final UserDao userDao;

    private BufferedReader in;
    private BufferedWriter out;

    private String loginId = null; // 이 연결이 로그인 성공한 ID

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.userDao = new UserDao();
    }

    @Override
    public void run() {
        System.out.println("[서버] ClientHandler 시작: " + socket.getInetAddress());

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            synchronized (clients) {
                clients.add(this);
            }

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[서버] 수신: " + line);

                String response = handleRequest(line);

                // 채팅류는 response=null (응답 필요 없게)
                if (response != null) {
                    send(response);
                    System.out.println("[서버] 응답: " + response);
                }
            }

        } catch (IOException e) {
            System.out.println("[서버] 연결 종료: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    // ===================== 요청 분기 =====================
    private String handleRequest(String line) {

        try {
            if (line.startsWith(Protocol.LOGIN_REQUEST)) {
                return handleLogin(line.substring(Protocol.LOGIN_REQUEST.length()));
            }
            else if (line.startsWith(Protocol.JOIN_REQUEST)) {
                return handleJoin(line.substring(Protocol.JOIN_REQUEST.length()));
            }
            else if (line.startsWith(Protocol.LOGOUT_REQUEST)) {
                return handleLogout();
            }
            else if (line.startsWith(Protocol.UPDATE_USER_REQUEST)) {
                return handleUpdateUser(line.substring(Protocol.UPDATE_USER_REQUEST.length()));
            }
            else if (line.startsWith(Protocol.DELETE_USER_REQUEST)) {
                return handleDeleteUser(line.substring(Protocol.DELETE_USER_REQUEST.length()));
            }

            // ===== 그룹 =====
            else if (line.startsWith(Protocol.GROUP_JOIN)) {
                String roomName = line.substring(Protocol.GROUP_JOIN.length()).trim();
                joinGroup(roomName);
                return Protocol.SUCCESS_RESPONSE + "GROUP_JOIN";
            }
            else if (line.startsWith(Protocol.GROUP_CHAT)) {
                String data = line.substring(Protocol.GROUP_CHAT.length());
                handleGroupChat(data);
                return null;
            }

            // ===== DM =====
            else if (line.startsWith(Protocol.DIRECT_MESSAGE_REQUEST)) {
                String data = line.substring(Protocol.DIRECT_MESSAGE_REQUEST.length());
                handleDirectMessage(data);
                return null;
            }

            // ===== 전체 채팅 =====
            else if (line.startsWith(Protocol.CHAT_MESSAGE_SEND)) {
                String msg = line.substring(Protocol.CHAT_MESSAGE_SEND.length()); // "sender:msg"
                broadcast(Protocol.CHAT_BROADCAST + msg);
                return null;
            }

            return Protocol.FAIL_RESPONSE + "알_수_없는_요청";

        } catch (Exception e) {
            System.out.println("[서버] 요청 처리 예외: " + e.getMessage());
            return Protocol.FAIL_RESPONSE + "서버_오류";
        }
    }

    // ===================== 회원 기능(유지) =====================

    private String handleLogin(String data) {
        String[] parts = data.split(":");
        if (parts.length != 2) return Protocol.FAIL_RESPONSE + "형식_오류";

        String id = parts[0];
        String pw = parts[1];

        boolean ok = userDao.checkLogin(id, pw);
        if (!ok) return Protocol.FAIL_RESPONSE + "ID_또는_PW_오류";

        // 로그인 성공: 이 연결에 loginId 바인딩
        this.loginId = id;
        synchronized (onlineUsers) {
            onlineUsers.put(id, this);
        }

        return Protocol.SUCCESS_RESPONSE + id;
    }

    private String handleJoin(String data) {
        String[] parts = data.split(":");
        if (parts.length != 7) return Protocol.FAIL_RESPONSE + "형식_오류";

        if (userDao.existsId(parts[0]))
            return Protocol.FAIL_RESPONSE + "이미_존재하는_ID";

        boolean ok = userDao.insertUser(
                parts[0], parts[1], parts[2],
                Integer.parseInt(parts[3]), parts[4], parts[5], parts[6]
        );

        return ok ? Protocol.SUCCESS_RESPONSE + parts[0]
                  : Protocol.FAIL_RESPONSE + "DB_오류";
    }

    private String handleLogout() {
        // 연결 유지할 수도 있지만, 일단 로그아웃 처리만
        if (loginId != null) {
            synchronized (onlineUsers) {
                onlineUsers.remove(loginId);
            }
            loginId = null;
        }
        return Protocol.SUCCESS_RESPONSE + "LOGOUT";
    }

    private String handleUpdateUser(String data) {
        String[] parts = data.split(":", -1);
        if (parts.length != 5) return Protocol.FAIL_RESPONSE + "형식_오류";

        boolean ok = userDao.updateUser(parts[0], parts[1], parts[2], parts[3], parts[4]);
        return ok ? Protocol.SUCCESS_RESPONSE + parts[0]
                  : Protocol.FAIL_RESPONSE + "DB_오류";
    }

    private String handleDeleteUser(String data) {
        String[] parts = data.split(":");
        if (parts.length != 2) return Protocol.FAIL_RESPONSE + "형식_오류";

        boolean ok = userDao.checkLogin(parts[0], parts[1]);
        if (!ok) return Protocol.FAIL_RESPONSE + "PW_오류";

        boolean deleted = userDao.deleteUser(parts[0]);
        return deleted ? Protocol.SUCCESS_RESPONSE + "DELETE_USER"
                       : Protocol.FAIL_RESPONSE + "DB_오류";
    }

    // ===================== 채팅 기능 =====================

    // 전체 브로드캐스트
    private void broadcast(String packet) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.send(packet);
            }
        }
    }

    // 그룹 입장
    private void joinGroup(String roomName) {
        if (roomName == null || roomName.isEmpty()) return;
        synchronized (groupRooms) {
            groupRooms.putIfAbsent(roomName, new HashSet<>());
            groupRooms.get(roomName).add(this);
        }
        System.out.println("[서버] 그룹 입장: " + roomName + " / " + loginId);
    }

    // 그룹 채팅 data: "room:sender:msg"
    private void handleGroupChat(String data) {
        String[] parts = data.split(":", 3);
        if (parts.length != 3) return;

        String room = parts[0];
        String sender = parts[1];
        String msg = parts[2];

        String packet = "GROUP:" + room + ":" + sender + ":" + msg;

        synchronized (groupRooms) {
            Set<ClientHandler> roomSet = groupRooms.get(room);
            if (roomSet == null) return;

            for (ClientHandler c : roomSet) {
                c.send(packet);
            }
        }
    }

    // DM data: "toId:fromId:msg"
    private void handleDirectMessage(String data) {
        String[] parts = data.split(":", 3);
        if (parts.length != 3) return;

        String toId = parts[0];
        String fromId = parts[1];
        String msg = parts[2];

        String packet = Protocol.DIRECT_MESSAGE_PREFIX + toId + ":" + fromId + ":" + msg;

        ClientHandler to;
        ClientHandler from;

        synchronized (onlineUsers) {
            to = onlineUsers.get(toId);
            from = onlineUsers.get(fromId);
        }

        // 받는 사람에게
        if (to != null) to.send(packet);
        // 보낸 사람에게도(내 화면에도 보이게)
        if (from != null && from != to) from.send(packet);
    }

    // ===================== 송신/정리 =====================

    private void send(String packet) {
        try {
            out.write(packet);
            out.write("\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    private void cleanup() {
        synchronized (clients) {
            clients.remove(this);
        }

        if (loginId != null) {
            synchronized (onlineUsers) {
                onlineUsers.remove(loginId);
            }
        }

        synchronized (groupRooms) {
            for (Set<ClientHandler> set : groupRooms.values()) {
                set.remove(this);
            }
        }

        try { socket.close(); } catch (IOException ignored) {}
        System.out.println("[서버] 종료: " + socket.getInetAddress());
    }

    // (옵션) 서버 공지 필요하면 ServerMain에서 호출 가능
    public static void broadcastFromServer(String msg) {
        String packet = Protocol.CHAT_BROADCAST + "[SERVER]:" + msg;
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.send(packet);
            }
        }
    }
}