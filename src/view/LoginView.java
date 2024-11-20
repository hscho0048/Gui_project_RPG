package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;

public class LoginView extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signUpButton;
    private UserController userController;
    private JFrame mainFrame;

    public LoginView(UserController userController, JFrame mainFrame) {
        this.userController = userController;
        this.mainFrame = mainFrame;

        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("아이디:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordField = new JPasswordField(15);

        loginButton = new JButton("로그인");
        loginButton.addActionListener(e -> login());

        signUpButton = new JButton("회원가입");
        signUpButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "SignUpView");
        });

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signUpButton);
    }

    private void indicateError(JButton button) {
        Color originalColor = button.getBackground(); // 원래 색상 저장
        button.setBackground(Color.RED); // 빨간색으로 변경
        Timer timer = new Timer(200, e -> button.setBackground(originalColor)); // 200ms 후 복구
        timer.setRepeats(false); // 한 번만 실행
        timer.start();
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            indicateError(loginButton); // 입력이 비어 있을 경우 에러 표시
            return;
        }

        if (userController.authenticate(username, password)) {
            // 로그인 성공 시 홈 화면으로 이동
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "HomeView");
        } else {
            // 로그인 실패 시 버튼 색상으로 알림
            indicateError(loginButton);
        }
    }
}
