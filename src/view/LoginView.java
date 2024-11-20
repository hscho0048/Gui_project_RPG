package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
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
            // 로그인 성공 시 플레이어 정보 가져오기
            Player player = userController.getPlayerInfo(username);
            if (player == null) {
                JOptionPane.showMessageDialog(this, "플레이어 정보를 가져오는 데 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // GameView 및 ShopView를 Player로 초기화
            GameView gameView = new GameView(username, userController, player, mainFrame);
            ShopView shopView = new ShopView(player, userController, gameView, mainFrame);

            // GameView와 ShopView를 메인 프레임에 추가
            mainFrame.getContentPane().add(gameView, "GameView");
            mainFrame.getContentPane().add(shopView, "ShopView");

            // 홈 화면으로 이동
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "HomeView");
        } else {
            // 로그인 실패 시 버튼 색상 변경
            UIUtils.indicateError(loginButton);
            JOptionPane.showMessageDialog(this, "아이디 또는 비밀번호가 올바르지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

}
