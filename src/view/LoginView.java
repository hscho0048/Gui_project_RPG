package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import util.UIUtils;

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

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UIUtils.indicateError(loginButton); // 입력이 비어 있을 경우 버튼 색상 변경
            return;
        }

        if (userController.authenticate(username, password)) {
            // 로그인 성공 시 홈 화면으로 이동
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "HomeView");
        } else {
            // 로그인 실패 시 버튼 색상 변경
            UIUtils.indicateError(loginButton);
        }
    }
}
