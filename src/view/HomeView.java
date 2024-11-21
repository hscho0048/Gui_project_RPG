package view;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import controller.UserController;
import model.MyCharacter;
import model.Player;

public class HomeView extends JPanel {
    private JButton battleButton, shopButton, characterSelectButton;
    private JTextArea rankingTextArea; // 랭킹 정보를 표시할 텍스트 영역
    private UserController userController;
    private JFrame mainFrame;
    private MyCharacter myCharacter; // MyCharacter 객체
    private Player player;
    
    private GameView gameView;
    private ShopView shopView;

    public HomeView(UserController userController, JFrame mainFrame, MyCharacter myCharacter, Player player) {
        this.userController = userController;
        this.mainFrame = mainFrame;
        this.myCharacter = myCharacter;
        this.player = player;
        

        setLayout(new BorderLayout()); // 전체 레이아웃 설정

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1)); // 세로로 버튼 배치
        battleButton = new JButton("대결");
        battleButton.addActionListener(e -> showGameView());
        shopButton = new JButton("상점");
        shopButton.addActionListener(e -> showShopView());
        characterSelectButton = new JButton("캐릭터 선택");
        characterSelectButton.addActionListener(e -> showCharacterView()); // 캐릭터 선택

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
        if (player == null) {
            JOptionPane.showMessageDialog(this, "플레이어 정보가 없습니다. 다시 로그인해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // GameView가 추가되어 있는지 확인
        for (Component component : mainFrame.getContentPane().getComponents()) {
            if (component instanceof GameView) {
                // GameView가 이미 추가되어 있으면 해당 화면으로 전환
                CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
                cardLayout.show(mainFrame.getContentPane(), "GameView");
                return;
            }
        }

        // GameView 생성 및 추가
        GameView gameView = new GameView(player.getName(), userController, player, mainFrame);
        mainFrame.getContentPane().add(gameView, "GameView");

        // GameView 화면으로 전환
        CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
        cardLayout.show(mainFrame.getContentPane(), "GameView");
    }

    private void showShopView() {
    	initializeGameAndShopViews(); // 필요할 때 초기화
        if (player == null) {
            JOptionPane.showMessageDialog(this, "플레이어 정보가 없습니다. 다시 로그인해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ShopView가 추가되어 있는지 확인
        for (Component component : mainFrame.getContentPane().getComponents()) {
            if (component instanceof ShopView) {
                // ShopView가 이미 추가되어 있으면 해당 화면으로 전환
                CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
                cardLayout.show(mainFrame.getContentPane(), "ShopView");
                return;
            }
        }

        // ShopView 생성 및 추가
        ShopView shopView = new ShopView(player, userController, null, mainFrame);
        mainFrame.getContentPane().add(shopView, "ShopView");

        // ShopView 화면으로 전환
        CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
        cardLayout.show(mainFrame.getContentPane(), "ShopView");
    }
    private void showCharacterView() {
        // Player 정보가 null일 경우 기본 Player 생성
        if (player == null) {
            JOptionPane.showMessageDialog(this, "플레이어 정보가 없습니다. 기본 캐릭터로 설정됩니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            player = new Player(0, "Guest", 100); // ID: 0, 이름: "Guest", 초기 금액: 100으로 설정
        }

        // 이미 CharacterView가 추가되었는지 확인
        for (Component component : mainFrame.getContentPane().getComponents()) {
            if (component instanceof CharacterView) {
                // CharacterView가 이미 추가되어 있으면 해당 화면으로 전환
                CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
                cardLayout.show(mainFrame.getContentPane(), "CharacterView");
                return;
            }
        }

        // CharacterView 생성 및 추가
        CharacterView characterView = new CharacterView(player, myCharacter, this);
        mainFrame.getContentPane().add(characterView, "CharacterView");

        // CharacterView 화면으로 전환
        CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
        cardLayout.show(mainFrame.getContentPane(), "CharacterView");
    }


    public void setPlayer(Player player) {
        this.player = player;
        System.out.println("플레이어 정보가 설정되었습니다: " + player.getName());
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
    public void initializeGameAndShopViews() {
        if (gameView == null) {
            gameView = new GameView(player.getName(), userController, player, mainFrame);
            mainFrame.getContentPane().add(gameView, "GameView");
            System.out.println("GameView 초기화 완료");
        }

        if (shopView == null) {
            shopView = new ShopView(player, userController, gameView, mainFrame);
            mainFrame.getContentPane().add(shopView, "ShopView");
            System.out.println("ShopView 초기화 완료");
        }
    }


}

