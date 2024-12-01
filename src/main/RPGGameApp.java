package main;

import java.awt.CardLayout;
import javax.swing.*;
import controller.UserController;
import model.Player;
import model.MyCharacter;
import view.*;

public class RPGGameApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// UserController 초기화
			UserController userController = new UserController();
			MyCharacter globalMyCharacter = new MyCharacter(); // MyCharacter 전역 초기화

			// JFrame 설정
			JFrame mainFrame = new JFrame("RPG Game");
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setSize(800, 700);
			mainFrame.setLayout(new CardLayout());

			// View 초기화
			LoginView loginView = new LoginView(userController, mainFrame);
			SignUpView signUpView = new SignUpView(userController, mainFrame);

			// CardLayout에 기본 View 추가
			mainFrame.add(loginView, "LoginView");
			mainFrame.add(signUpView, "SignUpView");

			// 로그인 성공 시 실행
			loginView.setOnLoginSuccessListener((userId) -> {
				Player player = userController.getPlayerInfo(userId); // 데이터베이스에서 Player 생성
				if (player == null) {
			        throw new RuntimeException("플레이어 정보를 불러올 수 없음");
				}

				// HomeView, GameView, ShopView 추가
				HomeView homeView = new HomeView(userController, mainFrame, globalMyCharacter, player);
				GameView gameView = new GameView(player.getName(), userController, player, mainFrame, homeView);
				ShopView shopView = new ShopView(player, userController, gameView, mainFrame, homeView); // homeView 전달

				// 화면에 View 추가
				mainFrame.getContentPane().add(homeView, "HomeView");
				mainFrame.getContentPane().add(gameView, "GameView");
				mainFrame.getContentPane().add(shopView, "ShopView");

				// HomeView로 전환
				CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
				cardLayout.show(mainFrame.getContentPane(), "HomeView");
			});

			// JFrame 표시
			mainFrame.setVisible(true);
		});
	}
}
