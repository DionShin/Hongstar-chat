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
 * - LOGIN / JOIN / LOGOUT / UPDATE_USER / DELETE_USER 요청을 처리한 뒤
 * - SUCCESS: 또는 FAIL: 형식으로 응답을 돌려줌.
 *
 * 현재 구조:
 *   1) 클라이언트가 소켓을 열고 문자열 1줄 전송
 *   2) 서버는 1줄 처리 후 1줄 응답
 *   3) 소켓을 닫고 종료 (요청당 1회 통신)
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

    /**
     * 클라이언트가 보낸 한 줄(line)을 보고
     * 어떤 요청인지 판단해서 각각의 처리 메소드를 호출.
     */
    private String handleRequest(String line) {
    try {

        if (line.startsWith(Protocol.LOGIN_REQUEST)) {
            String data = line.substring(Protocol.LOGIN_REQUEST.length());
            return handleLogin(data);
        }

        else if (line.startsWith(Protocol.JOIN_REQUEST)) {
            String data = line.substring(Protocol.JOIN_REQUEST.length());
            return handleJoin(data);
        }

        else if (line.startsWith(Protocol.LOGOUT_REQUEST)) {
            return handleLogout();
        }

        else if (line.startsWith(Protocol.UPDATE_USER_REQUEST)) {
            // UPDATE_USER:id:pw:name:email:phone  → gender/birth 제외 (네 DB 구조 기준!)
            String data = line.substring(Protocol.UPDATE_USER_REQUEST.length());
            return handleUpdateUser(data);
        }

        else if (line.startsWith(Protocol.DELETE_USER_REQUEST)) {
            // DELETE_USER:id:pw
            String data = line.substring(Protocol.DELETE_USER_REQUEST.length());
            return handleDeleteUser(data);
        }

        else {
            return Protocol.FAIL_RESPONSE + "알_수_없는_요청";
        }

    } catch (Exception e) {
        System.out.println("[서버] 요청 처리 중 예외: " + e.getMessage());
        return Protocol.FAIL_RESPONSE + "서버_오류";
    }
}


    

    /**
     * 로그인 처리
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
     * 회원가입 처리
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

    /**
     * 로그아웃 처리
     * 현재 구조에서는 서버가 따로 로그인 상태를 유지하지 않기 때문에
     * 단순히 "성공" 응답만 내려주면 됨.
     * (클라이언트 쪽에서 로그인 화면으로 돌아가는 식으로 처리)
     */
    private String handleLogout() {
        // 필요하다면 여기서 서버측에서 세션/로그인 정보 정리 가능
        return Protocol.SUCCESS_RESPONSE + "LOGOUT";
    }

    /**
     * 사용자 정보 수정 처리
     */
    // ClientHandler.java 안

/**
 * data 형식: "id:newPw:newName:newEmail:newPhone"
 */
private String handleUpdateUser(String data) {

    // 빈 항목 허용 — length 유지
    String[] parts = data.split(":", -1);

    System.out.println("[서버] UPDATE_USER data: " + data);
    System.out.println("[서버] UPDATE_USER split length = " + parts.length);

    if (parts.length != 5) {
        return Protocol.FAIL_RESPONSE + "형식_오류";
    }

    String id       = parts[0];
    String newPw    = parts[1];
    String newName  = parts[2];
    String newEmail = parts[3];
    String newPhone = parts[4];

    boolean ok = userDao.updateUser(id, newPw, newName, newEmail, newPhone);

    if (ok) return Protocol.SUCCESS_RESPONSE + id;
    return Protocol.FAIL_RESPONSE + "DB_오류";
}

    /**
     * 사용자 삭제(회원 탈퇴) 처리
     * data 형식: "id:pw"
     *  - 비밀번호 확인 후 실제 삭제.
     */
    private String handleDeleteUser(String data) {
        String[] parts = data.split(":");
        if (parts.length != 2) {
            return Protocol.FAIL_RESPONSE + "형식_오류";
        }

        String id = parts[0];
        String pw = parts[1];

        // 비밀번호 확인
        boolean ok = userDao.checkLogin(id, pw);
        if (!ok) {
            return Protocol.FAIL_RESPONSE + "PW_오류";
        }

        boolean deleted = userDao.deleteUser(id);
        if (deleted) {
            System.out.println("[서버] 회원 삭제 성공: " + id);
            return Protocol.SUCCESS_RESPONSE + "DELETE_USER";
        } else {
            System.out.println("[서버] 회원 삭제 실패: " + id);
            return Protocol.FAIL_RESPONSE + "DB_오류";
        }
    }
}
