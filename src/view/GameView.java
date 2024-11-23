package view;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;
import java.util.List;

import controller.GameController;
import controller.UserController;
import model.Player;
import model.Item;
import model.Opponent;

public class GameView extends JPanel {
	private HomeView homeView; // HomeView 참조
	private ShopView shopView; // ShopView 참조
	private JLabel playerImageLabel, opponentImageLabel;
	private JLabel playerInfoLabel, opponentInfoLabel, stageLabel;
	private JProgressBar playerHealthBar, opponentHealthBar;
	private JButton attackButton, skillAttackButton, defendButton, nextButton, homeButton;
	private JPanel logPanel;
	private JScrollPane logScrollPane;
	private Player player;
	private Opponent opponent;
	private UserController userController;
	private Random random;
	private boolean isPlayerTurn = true; // 턴제 구현
	private boolean isPlayerDefending;
	private JFrame mainFrame;
	private GameController gameController;
	private JPanel inventoryPanel; // 아이템 인벤토리 패널
	private JLabel turnCountLabel; // 턴 수를 표시할 레이블
	private int turnCount = 1; // 초기 턴 수
	private int currentStage = 1; // 초기 스테이지 수

	public GameView(String playerName, UserController userController, Player player, JFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.userController = userController;
		this.player = player;
		this.random = new Random();
		this.isPlayerDefending = false;

		// GameController 초기화
		this.gameController = new GameController(player, new Opponent("상대", 100, 10, 10, 10, 10));

		// 상대 정보 가져오기
		this.opponent = gameController.getOpponent();

		setLayout(new BorderLayout());

		// UI 초기화
		initializeUI();

		// 초기 정보 업데이트
		updatePlayerInfo();
		updateOpponentInfo();
		updateStage(gameController.getCurrentStage());
	}

	private void initializeUI() {
		// 상단 패널 (스테이지 및 플레이어 정보 포함)
		JPanel topPanel = new JPanel(new BorderLayout());

		// 스테이지 레이블 초기화
		stageLabel = new JLabel("Stage " + gameController.getCurrentStage());
		stageLabel.setFont(new Font("Serif", Font.BOLD, 24));
		stageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// 플레이어 정보 및 상대 패널 초기화
		initializePlayerPanel();
		initializeOpponentPanel();
		// 턴 수 레이블 초기화
		turnCountLabel = new JLabel("턴 수: " + turnCount);
		turnCountLabel.setFont(new Font("Serif", Font.PLAIN, 16));
		turnCountLabel.setHorizontalAlignment(SwingConstants.LEFT);

		// 상단 패널 구성
		topPanel.add(stageLabel, BorderLayout.CENTER);
		topPanel.add(turnCountLabel, BorderLayout.WEST);

		// 전체 레이아웃에 상단 패널 추가
		add(topPanel, BorderLayout.NORTH);

		// 로그와 버튼 초기화
		initializeLogAndButtons();
	}

	// 로그와 버튼 초기화 메서드
	private void initializeLogAndButtons() {
		// 로그 패널
		logPanel = new JPanel();
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
		logScrollPane = new JScrollPane(logPanel);
		logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// 버튼 패널
		attackButton = new JButton("공격");
		attackButton.addActionListener(e -> handleAttackButton());

		skillAttackButton = new JButton("특수공격");
		skillAttackButton.addActionListener(e -> handleSkillAttackButton()); // 특수공격 버튼 클릭 이벤트 추가

		defendButton = new JButton("방어");
		defendButton.addActionListener(e -> playerDefend());

		nextButton = new JButton("다음");
		nextButton.addActionListener(e -> nextStage());
		nextButton.setEnabled(false);

		homeButton = new JButton("홈으로");
		homeButton.addActionListener(e -> returnToHomeRestart());

		JPanel buttonPanel = new JPanel(new GridLayout(1, 6));
		buttonPanel.add(attackButton);
		buttonPanel.add(skillAttackButton);
		buttonPanel.add(defendButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(homeButton);

		add(logScrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	// 메인 패널을 초기화하는 메서드
	private void initializePlayerPanel() {
	    // 캐릭터 관련 UI 요소
	    playerImageLabel = new JLabel(player.getCharacterImage());
	    playerInfoLabel = new JLabel("플레이어: " + player.getName() + " | 체력: " + player.getHealth());
	    playerHealthBar = new JProgressBar();
	    setupHealthBar(playerHealthBar, player.getHealth(), player.getMaxHealth());

	    // 인벤토리 패널 (독립적으로 관리)
	    inventoryPanel = new JPanel();
	    inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
	    inventoryPanel.setBorder(BorderFactory.createTitledBorder("인벤토리"));
	    updateInventoryPanel(); // 인벤토리 갱신

	    // 플레이어 패널 구성
	    JPanel playerPanel = new JPanel();
	    playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
	    playerPanel.add(playerImageLabel);
	    playerPanel.add(playerInfoLabel);
	    playerPanel.add(playerHealthBar);

	    // 메인 패널에 추가
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    mainPanel.add(playerPanel, BorderLayout.NORTH); // 캐릭터 정보
	    mainPanel.add(new JScrollPane(inventoryPanel), BorderLayout.CENTER); // 인벤토리 정보

	    add(mainPanel, BorderLayout.WEST); // 전체 레이아웃에 추가
	}

	private void updateInventoryPanel() {
	    inventoryPanel.removeAll(); // 인벤토리 내용만 초기화

	    List<Item> inventory = player.getInventory();
	    if (inventory == null || inventory.isEmpty()) {
	        JLabel emptyLabel = new JLabel("인벤토리가 비었습니다.");
	        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        inventoryPanel.add(emptyLabel);
	    } else {
	        for (Item item : inventory) {
	            JButton itemButton = new JButton(item.getName(), item.getImage());
	            itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
	            itemButton.setHorizontalTextPosition(SwingConstants.CENTER);
	            itemButton.setPreferredSize(new Dimension(80, 100));
	            itemButton.addActionListener(e -> useItem(item, itemButton)); // 아이템 사용 이벤트
	            inventoryPanel.add(itemButton);
	        }
	    }

	    inventoryPanel.revalidate(); // 인벤토리 갱신
	    inventoryPanel.repaint();
	}

	private void initializeOpponentPanel() {
		opponentImageLabel = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("opponentImage.png")));
		opponentInfoLabel = new JLabel("상대: " + opponent.getName() + " | 체력: " + opponent.getHealth());
		opponentHealthBar = new JProgressBar();

		// 상대의 체력 바 설정
		setupHealthBar(opponentHealthBar, opponent.getHealth(), opponent.getMaxHealth());

		// 상대 패널 구성
		JPanel opponentPanel = new JPanel();
		opponentPanel.setLayout(new BoxLayout(opponentPanel, BoxLayout.Y_AXIS));
		opponentPanel.add(opponentImageLabel);
		opponentPanel.add(opponentInfoLabel);
		opponentPanel.add(opponentHealthBar);

		// 레이아웃에 상대 패널 추가
		add(opponentPanel, BorderLayout.EAST);
	}

	public void addItemToInventory(Item item) {
		JButton itemButton = new JButton(item.getName());
		itemButton.setIcon(item.getImage());
		itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		itemButton.setHorizontalTextPosition(SwingConstants.CENTER);
		itemButton.setToolTipText("잔여량: " + item.getQuantity());

		itemButton.addActionListener(e -> useItem(item, itemButton)); // 클릭 시 아이템 사용
		inventoryPanel.add(itemButton); // 인벤토리에 추가
		inventoryPanel.revalidate();
		inventoryPanel.repaint();
	}

	private void setupHealthBar(JProgressBar healthBar, int currentHealth, int maxHealth) {
		healthBar.setMaximum(maxHealth);
		healthBar.setValue(currentHealth);
		healthBar.setString(currentHealth + " / " + maxHealth);
		healthBar.setStringPainted(true);
	}

	private void handleAttackButton() {
		if (!isPlayerTurn) {
			JOptionPane.showMessageDialog(this, "상대의 턴입니다. 기다려 주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int damage = player.getAttackPower() + random.nextInt(50);
		int finalDamage = opponent.takeDamage(damage, false);
		updateStatus("플레이어가 " + finalDamage + " 데미지를 입혔습니다!");

		updateOpponentInfo();

		if (opponent.getHealth() <= 0) {
			updateStatus("플레이어가 승리했습니다!");
			disableAllButtons();
			nextButton.setEnabled(true);
		} else {
			endPlayerTurn();
		}
	}

	private void handleSkillAttackButton() {
		if (!isPlayerTurn) {
			JOptionPane.showMessageDialog(this, "상대의 턴입니다. 기다려 주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int damage = player.getSpecialAttackPower() + random.nextInt(50);
		int finalDamage = opponent.takeDamage(damage, true);
		updateStatus("플레이어가 " + finalDamage + " 데미지를 입혔습니다!");

		updateOpponentInfo();
		if (opponent.getHealth() <= 0) {
			updateStatus("플레이어가 승리했습니다!");
			disableAllButtons();
			nextButton.setEnabled(true);
		} else {
			endPlayerTurn();
		}
	}

	private void useItem(Item item, JButton itemButton) {
		if ("체력 회복 물약".equals(item.getName())) {
			int healAmount = 30;
			int actualHeal = Math.min(healAmount, player.getMaxHealth() - player.getHealth());
			player.heal(actualHeal);
			updateStatus(item.getName() + "을(를) 사용하여 체력을 " + actualHeal + " 회복했습니다!");
			updatePlayerInfo();
		} else if ("공격력 증가 물약".equals(item.getName())) {
			player.increaseAttackPower(10);
			updateStatus(item.getName() + "을(를) 사용하여 공격력이 증가했습니다!");
		} else if ("방어력 증가 물약".equals(item.getName())) {
			player.increaseDefencePower(10);
			updateStatus(item.getName() + "을(를) 사용하여 방어력이 증가했습니다!");
		}

		// 아이템 수량 감소 및 버튼 제거
		item.decreaseQuantity(1);
		if (item.getQuantity() <= 0) {
			inventoryPanel.remove(itemButton); // 버튼 제거
			inventoryPanel.revalidate();
			inventoryPanel.repaint();
		}
	}

	private void playerDefend() {
		if (!isPlayerTurn) {
			JOptionPane.showMessageDialog(this, "상대의 턴입니다. 기다려 주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		isPlayerDefending = true;
		updateStatus("플레이어가 방어 자세를 취했습니다!");
		endPlayerTurn();
	}

	private void endPlayerTurn() {
		isPlayerTurn = false;
		disableAllButtons();
		homeButton.setEnabled(false);
		scheduleOpponentTurn();
	}

	private void scheduleOpponentTurn() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> {
					int damage = opponent.getAttackPower() + random.nextInt(10) + 5;

					if (isPlayerDefending) {
						updateStatus("방어 성공! 상대의 공격이 무효화되었습니다.");
						isPlayerDefending = false;
					} else {
						player.takeDamage(damage);
						updateStatus("상대가 " + damage + " 데미지를 입혔습니다!");
						updatePlayerInfo();

						if (player.getHealth() <= 0) {
							handlePlayerDeath();
							return;
						}
					}

					startPlayerTurn();
				});
			}
		}, 2000);
	}

	private void startPlayerTurn() {
		isPlayerTurn = true;
		turnCount++; // 턴 수 증가
		updateTurnCountLabel(); // 턴 수 레이블 업데이트
		enableAllButtons();
		homeButton.setEnabled(true);
		nextButton.setEnabled(false);
		updateStatus("플레이어의 턴입니다!");
	}

	private void updateTurnCountLabel() {
		turnCountLabel.setText("턴 수: " + turnCount);
	}

	private void nextStage() {
		if (!gameController.isLastStage()) {
			gameController.nextStage();
			opponent = gameController.getOpponent();

			updatePlayerInfo();
			updateOpponentInfo();
			updateStage(gameController.getCurrentStage());
			updateOpponentImage();
			startPlayerTurn();

			if (gameController.isBossMonster()) {
				updateStatus("보스 몬스터가 등장했습니다: " + opponent.getName());
			}
		} else {
			endGame();
		}
	}

	private void updateOpponentImage() {
		if (gameController.isBossMonster()) {
			opponentImageLabel.setIcon(new ImageIcon(getClass().getClassLoader().getResource("bossImage.png")));
		} else {
			opponentImageLabel.setIcon(new ImageIcon(getClass().getClassLoader().getResource("opponentImage.png")));
		}
	}

	private void updatePlayerInfo() {
		playerInfoLabel.setText("플레이어: " + player.getName());
		playerImageLabel.setIcon(player.getCharacterImage()); // 캐릭터 이미지 업데이트
		setupHealthBar(playerHealthBar, player.getHealth(), player.getMaxHealth());
	}

	private void updateOpponentInfo() {
		opponentInfoLabel.setText("상대: " + opponent.getName());
		setupHealthBar(opponentHealthBar, opponent.getHealth(), opponent.getMaxHealth());
	}

	private void updateStage(int stage) {
		stageLabel.setText("Stage " + stage);
	}

	private void updateStatus(String status) {
		JLabel logLabel = new JLabel(status);
		logPanel.add(logLabel);
		logPanel.revalidate();
		logPanel.repaint();
	}

	private void handlePlayerDeath() {
		updateStatus("플레이어가 패배했습니다!");
		disableAllButtons();

		JOptionPane.showMessageDialog(this, "플레이어가 패배했습니다. 홈으로 돌아갑니다.");

		userController.updateTurns(player.getName(), turnCount);

		// 플레이어 상태 초기화
		player.reset();

		returnToHome();
	}
	

	private void endGame() {
	    updateStatus("게임이 종료되었습니다!");
	    userController.updateTurns(player.getName(), turnCount); // 나의 턴수로 점수 업데이트

	    // 게임 종료 후 스테이지 업데이트
	    userController.updateStage(player.getName(), currentStage); // 현재 스테이지 업데이트

	    // 게임 종료 후 턴수를 HomeView에 전달하여 업데이트
	    homeView.updateTurnCount(turnCount); // HomeView에서 턴수를 업데이트하는 메서드 호출
	    homeView.updateStage(currentStage); // HomeView에서 스테이지를 업데이트하는 메서드 호출
	    homeView.updateRanking(); // HomeView에서 랭킹을 갱신하는 메서드 호출

	    returnToHome();
	}


	public void restartGame() {
		gameController.resetGame(); // 게임 상태 초기화
		opponent = gameController.getOpponent(); // 초기화된 상대 가져오기
		turnCount = 1;
		updatePlayerInfo(); // 플레이어 정보 갱신
		updateOpponentInfo(); // 상대 정보 갱신
		updateTurnCountLabel();
		updateStage(gameController.getCurrentStage()); // 스테이지 정보 갱신
		nextButton.setEnabled(false); // "다음" 버튼 비활성화
		clearLog(); // 로그 초기화
	}

	private void clearLog() {
		logPanel.removeAll(); // 로그 패널의 모든 컴포넌트 제거
		logPanel.revalidate(); // 레이아웃 갱신
		logPanel.repaint(); // 화면 갱신
	}

	private void returnToHomeRestart() {
		restartGame();
		CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
		cardLayout.show(mainFrame.getContentPane(), "HomeView");
		homeView.updateRanking();       // 홈 화면으로 돌아오며 랭킹 갱신
	}

	private void returnToHome() {
		CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
		cardLayout.show(mainFrame.getContentPane(), "HomeView");
	}

	public void disableAllButtons() {
		attackButton.setEnabled(false);
		skillAttackButton.setEnabled(false);
		defendButton.setEnabled(false);
	}

	public void enableAllButtons() {
		attackButton.setEnabled(true);
		skillAttackButton.setEnabled(true);
		defendButton.setEnabled(true);
	}
}
