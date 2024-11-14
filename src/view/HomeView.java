package view;

import javax.swing.*;
import java.awt.*;
import controller.UserController;
import model.Player;
import model.Shop;

public class HomeView extends JFrame {
    private JButton battleButton, shopButton, characterSelectButton;
    private String playerName;
    private UserController userController;
    private Player player;
    private Shop shop;

    public HomeView(String playerName, UserController userController) {
        //this.playerName = playerName;
    	player = new Player(playerName, 100, 1);
        this.userController = userController;

        setTitle("RPG 홈");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        // 대결 버튼
        battleButton = new JButton("대결");
        battleButton.addActionListener(e -> startBattle());

        // 상점 버튼
        shopButton = new JButton("상점");
        shopButton.addActionListener(e -> openShop());

        add(battleButton);
        add(shopButton);

        setVisible(true);
    }

    private void startBattle() {
        // 대결 화면으로 이동 (GameView로 전환)
        new GameView(playerName, player);
        dispose(); // 홈 화면 닫기
    }

    private void openShop() {
    	Shop shop = new Shop();
    	// 상점 화면으로 이동 (ShopView로 전환)
    	new ShopView(player, shop, this);
    	dispose();
    }
}