package view;

import javax.swing.*;

import controller.UserController;

import java.awt.*;
import java.util.List;
import model.Item;
import model.Player;
import model.Shop;
import util.UIUtils;

public class ShopView extends JPanel {
    private Player player;
    private JLabel playerInfoLabel;
    private JPanel itemPanel;
    private JButton buyButton;
    private JButton backButton;
    private UserController userController;
    private Shop shop;
    private JFrame mainFrame; // CardLayout 관리용 프레임
    private GameView gameView;

    public ShopView(Player player, UserController userController, GameView gameView, JFrame mainFrame) {
        this.player = player;
        this.userController = userController;
        this.gameView = gameView; // GameView 초기화
        this.mainFrame = mainFrame;
        this.shop = new Shop();
        Font font = new Font("Default", Font.BOLD, 15);

        // 플레이어 정보
        playerInfoLabel = new JLabel("플레이어: " + player.getName() + " | 금액: " + player.getMoney());
        playerInfoLabel.setFont(font);
        add(playerInfoLabel, BorderLayout.NORTH);

        // 아이템 패널
        itemPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        itemPanel.setOpaque(false);
        List<Item> items = shop.getItem();
        if (items != null && !items.isEmpty()) {
            for (Item item : items) {
                JButton itemButton = createItemButton(item);
                itemPanel.add(itemButton);
            }
        } else {
            JOptionPane.showMessageDialog(this, "상점에 아이템이 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
        add(new JScrollPane(itemPanel), BorderLayout.CENTER);

        // 버튼 패널
        buyButton = new JButton("구매");
        buyButton.addActionListener(e -> handleBuyItem());

        backButton = new JButton("홈으로");
        backButton.addActionListener(e -> handleBack());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(buyButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createItemButton(Item item) {
        JButton itemButton = new JButton(
                "<html><center>" + item.getName() + "<br>가격: " + item.getPrice() + "</center></html>", item.getImage());
        itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        itemButton.setHorizontalTextPosition(SwingConstants.CENTER);
        itemButton.setFont(new Font("Default", Font.BOLD, 16));
        itemButton.setPreferredSize(new Dimension(10, 50));
        itemButton.setBackground(Color.WHITE);
        itemButton.setOpaque(true);
        itemButton.setBorder(BorderFactory.createEmptyBorder());
        itemButton.putClientProperty("selected", false);

        itemButton.addActionListener(e -> {
            boolean isSelected = (boolean) itemButton.getClientProperty("selected");

            if (isSelected) {
                itemButton.setBorder(BorderFactory.createEmptyBorder());
                itemButton.setBackground(Color.WHITE);
                itemButton.putClientProperty("selected", false);
                itemPanel.putClientProperty("selectedItem", null);
            } else {
                for (Component component : itemPanel.getComponents()) {
                    if (component instanceof JButton) {
                        ((JButton) component).setBorder(BorderFactory.createEmptyBorder());
                        ((JButton) component).setBackground(Color.WHITE);
                        ((JButton) component).putClientProperty("selected", false);
                    }
                }
                itemButton.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 3));
                itemButton.setBackground(new Color(240, 240, 240));
                itemButton.putClientProperty("selected", true);
                itemPanel.putClientProperty("selectedItem", item);
            }
        });

        return itemButton;
    }

    private void handleBuyItem() {
        Item selectedItem = (Item) itemPanel.getClientProperty("selectedItem");
        if (selectedItem != null) {
            if (player.getMoney() >= selectedItem.getPrice()) {
                player.buyItem(selectedItem); // 플레이어가 아이템 구매
                player.setMoney(player.getMoney() - selectedItem.getPrice()); // 금액 차감

                // GameView에 아이템 추가
                gameView.addItemToInventory(selectedItem); // 필드 gameView를 사용

                updatePlayerInfo(); // 플레이어 정보 갱신
            } else {
                UIUtils.indicateError(buyButton); // 돈이 부족한 경우
            }
        } else {
            UIUtils.indicateError(buyButton); // 아이템이 선택되지 않았을 때
        }
    }



    private void updatePlayerInfo() {
        playerInfoLabel.setText("플레이어: " + player.getName() + " | 금액: " + player.getMoney());
        revalidate();
        repaint();
    }

    private void handleBack() {
        CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
        cardLayout.show(mainFrame.getContentPane(), "HomeView");
    }
}
