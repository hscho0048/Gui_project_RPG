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
            // UserController 초기화
            UserController userController = new UserController();

            // JFrame 설정
            JFrame mainFrame = new JFrame("RPG Game");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(800, 600);
            mainFrame.setLayout(new CardLayout());

            // View 초기화
            LoginView loginView = new LoginView(userController, mainFrame);
            HomeView homeView = new HomeView(userController, mainFrame);
            SignUpView signUpView = new SignUpView(userController, mainFrame);

            // CardLayout에 View 추가 (로그인 후 추가 View 초기화)
            mainFrame.add(loginView, "LoginView");
            mainFrame.add(homeView, "HomeView");
            mainFrame.add(signUpView, "SignUpView");

            // 로그인 성공 시 Player 초기화 및 게임 시작
            loginView.setOnLoginSuccessListener((userId) -> {
                Player player = userController.getPlayerInfo(userId); // 데이터베이스에서 Player 생성
                if (player == null) {
                    JOptionPane.showMessageDialog(mainFrame, "플레이어 정보를 불러올 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                GameView gameView = new GameView("플레이어 이름", userController, player, mainFrame);
                ShopView shopView = new ShopView(player, userController, gameView, mainFrame);


                mainFrame.getContentPane().add(gameView, "GameView");
                mainFrame.getContentPane().add(shopView, "ShopView");

                CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
                cardLayout.show(mainFrame.getContentPane(), "HomeView");
            });

            // JFrame 표시
            mainFrame.setVisible(true);
        });
    }
}
