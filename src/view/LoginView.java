package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import model.MyCharacter;
import model.Player;
import util.UIUtils;

public class LoginView extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signUpButton;
    private UserController userController;
    private JFrame mainFrame;

    // 로그인 성공 리스너 인터페이스
    public interface OnLoginSuccessListener {
        void onLoginSuccess(String userId); // 로그인 성공 시 호출
    }

    private OnLoginSuccessListener onLoginSuccessListener; // 리스너 필드

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

    public void setOnLoginSuccessListener(OnLoginSuccessListener listener) {
        this.onLoginSuccessListener = listener;
    }

    private void login() {
        String username = usernameField.getText(); // 사용자 ID
        String password = new String(passwordField.getPassword()); // 비밀번호

        if (username.isEmpty() || password.isEmpty()) {
            UIUtils.indicateError(loginButton); // 입력이 비어 있을 경우 버튼 색상 변경
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userController.authenticate(username, password)) {
            // 로그인 성공 시 초기화 작업 수행
            Player player = userController.getPlayerInfo(username); // 플레이어 정보 가져오기
            if (player == null) {
                JOptionPane.showMessageDialog(this, "플레이어 정보를 가져오는 데 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // player 객체에 ID를 명시적으로 설정 (로그인 후 받아온 userId를 설정)
            int userId = userController.getUserId(username);  // getUserId()는 로그인 후 userId를 가져오는 메서드
            player.setId(userId);  // player 객체에 userId 설정
            System.out.println("디버깅: 로그인 후 player ID: " + player.getId());  // userId가 제대로 설정되었는지 확인
            
            // MyCharacter 초기화
            MyCharacter globalMyCharacter = new MyCharacter();

            // HomeView 초기화
            HomeView homeView = new HomeView(userController, mainFrame, globalMyCharacter, player);
            mainFrame.getContentPane().add(homeView, "HomeView");

            // HomeView로 이동
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "HomeView");
        } else {
            // 로그인 실패 시 버튼 색상 변경
            UIUtils.indicateError(loginButton);
            JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

