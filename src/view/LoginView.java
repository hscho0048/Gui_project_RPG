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

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (userController.authenticate(username, password)) {
            JOptionPane.showMessageDialog(this, "로그인 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "HomeView");
        } else {
            JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
