package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;

public class SignUpView extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signUpButton, backButton;
    private UserController userController;
    private JFrame mainFrame; // CardLayout 관리용 프레임

    public SignUpView(UserController userController, JFrame mainFrame) {
        this.userController = userController;
        this.mainFrame = mainFrame;

        setLayout(new GridLayout(4, 2));

        // ID 입력 필드
        JLabel usernameLabel = new JLabel("아이디:");
        usernameField = new JTextField(15);

        // 비밀번호 입력 필드
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordField = new JPasswordField(15);

        // 회원가입 버튼
        signUpButton = new JButton("회원가입");
        signUpButton.addActionListener(e -> signUp());

        // 뒤로가기 버튼
        backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "LoginView"); // LoginView로 전환
        });

        // 컴포넌트 추가
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(signUpButton);
        add(backButton);
    }

    private void signUp() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userController.signUp(username, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "회원가입 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "LoginView"); // 회원가입 성공 시 LoginView로 이동
        } else {
            JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
