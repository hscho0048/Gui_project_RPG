package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import controller.UserController;

public class HomeView extends JPanel {
    private JButton battleButton, shopButton, characterSelectButton;
    private JTable rankingTable; // JTable로 랭킹 데이터 표시
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

        // 랭킹 테이블 패널
        JPanel rankingPanel = new JPanel(new BorderLayout());
        rankingPanel.setBorder(BorderFactory.createTitledBorder("랭킹"));

        // JTable 초기화
        rankingTable = new JTable(new DefaultTableModel(new Object[]{"순위", "플레이어", "캐릭터", "구매 아이템", "턴수"}, 0));
        JScrollPane scrollPane = new JScrollPane(rankingTable); // 스크롤 가능하도록 설정
        rankingPanel.add(scrollPane, BorderLayout.CENTER);

        // 버튼과 랭킹 테이블을 나란히 배치
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
        DefaultTableModel tableModel = (DefaultTableModel) rankingTable.getModel();
        tableModel.setRowCount(0); // 기존 데이터 제거

        ResultSet rs = userController.getRanking();
        if (rs == null) {
            JOptionPane.showMessageDialog(this, "랭킹 데이터를 불러오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            while (rs.next()) {
                int rank = rs.getInt("rank");
                String player = rs.getString("player");
                String character = rs.getString("character") != null ? rs.getString("character") : "UNKNOWN";
                String items = rs.getString("items") != null ? rs.getString("items") : "없음";
                int turns = rs.getInt("turns");

                // JTable에 데이터 추가
                tableModel.addRow(new Object[]{rank, player, character, items, turns});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "랭킹 데이터를 처리하는 중 오류가 발생했습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
