package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signUpButton;
    private UserController userController;

    public LoginView(UserController userController) {
        this.userController = userController;

        setTitle("RPG 로그인");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        // ID 입력 필드
        JLabel usernameLabel = new JLabel("아이디:");
        usernameField = new JTextField(15);

        // 비밀번호 입력 필드
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordField = new JPasswordField(15);

        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.addActionListener(e -> login());

        // 회원가입 버튼
        signUpButton = new JButton("회원가입");
        signUpButton.addActionListener(e -> new SignUpView(userController));

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signUpButton);

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (userController.authenticate(username, password)) {
            JOptionPane.showMessageDialog(this, "로그인 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // 로그인 창 닫기
            new GameView(username); // 게임 창 열기
        } else {
            JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

