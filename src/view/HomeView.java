package view;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import controller.UserController;
import model.Player;
import model.Shop;

public class HomeView extends JFrame {
	private JButton battleButton, shopButton, characterSelectButton, rankingButton;
	private String playerName;
	private UserController userController;
	private Player player;
	private Shop shop;

	public HomeView(String playerName, UserController userController) {
		player = new Player(playerName, 100, 10);
		this.playerName = playerName;
		this.userController = userController;

		setTitle("RPG 홈");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(4, 1));

		// 대결 버튼
		battleButton = new JButton("대결");
		battleButton.addActionListener(e -> startBattle());

		// 상점 버튼
		shopButton = new JButton("상점");
		shopButton.addActionListener(e -> openShop());

		// 캐릭터 선택 버튼
		characterSelectButton = new JButton("캐릭터 선택");
		characterSelectButton.addActionListener(e -> selectCharacter());

		// 랭킹 보기 버튼
		rankingButton = new JButton("랭킹 보기");
		rankingButton.addActionListener(e -> displayRanking());

		add(battleButton);
		add(shopButton);
		add(characterSelectButton);
		add(rankingButton);

		setVisible(true);
	}

	private void startBattle() {
		// 대결 화면으로 이동 (GameView로 전환)
		new GameView(playerName, userController, player);
		dispose(); // 홈 화면 닫기
	}

	private void openShop() {
		shop = new Shop();
		// 상점 화면으로 이동 (ShopView로 전환)
		new ShopView(player, shop, this);
		dispose();
	}

	private void selectCharacter() {
		JOptionPane.showMessageDialog(this, "캐릭터 선택은 아직 구현되지 않았습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
	}

	// 랭킹을 가져와 표시하는 메서드
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

		// 랭킹 내용을 팝업창으로 표시
		JTextArea textArea = new JTextArea(rankingText.toString());
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(300, 200));

		JOptionPane.showMessageDialog(this, scrollPane, "랭킹 보기", JOptionPane.INFORMATION_MESSAGE);
	}

}