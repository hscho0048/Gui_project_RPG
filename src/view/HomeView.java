package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import controller.UserController;
import model.MyCharacter;
import model.Player;

public class HomeView extends JPanel {
	private JButton battleButton, shopButton, characterSelectButton;
	private UserController userController;
	private JFrame mainFrame;
	private MyCharacter myCharacter;
	private Player player;
	private JTable rankingTable;

	private GameView gameView;

	public HomeView(UserController userController, JFrame mainFrame, MyCharacter myCharacter, Player player) {
		this.userController = userController;
		this.mainFrame = mainFrame;
		this.myCharacter = myCharacter;
		this.player = player;

		setLayout(new BorderLayout()); // 전체 레이아웃 설정

		// 버튼 패널 초기화
		JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // 4개의 버튼과 간격 설정

		// 대결 버튼 초기화 (기본적으로 비활성화)
		battleButton = new JButton("대결");
		battleButton.setEnabled(false); // 기본적으로 비활성화
		battleButton.addActionListener(e -> showGameView());

		// 상점 버튼 초기화
		shopButton = new JButton("상점");
		shopButton.addActionListener(e -> showShopView());

		// 캐릭터 선택 버튼 초기화
		characterSelectButton = new JButton("캐릭터 선택");
		characterSelectButton.addActionListener(e -> {
			showCharacterView(); // 캐릭터 선택 화면 표시
		});

		// 버튼 패널에 버튼 추가
		buttonPanel.add(battleButton);
		buttonPanel.add(shopButton);
		buttonPanel.add(characterSelectButton);

		// 랭킹 패널 초기화
		JPanel rankingPanel = new JPanel(new BorderLayout());
		rankingPanel.setBorder(BorderFactory.createTitledBorder("랭킹"));

		// JTable 생성
		String[] columnNames = { "순위", "플레이어", "캐릭터", "구매 아이템", "턴수", "완료한 스테이지" };
		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
		rankingTable = new JTable(tableModel);
		rankingTable.setEnabled(false);

		// 스크롤 가능하도록 설정
		JScrollPane scrollPane = new JScrollPane(rankingTable);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// 랭킹 패널에 스크롤 패널 추가
		rankingPanel.add(scrollPane, BorderLayout.CENTER);

		// 전체 레이아웃에 패널 추가
		add(buttonPanel, BorderLayout.WEST);
		add(rankingPanel, BorderLayout.CENTER); // 랭킹 패널을 중앙에 추가

		// 초기 랭킹 로드
		updateRanking();
	}

	// 대결 버튼 활성화 메서드
	public void enableBattleButton() {
		battleButton.setEnabled(true);
	}

	// 대결 버튼 비활성화 메서드
	public void disableBattleButton() {
		battleButton.setEnabled(false);
	}

	// 캐릭터 선택 후 업데이트
	public boolean updateCharacter(String characterName) {
		int userId = player.getId(); // 로그인된 플레이어의 ID를 가져옵니다.

		if (userId == 0) {
			JOptionPane.showMessageDialog(this, "플레이어 ID가 잘못 설정되었습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		boolean updateSuccess = userController.updateCharacterName(userId, characterName); // 데이터베이스 업데이트

		return updateSuccess;
	}

	private void showGameView() {
		// GameView 생성 및 추가
		GameView gameView = new GameView(player.getName(), userController, player, mainFrame, this);
		mainFrame.getContentPane().add(gameView, "GameView");

		// GameView 화면으로 전환
		CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
		cardLayout.show(mainFrame.getContentPane(), "GameView");
	}

	private void showShopView() {
		// ShopView가 추가되어 있는지 확인
		for (Component component : mainFrame.getContentPane().getComponents()) {
			if (component instanceof ShopView) {
				// ShopView가 이미 추가되어 있으면 해당 화면으로 전환
				((ShopView) component).updatePlayerInfo();
				((ShopView) component).updateInventoryPanel();

				CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
				cardLayout.show(mainFrame.getContentPane(), "ShopView");
				return;
			}
		}

		// ShopView 생성 및 추가
		ShopView shopView = new ShopView(player, userController, gameView, mainFrame, this); // homeView 전달
		mainFrame.getContentPane().add(shopView, "ShopView");

		// ShopView 화면으로 전환
		CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
		cardLayout.show(mainFrame.getContentPane(), "ShopView");
	}

	private void showCharacterView() {
		for (Component component : mainFrame.getContentPane().getComponents()) {
			if (component instanceof CharacterView) {
				((CharacterView) component).updatePlayerInfo();
				CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
				cardLayout.show(mainFrame.getContentPane(), "CharacterView");
				return;
			}
		}

		CharacterView characterView = new CharacterView(player, myCharacter, this);
		mainFrame.getContentPane().add(characterView, "CharacterView");

		CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
		cardLayout.show(mainFrame.getContentPane(), "CharacterView");
	}

	public void updateRanking() {
		DefaultTableModel tableModel = (DefaultTableModel) rankingTable.getModel();
		tableModel.setRowCount(0); // 테이블 초기화

		ResultSet rs = userController.getRanking();
		if (rs == null) {
			JOptionPane.showMessageDialog(this, "랭킹 데이터를 불러오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				int rank = rs.getInt("rank");
				String playerName = rs.getString("player");
				String character = rs.getString("character") != null ? rs.getString("character") : "UNKNOWN";

				String items = rs.getString("items");
				String lastItem = "없음";
				if (items != null && !items.isEmpty()) {
					String[] itemArray = items.split(",");
					lastItem = itemArray[itemArray.length - 1].trim(); // 마지막 아이템 가져오기
				}

				int turns = rs.getInt("turns");
				int stage = rs.getInt("stage");

				tableModel.addRow(new Object[] { rank, playerName, character, lastItem, turns, stage });
			}

			if (!hasData) {
				JOptionPane.showMessageDialog(this, "아직 플레이한 사용자가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "랭킹 데이터를 처리하는 중 오류 발생: " + e.getMessage(), "오류",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}