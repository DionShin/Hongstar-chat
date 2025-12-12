package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 회원가입 / 로그인 DB처리 전담 클래스
 */
public class UserDao {

    // 회원가입 INSERT
    public boolean insertUser(String id, String pw, String name,
                              int gender, String birth,
                              String email, String phone) {

        String sql = "INSERT INTO user_table(id, password, username, gender, birth, email, phone) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            pstmt.setString(3, name);
            pstmt.setInt(4, gender);
            pstmt.setString(5, birth);   // "YYYY-MM-DD"
            pstmt.setString(6, email);
            pstmt.setString(7, phone);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("[서버] 회원가입 중 오류: " + e.getMessage());
            return false;
        }
    }

    // 로그인 체크
    public boolean checkLogin(String id, String pw) {
    String sql = "SELECT id FROM user_table WHERE id = ? AND password = ?";

    try (
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
        pstmt.setString(1, id);
        pstmt.setString(2, pw);
        ResultSet rs = pstmt.executeQuery();

        boolean exists = rs.next();
        System.out.println("[서버] DB 로그인 검사 결과: " + exists);  // <-- 로그추가

        return exists;

    } catch (SQLException e) {
        System.out.println("[서버] 로그인 확인 중 오류: " + e.getMessage());
        return false;
    }
}


    // 아이디 중복 검사
    public boolean existsId(String id) {
        String sql = "SELECT id FROM user_table WHERE id = ?";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("[서버] 중복검사 중 오류: " + e.getMessage());
            return false;
        }
    }

    

    // 회원 삭제
    public boolean deleteUser(String id) {

        String sql = "DELETE FROM user_table WHERE id = ?";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("[서버] 회원 삭제 중 오류: " + e.getMessage());
            return false;
        }
    }


    //회원정보 수정
    public boolean updateUser(String id, String pw, String name, String email, String phone) {

    String sql = "UPDATE user_table "
               + "SET password=?, username=?, email=?, phone=? "
               + "WHERE id=?";

    try (
        Connection conn = DBUtil.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {
        pstmt.setString(1, pw);
        pstmt.setString(2, name);
        pstmt.setString(3, email);
        pstmt.setString(4, phone);
        pstmt.setString(5, id);

        return pstmt.executeUpdate() > 0;

    } catch (SQLException e) {
        System.out.println("[서버] UPDATE 오류: " + e.getMessage());
        return false;
    }
}
}
