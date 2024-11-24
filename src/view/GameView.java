package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;
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
	private JLabel playerInfoLabel, opponentInfoLabel, stageLabel, goldLabel;
	private JProgressBar playerHealthBar, opponentHealthBar;
	private JButton attackButton, skillAttackButton, defendButton, nextButton, homeButton;
	private JPanel playerPanel, opponentPanel;
	private Player player;
	private JLayeredPane playerLayeredPane;
	private Opponent opponent;
	private JLayeredPane opponentLayeredPane;
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

	public GameView(String playerName, UserController userController, Player player, JFrame mainFrame,
			HomeView homeView) {
		this.mainFrame = mainFrame;
		this.userController = userController;
		this.player = player;
		this.random = new Random();
		this.isPlayerDefending = false;
		this.homeView = homeView;

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
		
		 // 골드 레이블 초기화
	    goldLabel = new JLabel("골드: " + player.getMoney());
	    goldLabel.setFont(new Font("Serif", Font.PLAIN, 16));
	    goldLabel.setHorizontalAlignment(SwingConstants.RIGHT);
	    

		// 상단 패널 구성
		topPanel.add(stageLabel, BorderLayout.CENTER);
		topPanel.add(turnCountLabel, BorderLayout.WEST);
		topPanel.add(goldLabel, BorderLayout.EAST);

		// 전체 레이아웃에 상단 패널 추가
		add(topPanel, BorderLayout.NORTH);

		// 로그와 버튼 초기화
		initializeLogAndButtons();
	}

	// 로그와 버튼 초기화 메서드
	private void initializeLogAndButtons() {
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
		homeButton.addActionListener(e -> returnToHome());

		JPanel buttonPanel = new JPanel(new GridLayout(1, 6));
		buttonPanel.add(attackButton);
		buttonPanel.add(skillAttackButton);
		buttonPanel.add(defendButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(homeButton);

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

		JScrollPane scrollPane = new JScrollPane(inventoryPanel);
		scrollPane.setPreferredSize(new Dimension(300, 200)); // 원하는 크기로 설정
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createTitledBorder("인벤토리"));

		inventoryPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

		playerLayeredPane = new JLayeredPane();
		playerLayeredPane.setPreferredSize(new Dimension(300, 500));

		// 플레이어 패널 구성
		playerPanel = new JPanel();
		playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
		playerPanel.add(playerInfoLabel);
		playerPanel.add(playerHealthBar);
		playerPanel.add(playerImageLabel);
		playerPanel.setMaximumSize(new Dimension(300, 300));
		playerPanel.setOpaque(false);

		playerPanel.setBounds(0, 200, 300, 300);
		scrollPane.setBounds(0, 0, 300, 200);

		playerLayeredPane.add(playerPanel, JLayeredPane.DEFAULT_LAYER);
		playerLayeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);

		updateInventoryPanel(); // 인벤토리 갱신

		add(playerLayeredPane, BorderLayout.WEST); // 전체 레이아웃에 추가
	}

	private void updateInventoryPanel() {
		inventoryPanel.removeAll(); // 인벤토리 내용만 초기화

		List<Item> inventory = player.getInventory();
		if (inventory == null || inventory.isEmpty()) {
			JLabel emptyLabel = new JLabel("인벤토리가 비었습니다.");
			emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
			emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬
			inventoryPanel.add(emptyLabel);
		} else {
			for (Item item : inventory) {
				ImageIcon originalIcon = item.getImage();
				Image resizedImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
				ImageIcon resizedIcon = new ImageIcon(resizedImage);
				JButton itemButton = new JButton(item.getName() + "x" + item.getQuantity(), resizedIcon);
				itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
				itemButton.setHorizontalTextPosition(SwingConstants.CENTER);
				itemButton.setPreferredSize(new Dimension(150, 80));
				itemButton.setMaximumSize(new Dimension(150, 80));
				itemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				itemButton.addActionListener(e -> useItem(item, itemButton)); // 아이템 사용 이벤트
				inventoryPanel.add(itemButton);

				inventoryPanel.add(Box.createRigidArea(new Dimension(0, 5)));
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
		opponentLayeredPane = new JLayeredPane();
		opponentLayeredPane.setPreferredSize(new Dimension(500, 500));

		opponentPanel = new JPanel();
		opponentPanel.setLayout(new BoxLayout(opponentPanel, BoxLayout.Y_AXIS));
		opponentPanel.add(opponentInfoLabel);
		opponentPanel.add(opponentHealthBar);
		opponentPanel.add(opponentImageLabel);
		// opponent 패널 크기와 위치 설정
		opponentPanel.setBounds(0, 0, 500, 500);
		// 기본 레이어에 opponent 패널 추가
		opponentLayeredPane.add(opponentPanel, JLayeredPane.DEFAULT_LAYER);
		// 메인 레이아웃에 레이어드 패널 추가
		add(opponentLayeredPane, BorderLayout.CENTER);
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

		showAttackEffect();
		showAttackDamage(finalDamage);

		updateOpponentInfo();

		if (opponent.getHealth() <= 0) {
			// 승리 이펙트
			disableAllButtons();
			homeButton.setEnabled(false);
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

		showSpecialAttackEffect();
		showAttackDamage(finalDamage);

		updateOpponentInfo();
		if (opponent.getHealth() <= 0) {
			// 승리 이펙트
			disableAllButtons();
			homeButton.setEnabled(false);
			nextButton.setEnabled(true);
		} else {
			endPlayerTurn();
		}
	}

	private void showAttackDamage(int finalDamage) {
		// 데미지 표시를 위한 JPanel 생성
		JPanel damagePanel = new JPanel();
		damagePanel.setLayout(null);
		damagePanel.setOpaque(false); // 패널 배경 투명하게
		damagePanel.setBounds(0, 0, 500, 500);

		JLabel damageLabel = new JLabel(String.valueOf(finalDamage));
		damageLabel.setFont(new Font("Arial", Font.BOLD, 32));
		damageLabel.setForeground(new Color(255, 0, 0));

		int labelWidth = 100;
		int labelHeight = 30;
		int x = (500 - labelWidth) / 2;
		int y = 500 / 2;
		damageLabel.setBounds(x, y, labelWidth, labelHeight);

		damagePanel.add(damageLabel);

		opponentLayeredPane.add(damagePanel, JLayeredPane.POPUP_LAYER);

		Timer timer = new javax.swing.Timer(30, (ActionEvent e) -> {
			damageLabel.setLocation(damageLabel.getX(), damageLabel.getY() - 5);
			if (damageLabel.getY() < y - 100) {
				opponentLayeredPane.remove(damagePanel);
				opponentLayeredPane.repaint();
				((Timer) e.getSource()).stop();
			}
		});
		timer.start();
	}

	private void showtakeDamage(int finalDamage) {
		// 데미지 표시를 위한 JPanel 생성
		JPanel damagePanel = new JPanel();
		damagePanel.setLayout(null);
		damagePanel.setOpaque(false); // 패널 배경 투명하게
		damagePanel.setBounds(0, 150, 300, 300);

		JLabel damageLabel = new JLabel(String.valueOf(finalDamage));
		damageLabel.setFont(new Font("Arial", Font.BOLD, 32));
		damageLabel.setForeground(new Color(255, 0, 255));

		int labelWidth = 100;
		int labelHeight = 30;
		int x = (300 - labelWidth) / 2;
		int y = 300 / 2;
		damageLabel.setBounds(x, y, labelWidth, labelHeight);

		damagePanel.add(damageLabel);

		// 이펙트 패널을 팝업 레이어에 추가
		playerLayeredPane.add(damagePanel, JLayeredPane.POPUP_LAYER);

		Timer timer = new javax.swing.Timer(30, (ActionEvent e) -> {
			damageLabel.setLocation(damageLabel.getX(), damageLabel.getY() - 5);
			if (damageLabel.getY() < y - 100) {
				playerLayeredPane.remove(damagePanel);
				playerLayeredPane.repaint();
				((Timer) e.getSource()).stop();
			}
		});
		timer.start();
	}

	private void showAttackEffect() {
		// GIF 이펙트 생성
		ImageIcon effectGif = new ImageIcon(getClass().getClassLoader().getResource("attackEffect.gif"));
		JLabel effectLabel = new JLabel(effectGif);

		JPanel effectPanel = new JPanel();
		effectPanel.setLayout(null);
		effectPanel.setOpaque(false);
		effectPanel.setBounds(0, 0, opponentPanel.getWidth(), opponentPanel.getHeight());

		int effectX = (opponentPanel.getWidth() - effectGif.getIconWidth()) / 2;
		int effectY = (opponentPanel.getHeight() - effectGif.getIconHeight()) / 2;
		effectLabel.setBounds(effectX, effectY, effectGif.getIconWidth(), effectGif.getIconHeight());

		effectPanel.add(effectLabel);

		// 이펙트 패널을 팝업 레이어에 추가
		opponentLayeredPane.add(effectPanel, JLayeredPane.POPUP_LAYER);

		Timer timer = new javax.swing.Timer(700, (ActionEvent e) -> {
			opponentLayeredPane.remove(effectPanel);
			opponentLayeredPane.repaint();
			((Timer) e.getSource()).stop();
		});
		timer.setRepeats(false);
		timer.start();
	}

	private void showSpecialAttackEffect() {
		// GIF 이펙트 생성
		ImageIcon effectGif = new ImageIcon(getClass().getClassLoader().getResource("specialAttackEffect.gif"));
		JLabel effectLabel = new JLabel(effectGif);

		JPanel effectPanel = new JPanel();
		effectPanel.setLayout(null);
		effectPanel.setOpaque(false);
		effectPanel.setBounds(0, 0, opponentPanel.getWidth(), opponentPanel.getHeight());

		int startX = -effectGif.getIconWidth() + 200;
		int endX = (opponentPanel.getWidth() - effectGif.getIconWidth()) / 2;
		int effectY = (opponentPanel.getHeight() - effectGif.getIconHeight()) / 2;
		effectLabel.setBounds(startX, effectY, effectGif.getIconWidth(), effectGif.getIconHeight());

		effectPanel.add(effectLabel);

		// 이펙트 패널을 팝업 레이어에 추가
		opponentLayeredPane.add(effectPanel, JLayeredPane.POPUP_LAYER);

		// 애니메이션 타이머 (더 부드러운 움직임을 위해 더 짧은 딜레이 사용)
		Timer moveTimer = new javax.swing.Timer(16, null); // 약 60fps
		moveTimer.addActionListener(new AbstractAction() {
			private int currentX = startX;

			@Override
			public void actionPerformed(ActionEvent e) {
				currentX += 20; // 이동 속도 조절
				effectLabel.setLocation(currentX, effectY);

				if (currentX >= endX) {
					moveTimer.stop();
					// 이펙트 제거 타이머 시작
					Timer removeTimer = new javax.swing.Timer(300, (evt) -> {
						opponentLayeredPane.remove(effectPanel);
						opponentLayeredPane.repaint();
						((Timer) evt.getSource()).stop();
					});
					removeTimer.setRepeats(false);
					removeTimer.start();
				}
			}
		});

		moveTimer.start();
	}

	private void showDefendEffect() {
		ImageIcon effectGif = new ImageIcon(getClass().getClassLoader().getResource("guard.gif"));
		JLabel effectLabel = new JLabel(effectGif);

		JPanel effectPanel = new JPanel(null);
		effectPanel.setOpaque(false);
		effectPanel.setBounds(0, 200, 300, 300);

		int effectX = (300 - effectGif.getIconWidth()) / 2;
		int effectY = (300 - effectGif.getIconHeight()) / 2;
		effectLabel.setBounds(effectX, effectY, effectGif.getIconWidth(), effectGif.getIconHeight());

		effectPanel.add(effectLabel);

		// 이펙트 패널을 팝업 레이어에 추가
		playerLayeredPane.add(effectPanel, JLayeredPane.POPUP_LAYER);

		Timer timer = new javax.swing.Timer(700, (ActionEvent e) -> {
			playerLayeredPane.remove(effectPanel);
			playerLayeredPane.repaint();
			((Timer) e.getSource()).stop();
		});
		timer.setRepeats(false);
		timer.start();
	}

	private void showOpponentAttackEffect() {
		ImageIcon effectGif = new ImageIcon(getClass().getClassLoader().getResource("opponentAttackEffect.gif"));
		JLabel effectLabel = new JLabel(effectGif);

		JPanel effectPanel = new JPanel(null);
		effectPanel.setOpaque(false);
		effectPanel.setBounds(0, 200, 300, 300);

		int effectX = (300 - effectGif.getIconWidth()) / 2;
		int effectY = (300 - effectGif.getIconHeight()) / 2;
		effectLabel.setBounds(effectX, effectY, effectGif.getIconWidth(), effectGif.getIconHeight());

		effectPanel.add(effectLabel);

		// 이펙트 패널을 팝업 레이어에 추가
		playerLayeredPane.add(effectPanel, JLayeredPane.POPUP_LAYER);

		Timer timer = new javax.swing.Timer(400, (ActionEvent e) -> {
			playerLayeredPane.remove(effectPanel);
			playerLayeredPane.repaint();
			((Timer) e.getSource()).stop();
		});
		timer.setRepeats(false);
		timer.start();
	}

	private void useItem(Item item, JButton itemButton) {
		if ("체력 회복 물약".equals(item.getName())) {
			int healAmount = 30;
			int actualHeal = Math.min(healAmount, player.getMaxHealth() - player.getHealth());
			player.heal(actualHeal);
			// 체력 회복 이펙트
			updatePlayerInfo();
		} else if ("공격력 증가 물약".equals(item.getName())) {
			player.increaseAttackPower(10);
			// 공격력 증가 이펙트
		} else if ("방어력 증가 물약".equals(item.getName())) {
			player.increaseDefencePower(10);
			// 방어력 증가 이펙트 구현
		}

		// 아이템 수량 감소 및 버튼 제거
		item.decreaseQuantity(1);
		itemButton.setText(item.getName() + "x" + item.getQuantity());
		if (item.getQuantity() <= 0) {
			inventoryPanel.remove(itemButton); // 버튼 제거
		}
		inventoryPanel.revalidate();
		inventoryPanel.repaint();
	}

	private void playerDefend() {
		if (!isPlayerTurn) {
			JOptionPane.showMessageDialog(this, "상대의 턴입니다. 기다려 주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		isPlayerDefending = true;
		endPlayerTurn();
	}

	private void endPlayerTurn() {
		isPlayerTurn = false;
		disableAllButtons();
		homeButton.setEnabled(false);
		scheduleOpponentTurn();
	}

	private void scheduleOpponentTurn() {
		javax.swing.Timer timer = new javax.swing.Timer(2000, (ActionEvent e) -> {
			int damage = opponent.getAttackPower() + random.nextInt(20) + 5;

			if (isPlayerDefending) {
				showDefendEffect();
				showtakeDamage(0);
				isPlayerDefending = false;
			} else {
				int finalDamage = player.takeDamage(damage, false);
				showOpponentAttackEffect();
				showtakeDamage(finalDamage);
				updatePlayerInfo();

				if (player.getHealth() <= 0) {
					handlePlayerDeath();
					return;
				}
			}

			startPlayerTurn();
			((Timer) e.getSource()).stop(); // 타이머 중지
		});

		timer.setRepeats(false); // 한 번만 실행
		timer.start();
	}

	private void startPlayerTurn() {
		isPlayerTurn = true;
		turnCount++; // 턴 수 증가
		updateTurnCountLabel(); // 턴 수 레이블 업데이트
		enableAllButtons();
		homeButton.setEnabled(true);
		nextButton.setEnabled(false);
	}

	private void updateTurnCountLabel() {
		turnCountLabel.setText("턴 수: " + turnCount);
	}
	private void updateTotalGold() {
		updateGold();
		goldLabel.setText("골드: " + player.getMoney());
	}

	private void nextStage() {
		if (!gameController.isLastStage()) {
			gameController.nextStage();
			currentStage = gameController.getCurrentStage();
			opponent = gameController.getOpponent();

			updatePlayerInfo();
			updateOpponentInfo();
			updateTotalGold();
			updateStage(gameController.getCurrentStage());
			updateOpponentImage();
			startPlayerTurn();

			if (gameController.isBossMonster()) {
				// 보스 등장 이펙트 구현
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
	public void updateGold() {
	    int stageBonus = currentStage * 50; // 스테이지에 따른 보상 증가
	    player.increaseMoney(stageBonus); // 플레이어의 골드 증가
	    boolean updateSuccess = userController.updateGold(player.getId(), stageBonus);
	    if (updateSuccess) {
	        System.out.println("골드가 성공적으로 저장되었습니다.");
	    } else {
	        System.out.println("골드 저장 실패.");
	    }
	    System.out.println(stageBonus+"골드 획득! 현재 골드: " + player.getMoney());
	}

	private void updateStage(int stage) {
		stageLabel.setText("Stage " + stage);
	}

	private void handlePlayerDeath() {
		disableAllButtons();

		JOptionPane.showMessageDialog(this, "플레이어가 패배했습니다. 홈으로 돌아갑니다.");

		returnToHome();
	}

	private void endGame() {
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
	}

	private void returnToHome() {
		userController.updateTurns(player.getId(), turnCount);
		userController.updateCurrentStageInDatabase(player.getId(), currentStage);
		homeView.updateRanking(); // 홈 화면으로 돌아오며 랭킹 갱신
		restartGame();
		userController.clearUserItems(player.getId());
		homeView.disableBattleButton();
		CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
		cardLayout.show(mainFrame.getContentPane(), "HomeView");

	}

	public void disableAllButtons() {
		attackButton.setEnabled(false);
		skillAttackButton.setEnabled(false);
		defendButton.setEnabled(false);
		for (Component component : inventoryPanel.getComponents()) {
			if (component instanceof JButton) {
				component.setEnabled(false);
			}
		}
	}

	public void enableAllButtons() {
		attackButton.setEnabled(true);
		skillAttackButton.setEnabled(true);
		defendButton.setEnabled(true);
		for (Component component : inventoryPanel.getComponents()) {
			if (component instanceof JButton) {
				component.setEnabled(true);
			}
		}
	}
}
