package view;

import javax.swing.*;
import java.awt.*;
<<<<<<< HEAD
=======
<<<<<<< HEAD
import controller.GameController;

public class LoginView extends JFrame {
    private JTextField nameField;
    private JButton loginButton;

    public LoginView() {
        setTitle("RPG 로그인");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 이름 입력 필드
        nameField = new JTextField(15);
        JLabel nameLabel = new JLabel("플레이어 이름: ");
        
=======
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
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

<<<<<<< HEAD
=======
>>>>>>> 0ca0e7c (commit message)
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.addActionListener(e -> login());

<<<<<<< HEAD
=======
<<<<<<< HEAD
        // 패널에 컴포넌트 추가
        JPanel panel = new JPanel();
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);
=======
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
        // 회원가입 버튼
        signUpButton = new JButton("회원가입");
        signUpButton.addActionListener(e -> new SignUpView(userController));

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signUpButton);

<<<<<<< HEAD
=======
>>>>>>> 0ca0e7c (commit message)
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
        setVisible(true);
    }

    private void login() {
<<<<<<< HEAD
=======
<<<<<<< HEAD
        String playerName = nameField.getText();
        if (!playerName.trim().isEmpty()) {
            // 플레이어 이름 입력 시 GameView로 이동
            dispose();  // 현재 로그인 창을 닫고
            new GameView(playerName); // 새로운 게임 창 열기
        } else {
            JOptionPane.showMessageDialog(this, "이름을 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
=======
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (userController.authenticate(username, password)) {
            JOptionPane.showMessageDialog(this, "로그인 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // 로그인 창 닫기
            new HomeView(username, userController); // 홈 화면 열기
        } else {
            JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

<<<<<<< HEAD
=======
>>>>>>> 0ca0e7c (commit message)
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
