package common;

public class Protocol {

    // --- 통신 규약 상수 ---
    
    // 명령어 요청 (클라이언트 -> 서버)
    public static final String LOGIN_REQUEST = "LOGIN:";
    public static final String JOIN_REQUEST = "JOIN:";
    public static final String CHAT_MESSAGE_SEND = "CHAT_SEND:";
    public static final String PHOTO_UPLOAD_REQUEST = "PHOTO_UPLOAD:";

    public static final String LOGOUT_REQUEST       = "LOGOUT:";       // LOGOUT:
    public static final String UPDATE_USER_REQUEST  = "UPDATE_USER:";  // UPDATE_USER:...데이터...
    public static final String DELETE_USER_REQUEST  = "DELETE_USER:";  // DELETE_USER:...데이터...
    
    // 명령어 응답 (서버 -> 클라이언트)
    public static final String SUCCESS_RESPONSE = "SUCCESS:";
    public static final String FAIL_RESPONSE = "FAIL:";
    // public static final String BROADCAST_MESSAGE = "BROADCAST:"; // 전체 공지 메시지
    

    // --- 유틸리티 상수 ---
    
    // 데이터 항목을 구분하는 구분자
    // public static final String DELIMITER = ":"; 
    
    // private static final String SERVER_IP = ""; 
    // private static final int PORT = null;


    public static final String USER_DELETE_REQUEST = "DELETE_USER:";
}