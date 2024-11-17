package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;

public class SignUpView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signUpButton;
    private UserController userController;

    public SignUpView(UserController userController) {
        this.userController = userController;

        setTitle("회원가입");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        // ID 입력 필드
        JLabel usernameLabel = new JLabel("아이디:");
        usernameField = new JTextField(15);

        // 비밀번호 입력 필드
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordField = new JPasswordField(15);

        // 회원가입 버튼
        signUpButton = new JButton("회원가입");
        signUpButton.addActionListener(e -> signUp());

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(signUpButton);

        setVisible(true);
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
            dispose(); // 회원가입 창 닫기
        } else {
            JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}