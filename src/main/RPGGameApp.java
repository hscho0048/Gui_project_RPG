package main;

import java.awt.CardLayout;
import javax.swing.*;
import controller.UserController;
import model.Player;
import model.Shop;
import model.MyCharacter;
import view.*;

public class RPGGameApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // UserController 초기화
            UserController userController = new UserController();
            MyCharacter myCharacter = new MyCharacter(); // 캐릭터 데이터 초기화

            // JFrame 설정
            JFrame mainFrame = new JFrame("RPG Game");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(800, 600);
            mainFrame.setLayout(new CardLayout());

            // View 초기화
            LoginView loginView = new LoginView(userController, mainFrame);
            HomeView homeView = new HomeView(userController, mainFrame, myCharacter); // MyCharacter 전달
            SignUpView signUpView = new SignUpView(userController, mainFrame);
            // CardLayout에 View 추가
            mainFrame.add(loginView, "LoginView");
            mainFrame.add(homeView, "HomeView");
            mainFrame.add(signUpView, "SignUpView");

            // 로그인 성공 시 Player 초기화 및 게임 시작
            loginView.setOnLoginSuccessListener((userId) -> {
                Player player = userController.getPlayerInfo(userId); // 데이터베이스에서 Player 정보를 가져옴
                if (player == null) {
                    // 기본값으로 Player 객체 생성
                    player = new Player("기본 플레이어", 100, 10); // 이름, 체력, 공격력 기본값 설정
                    JOptionPane.showMessageDialog(mainFrame, "플레이어 정보를 불러올 수 없으므로 기본값으로 설정합니다.", "알림", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // HomeView에 Player 설정
                homeView.setPlayer(player);

             // CharacterView 초기화
                CharacterView charView = new CharacterView(player, myCharacter, homeView); // 변수 이름 변경
                mainFrame.getContentPane().add(charView, "CharacterView");

                // CharacterView로 화면 전환
                CardLayout layout = (CardLayout) mainFrame.getContentPane().getLayout();
                layout.show(mainFrame.getContentPane(), "HomeView");
            });


            // JFrame 표시
            mainFrame.setVisible(true);
        });
    }
}
