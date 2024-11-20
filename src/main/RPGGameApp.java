package main;

import java.awt.CardLayout;

import javax.swing.*;
import controller.UserController;
import model.Player;
import model.Shop;
import view.*;

public class RPGGameApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserController userController = new UserController();
            JFrame mainFrame = new JFrame("RPG Game");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(800, 600);
            mainFrame.setLayout(new CardLayout());

            // 필요한 데이터 초기화
            Player player = new Player("플레이어 이름", 100, 10);

            // View 초기화
            LoginView loginView = new LoginView(userController, mainFrame);
            HomeView homeView = new HomeView(userController, mainFrame);
            GameView gameView = new GameView("플레이어 이름", userController, player, mainFrame);
            ShopView shopView = new ShopView(player, new Shop(), mainFrame);
            SignUpView signUpView = new SignUpView(userController, mainFrame);

            // CardLayout에 추가
            mainFrame.add(loginView, "LoginView");
            mainFrame.add(homeView, "HomeView");
            mainFrame.add(gameView, "GameView");
            mainFrame.add(shopView, "ShopView");
            mainFrame.add(signUpView, "SignUpView");

            // 초기 화면 설정
            CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
            cardLayout.show(mainFrame.getContentPane(), "LoginView");

            mainFrame.setVisible(true);
        });
    }
}
