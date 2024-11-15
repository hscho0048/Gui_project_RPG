package controller;

import java.util.HashMap;
import java.util.Map;

public class UserController {
    private Map<String, String> userDatabase; // 사용자 ID와 비밀번호 저장

    public UserController() {
        userDatabase = new HashMap<>();
    }

    public boolean signUp(String username, String password) {
        if (userDatabase.containsKey(username)) {
            return false; // 이미 존재하는 사용자 ID
        }
        userDatabase.put(username, password);
        return true;
    }

    public boolean authenticate(String username, String password) {
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
    }
}
