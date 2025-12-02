package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import common.Protocol;

/**
 * 클라이언트 한 명을 담당하는 스레드.
 * - 클라이언트가 보낸 한 줄을 읽고
 * - LOGIN / JOIN 요청을 처리한 뒤
 * - SUCCESS: 또는 FAIL: 형식으로 응답을 돌려줌.
 */
public class ClientHandler extends Thread {

    private Socket socket;
    private UserDao userDao;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.userDao = new UserDao();
    }

    @Override
    public void run() {
        System.out.println("[서버] ClientHandler 스레드 시작");

        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
        ) {
            // 1. 클라이언트가 보낸 한 줄 읽기
            String line = in.readLine();
            if (line == null) {
                return;
            }

            System.out.println("[서버] 수신: " + line);

            // 2. 요청 처리
            String response = handleRequest(line);

            // 3. 응답 전송
            out.write(response);
            out.write("\n");
            out.flush();

            System.out.println("[서버] 응답: " + response);

        } catch (IOException e) {
            System.out.println("[서버] ClientHandler 오류: " + e.getMessage());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                System.out.println("[서버] 클라이언트 소켓 종료");
            } catch (IOException e) {
                System.out.println("[서버] 소켓 종료 오류: " + e.getMessage());
            }
        }
    }

    // ------------------- 여기부터는 private 메소드들 (전부 클래스 안에 있음!!) -------------------

    private String handleRequest(String line) {
        try {
            if (line.startsWith(Protocol.LOGIN_REQUEST)) {
                // 예: "LOGIN:id:pw"
                String data = line.substring(Protocol.LOGIN_REQUEST.length());
                return handleLogin(data);

            } else if (line.startsWith(Protocol.JOIN_REQUEST)) {
                // 예: "JOIN:id:pw:name:gender:birth:email:phone"
                String data = line.substring(Protocol.JOIN_REQUEST.length());
                return handleJoin(data);

            } else {
                return Protocol.FAIL_RESPONSE + "알_수_없는_요청";
            }
        } catch (Exception e) {
            System.out.println("[서버] 요청 처리 중 예외: " + e.getMessage());
            return Protocol.FAIL_RESPONSE + "서버_오류";
        }
    }

    /**
     * data 형식: "id:pw"
     */
    private String handleLogin(String data) {
        String[] parts = data.split(":");
        if (parts.length != 2) {
            return Protocol.FAIL_RESPONSE + "형식_오류";
        }

        String id = parts[0];
        String pw = parts[1];

        boolean ok = userDao.checkLogin(id, pw);
        if (ok) {
            return Protocol.SUCCESS_RESPONSE + id;
        } else {
            return Protocol.FAIL_RESPONSE + "ID_또는_PW_오류";
        }
    }

    /**
     * data 형식: "id:pw:name:gender:birth:email:phone"
     */
    private String handleJoin(String data) {
        String[] parts = data.split(":");
        if (parts.length != 7) {
            return Protocol.FAIL_RESPONSE + "형식_오류";
        }

        String id = parts[0];
        String pw = parts[1];
        String name = parts[2];
        int gender = Integer.parseInt(parts[3]);
        String birth = parts[4];
        String email = parts[5];
        String phone = parts[6];

        // 아이디 중복 체크
        if (userDao.existsId(id)) {
            return Protocol.FAIL_RESPONSE + "이미_존재하는_ID";
        }

        boolean ok = userDao.insertUser(id, pw, name, gender, birth, email, phone);
        if (ok) {
            return Protocol.SUCCESS_RESPONSE + id;
        } else {
            return Protocol.FAIL_RESPONSE + "DB_오류";
        }
    }
}
