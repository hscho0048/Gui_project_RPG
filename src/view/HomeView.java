package view;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import controller.UserController;
import model.Player;
import model.Shop;

public class HomeView extends JPanel {
    private JButton battleButton, shopButton, characterSelectButton, rankingButton;
    private String playerName;
    private UserController userController;
    private JFrame mainFrame;

    public HomeView(UserController userController, JFrame mainFrame) {
        this.userController = userController;
        this.mainFrame = mainFrame;

        setLayout(new GridLayout(4, 1));

        battleButton = new JButton("대결");
        battleButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "GameView");
        });

        shopButton = new JButton("상점");
        shopButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "ShopView");
        });

        characterSelectButton = new JButton("캐릭터 선택");
        characterSelectButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "캐릭터 선택은 아직 구현되지 않았습니다."));

        rankingButton = new JButton("랭킹 보기");
        rankingButton.addActionListener(e -> displayRanking());

        add(battleButton);
        add(shopButton);
        add(characterSelectButton);
        add(rankingButton);
    }

    private void displayRanking() {
        StringBuilder rankingText = new StringBuilder();
        rankingText.append("랭킹:\n\n");

        try {
            ResultSet rs = userController.getRanking();
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                rankingText.append("ID: ").append(username).append(", 턴 수: ").append(score).append("\n");
            }
        } catch (SQLException e) {
            rankingText.append("랭킹을 불러오는 중 오류 발생: ").append(e.getMessage());
        }

        JTextArea textArea = new JTextArea(rankingText.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "랭킹 보기", JOptionPane.INFORMATION_MESSAGE);
    }
}
