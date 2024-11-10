package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;

public class HomeView extends JFrame {
    private JButton battleButton, shopButton, characterSelectButton;
    private String playerName;
    private UserController userController;

    public HomeView(String playerName, UserController userController) {
        this.playerName = playerName;
        this.userController = userController;

        setTitle("RPG 홈");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        // 대결 버튼
        battleButton = new JButton("대결");
        battleButton.addActionListener(e -> startBattle());

        // 상점 버튼
        shopButton = new JButton("상점");
        shopButton.addActionListener(e -> openShop());

        // 캐릭터 선택 버튼
        characterSelectButton = new JButton("캐릭터 선택");
        characterSelectButton.addActionListener(e -> selectCharacter());

        add(battleButton);
        add(shopButton);
        add(characterSelectButton);

        setVisible(true);
    }

    private void startBattle() {
        // 대결 화면으로 이동 (GameView로 전환)
        new GameView(playerName);
        dispose(); // 홈 화면 닫기
    }

    private void openShop() {
        JOptionPane.showMessageDialog(this, "상점은 아직 구현되지 않았습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
    }

    private void selectCharacter() {
        JOptionPane.showMessageDialog(this, "캐릭터 선택은 아직 구현되지 않았습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
    }
}
