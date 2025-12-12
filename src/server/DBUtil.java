package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL 연결을 도와주는 클래스
 */
public class DBUtil {

    private static final String URL =
    "jdbc:mysql://localhost:3306/hongstar?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";
    private static final String USER = "root";      
    private static final String PASSWORD = "dionshin0843"; 

    // MySQL 드라이버 로딩
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[서버] MySQL 드라이버 로딩 성공");
        } catch (ClassNotFoundException e) {
            System.out.println("[서버] 드라이버 로딩 실패: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
