package view;

import javax.swing.*;
import java.awt.*;
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
        
        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.addActionListener(e -> login());

        // 패널에 컴포넌트 추가
        JPanel panel = new JPanel();
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(loginButton);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void login() {
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
