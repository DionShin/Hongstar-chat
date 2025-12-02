package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 서버 메인 클래스.
 * - 포트 8080에서 클라이언트 접속을 기다린다.
 * - 클라이언트가 접속하면 ClientHandler 스레드를 하나 만들어서 처리한다.
 */
public class ServerMain {

    public static final int PORT = 8080;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("[서버] 포트 " + PORT + "에서 대기 중...");

            while (true) {
                // 1. 클라이언트 접속 허용
                Socket clientSocket = serverSocket.accept();
                System.out.println("[서버] 클라이언트 접속: " + clientSocket.getInetAddress());

                // 2. 클라이언트 한 명을 담당할 스레드 생성
                ClientHandler handler = new ClientHandler(clientSocket);

                // 3. 스레드 시작
                handler.start();
            }

        } catch (IOException e) {
            System.out.println("[서버] 오류: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("[서버] 서버소켓 종료 중 오류: " + e.getMessage());
            }
        }
    }
}
