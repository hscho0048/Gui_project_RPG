package view;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import controller.UserController;

public class HomeView extends JPanel {
    private JButton battleButton, shopButton, characterSelectButton;
    private JTextArea rankingTextArea; // 랭킹 정보를 표시할 텍스트 영역
    private UserController userController;
    private JFrame mainFrame;

    public HomeView(UserController userController, JFrame mainFrame) {
        this.userController = userController;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout()); // 전체 레이아웃 설정

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1)); // 세로로 버튼 배치
        battleButton = new JButton("대결");
        battleButton.addActionListener(e -> showGameView());
        shopButton = new JButton("상점");
        shopButton.addActionListener(e -> showShopView());
        characterSelectButton = new JButton("캐릭터 선택");
        characterSelectButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "캐릭터 선택은 아직 구현되지 않았습니다."));

        buttonPanel.add(battleButton);
        buttonPanel.add(shopButton);
        buttonPanel.add(characterSelectButton);

        // 랭킹 패널
        JPanel rankingPanel = new JPanel();
        rankingPanel.setLayout(new BorderLayout());
        rankingPanel.setBorder(BorderFactory.createTitledBorder("랭킹"));

        rankingTextArea = new JTextArea();
        rankingTextArea.setEditable(false); // 사용자 입력 비활성화
        JScrollPane scrollPane = new JScrollPane(rankingTextArea); // 스크롤 가능하도록 설정
        rankingPanel.add(scrollPane, BorderLayout.CENTER);

        // 버튼과 랭킹을 나란히 배치
        add(buttonPanel, BorderLayout.WEST);
        add(rankingPanel, BorderLayout.CENTER);

        // 초기 랭킹 로드
        updateRanking();
    }

    private void showGameView() {
        CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
        cardLayout.show(mainFrame.getContentPane(), "GameView");
    }

    private void showShopView() {
        CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
        cardLayout.show(mainFrame.getContentPane(), "ShopView");
    }

    public void updateRanking() {
        StringBuilder rankingText = new StringBuilder();
        rankingText.append("순위\t플레이어\t캐릭터\t구매 아이템\t턴수\n");
        rankingText.append("--------------------------------------------------\n");

        ResultSet rs = userController.getRanking();
        if (rs == null) {
            rankingText.append("랭킹 데이터를 불러오는 중 오류가 발생했습니다.\n");
            rankingTextArea.setText(rankingText.toString());
            return;
        }

        try {
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                int rank = rs.getInt("rank");
                String player = rs.getString("player");
                String character = rs.getString("character"); // 'UNKNOWN' 값 가져오기
                String items = rs.getString("items") != null ? rs.getString("items") : "없음";
                int turns = rs.getInt("turns");

                rankingText.append(rank).append("\t")
                           .append(player).append("\t")
                           .append(character).append("\t")
                           .append(items).append("\t")
                           .append(turns).append("\n");
            }

            if (!hasData) {
                rankingText.append("아직 플레이한 사용자가 없습니다.\n");
            }
        } catch (SQLException e) {
            rankingText.append("랭킹 데이터를 처리하는 중 오류 발생: ").append(e.getMessage());
        }

        rankingTextArea.setText(rankingText.toString());
    }


}

