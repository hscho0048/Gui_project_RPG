package view;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

import java.awt.event.ActionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.List;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

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
	private int goldCount;

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
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

		// 스테이지 레이블 초기화
		stageLabel = new JLabel("Stage " + gameController.getCurrentStage());
		stageLabel.setFont(new Font("Dialog", Font.BOLD, 24));
		stageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// 턴 수 레이블 초기화
		turnCountLabel = new JLabel("턴 수: " + turnCount);
		turnCountLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		turnCountLabel.setHorizontalAlignment(SwingConstants.LEFT);
		turnCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

		goldCount = userController.getGold(player.getId());
		goldLabel = new JLabel("골드: " + goldCount);
		goldLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		goldLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		goldLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

		// 상단 패널 구성
		topPanel.add(stageLabel, BorderLayout.CENTER);
		topPanel.add(turnCountLabel, BorderLayout.WEST);
		topPanel.add(goldLabel, BorderLayout.EAST);
		topPanel.setPreferredSize(new Dimension(800, 50));

		// 전체 레이아웃에 상단 패널 추가
		add(topPanel, BorderLayout.NORTH);

		// 플레이어 정보 및 상대 패널 초기화
		initializePlayerPanel();
		initializeOpponentPanel();

		// 로그와 버튼 초기화
		initializeLogAndButtons();
	}

	// 로그와 버튼 초기화 메서드
	private void initializeLogAndButtons() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPanel.setPreferredSize(new Dimension(800, 60));
		// 버튼 스타일 변경
		Dimension buttonSize = new Dimension(120, 40);
		Font buttonFont = new Font("Dialog", Font.BOLD, 14);
		attackButton = new JButton("공격");
		skillAttackButton = new JButton("특수공격");
		defendButton = new JButton("방어");
		nextButton = new JButton("다음");
		homeButton = new JButton("홈으로");
		JButton[] buttons = { attackButton, skillAttackButton, defendButton, nextButton, homeButton };
		for (JButton button : buttons) {
			button.setPreferredSize(buttonSize);
			button.setFont(buttonFont);
			button.setFocusPainted(false);
			button.setBorder(BorderFactory.createRaisedBevelBorder());
			buttonPanel.add(button);
		}
		attackButton.addActionListener(e -> handleAttackButton(true));
		skillAttackButton.addActionListener(e -> handleAttackButton(false));
		defendButton.addActionListener(e -> playerDefend());
		nextButton.addActionListener(e -> nextStage());
		homeButton.addActionListener(e -> returnToHome());
		nextButton.setEnabled(false);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	// 메인 패널을 초기화하는 메서드
	private void initializePlayerPanel() {
		// 캐릭터 관련 UI 요소
		playerImageLabel = new JLabel(player.getCharacterImage());
		playerImageLabel.setPreferredSize(new Dimension(300, 240));
		playerImageLabel.setMaximumSize(new Dimension(300, 240));
		playerImageLabel.setMinimumSize(new Dimension(300, 240));
		playerImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		playerImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		playerImageLabel.setOpaque(true);
		playerInfoLabel = new JLabel("플레이어: " + player.getName());
		playerInfoLabel.setPreferredSize(new Dimension(300, 20));
		playerInfoLabel.setMaximumSize(new Dimension(300, 20));
		playerInfoLabel.setMinimumSize(new Dimension(300, 20));
		playerInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		playerInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		playerHealthBar = new JProgressBar();
		setupHealthBar(playerHealthBar, player.getHealth(), player.getMaxHealth());
		playerHealthBar.setPreferredSize(new Dimension(300, 20));
		playerHealthBar.setMaximumSize(new Dimension(300, 20));
		playerHealthBar.setMinimumSize(new Dimension(300, 20));
		playerHealthBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 인벤토리 패널 (독립적으로 관리)
		inventoryPanel = new JPanel();
		inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
		inventoryPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

		JScrollPane scrollPane = new JScrollPane(inventoryPanel);
		scrollPane.setPreferredSize(new Dimension(300, 180)); // 원하는 크기로 설정
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "인벤토리",
				TitledBorder.CENTER, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 16)));

		inventoryPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

		playerLayeredPane = new JLayeredPane();
		playerLayeredPane.setPreferredSize(new Dimension(300, 470));

		// 플레이어 패널 구성
		playerPanel = new JPanel();
		playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
		playerPanel.add(playerInfoLabel);
		playerPanel.add(playerHealthBar);
		playerPanel.add(playerImageLabel);
		playerPanel.setMaximumSize(new Dimension(300, 280));
		playerPanel.setOpaque(false);

		playerPanel.setBounds(0, 190, 300, 280);
		scrollPane.setBounds(0, 0, 300, 190);

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
		opponentImageLabel.setPreferredSize(new Dimension(500, 420));
		opponentImageLabel.setMaximumSize(new Dimension(500, 420));
		opponentImageLabel.setMinimumSize(new Dimension(500, 420));
		opponentImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		opponentImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		opponentInfoLabel = new JLabel(opponent.getName());
		opponentInfoLabel.setFont(new Font("Dialog", Font.BOLD, 24));
		opponentInfoLabel.setPreferredSize(new Dimension(500, 30));
		opponentInfoLabel.setMaximumSize(new Dimension(500, 30));
		opponentInfoLabel.setMinimumSize(new Dimension(500, 30));
		opponentInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		opponentInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		opponentHealthBar = new JProgressBar();
		opponentHealthBar.setPreferredSize(new Dimension(500, 20));
		opponentHealthBar.setMaximumSize(new Dimension(500, 20));
		opponentHealthBar.setMinimumSize(new Dimension(500, 20));
		opponentHealthBar.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 상대의 체력 바 설정
		setupHealthBar(opponentHealthBar, opponent.getHealth(), opponent.getMaxHealth());

		// 상대 패널 구성
		opponentLayeredPane = new JLayeredPane();
		opponentLayeredPane.setPreferredSize(new Dimension(500, 470));

		opponentPanel = new JPanel();
		opponentPanel.setLayout(new BoxLayout(opponentPanel, BoxLayout.Y_AXIS));
		opponentPanel.add(opponentInfoLabel);
		opponentPanel.add(opponentHealthBar);
		opponentPanel.add(opponentImageLabel);
		// opponent 패널 크기와 위치 설정
		opponentPanel.setBounds(0, 0, 500, 470);
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
		healthBar.setForeground(new Color(220, 20, 60)); // 크림슨 레드
		healthBar.setBackground(Color.LIGHT_GRAY);
		healthBar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		healthBar.setFont(new Font("Dialog", Font.BOLD, 12));
	}

	private void handleAttackButton(boolean is_attack) {
		if (!isPlayerTurn) {
			JOptionPane.showMessageDialog(this, "상대의 턴입니다. 기다려 주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (is_attack) {
			int damage = player.getAttackPower() + random.nextInt(50);
			int finalDamage = opponent.takeDamage(damage, false);

			showAttackEffect();
			showAttackDamage(finalDamage);
		} else {
			int damage = player.getSpecialAttackPower() + random.nextInt(50);
			int finalDamage = opponent.takeDamage(damage, true);

			showSpecialAttackEffect();
			showAttackDamage(finalDamage);
		}
		flashEffect(false);
		updateOpponentInfo();

		if (opponent.getHealth() <= 0) {
			disableAllButtons();
			homeButton.setEnabled(false);
			// 0.5초 딜레이
			Timer timer = new Timer(500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					hideOpponentWithAnimation();
					Timer timer2 = new Timer(1000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							nextButton.setEnabled(true);
							((Timer) e.getSource()).stop();
						}
					});
					timer2.setRepeats(false);
					timer2.start();
					((Timer) e.getSource()).stop();
				}
			});
			timer.setRepeats(false);
			timer.start();
		} else {
			endPlayerTurn();
		}
	}

	private void showAttackDamage(int finalDamage) {
		// 데미지 표시를 위한 JPanel 생성
		JPanel damagePanel = new JPanel();
		damagePanel.setLayout(null);
		damagePanel.setOpaque(false); // 패널 배경 투명하게
		damagePanel.setBounds(0, 0, 500, 540);

		JLabel damageLabel = new JLabel(String.valueOf(finalDamage));
		damageLabel.setFont(new Font("Arial", Font.BOLD, 32));
		damageLabel.setForeground(new Color(255, 0, 0));

		int labelWidth = 100;
		int labelHeight = 30;
		int x = (500 - labelWidth) / 2;
		int y = 540 / 2;
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

	private void showTakeDamageHeal(int num, boolean is_Damage) {
		// 데미지 표시를 위한 JPanel 생성
		JPanel damagePanel = new JPanel();
		damagePanel.setLayout(null);
		damagePanel.setOpaque(false); // 패널 배경 투명하게
		damagePanel.setBounds(0, 155, 300, 330);

		JLabel damageLabel = new JLabel(String.valueOf(num));
		damageLabel.setFont(new Font("Arial", Font.BOLD, 32));
		if (is_Damage)
			damageLabel.setForeground(new Color(255, 0, 255));
		else
			damageLabel.setForeground(new Color(0, 255, 0));

		int labelWidth = 100;
		int labelHeight = 30;
		int x = (300 - labelWidth) / 2;
		int y = 330 / 2;
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

		Timer timer = new javax.swing.Timer(600, (ActionEvent e) -> {
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
		int effectY = (opponentPanel.getHeight() - effectGif.getIconHeight()) * 3 / 4; // 시작 높이 수정
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
					Timer removeTimer = new javax.swing.Timer(50, (evt) -> {
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

	private void showPlayerEffect(String filename) {
		ImageIcon effectGif = new ImageIcon(getClass().getClassLoader().getResource(filename));
		JLabel effectLabel = new JLabel(effectGif);

		JPanel effectPanel = new JPanel(null);
		effectPanel.setOpaque(false);
		effectPanel.setBounds(0, 210, 300, 330);

		int effectX = (300 - effectGif.getIconWidth()) / 2;
		int effectY = (330 - effectGif.getIconHeight()) / 2;
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
			showTakeDamageHeal(actualHeal, false);
			showPlayerEffect("heal.gif");
			updatePlayerInfo();
		} else if ("공격력 증가 물약".equals(item.getName())) {
			player.increaseAttackPower(10);
			showPlayerEffect("abilityIncrease.gif");
		} else if ("방어력 증가 물약".equals(item.getName())) {
			player.increaseDefencePower(10);
			showPlayerEffect("abilityIncrease.gif");
		}

		// 아이템 수량 감소 및 버튼 제거
		item.decreaseQuantity();
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

	private void flashEffect(boolean is_player) {
		JLabel targetLabel = is_player ? playerImageLabel : opponentImageLabel;
		ImageIcon originalIcon = (ImageIcon) targetLabel.getIcon();
		BufferedImage bufferedImage = new BufferedImage(originalIcon.getIconWidth(), originalIcon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);

		Timer timer = new Timer(50, new ActionListener() {
			private int count = 0;
			private final int maxCount = 6;
			private boolean increasing = false;
			private float alpha = 1.0f;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (increasing) {
					alpha += 0.5f;
					if (alpha >= 1.0f) {
						alpha = 1.0f;
						increasing = false;
						count++;
					}
				} else {
					alpha -= 0.5f;
					if (alpha <= 0.3f) {
						alpha = 0.3f;
						increasing = true;
					}
				}

				Graphics2D g2d = bufferedImage.createGraphics();
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				g2d.drawImage(originalIcon.getImage(), 0, 0, null);
				g2d.dispose();

				targetLabel.setIcon(new ImageIcon(bufferedImage));

				if (count >= maxCount) {
					((Timer) e.getSource()).stop();
					targetLabel.setIcon(originalIcon);
				}
			}
		});
		timer.start();
	}

	private void endPlayerTurn() {
		isPlayerTurn = false;
		disableAllButtons();
		homeButton.setEnabled(false);
		scheduleOpponentTurn();
	}

	private void scheduleOpponentTurn() {
		javax.swing.Timer timer = new javax.swing.Timer(1500, (ActionEvent e) -> {
			int damage = opponent.getAttackPower() + random.nextInt(20) + 5;

			if (isPlayerDefending) {
				showPlayerEffect("guard.gif");
				showTakeDamageHeal(0, true);
				isPlayerDefending = false;
			} else {
				int finalDamage = player.takeDamage(damage, false);
				showPlayerEffect("opponentAttackEffect.gif");
				flashEffect(true);
				showTakeDamageHeal(finalDamage, true);
				updatePlayerInfo();

				if (player.getHealth() <= 0) {
					handlePlayerDeath();
					return;
				}
			}
			Timer timer2 = new Timer(500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					startPlayerTurn();
					((Timer) e.getSource()).stop();
				}
			});
			timer2.setRepeats(false);
			timer2.start();
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

	private void hideOpponentWithAnimation() {
		final int originalY = opponentImageLabel.getY(); // 현재 Y 위치 저장
		final int targetY = opponentPanel.getHeight(); // 목표 Y 위치 (패널 아래)

		Timer timer = new Timer(16, new ActionListener() { // 약 60fps로 부드러운 애니메이션
			int currentY = originalY;

			@Override
			public void actionPerformed(ActionEvent e) {
				currentY += 30; // 이동 속도 조절
				opponentImageLabel.setLocation(opponentImageLabel.getX(), currentY);

				if (currentY >= targetY) {
					((Timer) e.getSource()).stop();
					opponentImageLabel.setVisible(false);
				}
			}
		});
		timer.start();
	}

	private void nextStage() {
		nextButton.setEnabled(false);
		if (!gameController.isLastStage()) {
			gameController.nextStage();
			currentStage = gameController.getCurrentStage();
			opponent = gameController.getOpponent();

			updatePlayerInfo();
			updateOpponentInfo();
			updateStage(gameController.getCurrentStage());
			updateOpponentImage();

			// 0.5초 딜레이
			Timer timer = new Timer(500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					startPlayerTurn();
					((Timer) e.getSource()).stop();
				}
			});
			timer.setRepeats(false);
			timer.start();
		} else {
			endGame();
		}
	}

	private void updateOpponentImage() {
		ImageIcon icon;
		if (gameController.isBossMonster()) {
			icon = new ImageIcon(getClass().getClassLoader().getResource("bossImage.png"));
		} else {
			icon = new ImageIcon(getClass().getClassLoader().getResource("opponentImage.png"));
		}
		opponentImageLabel.setLocation(opponentImageLabel.getX(), opponentPanel.getHeight() / 2);

		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
		g2d.drawImage(icon.getImage(), 0, 0, null);
		opponentImageLabel.setIcon(new ImageIcon(bufferedImage));

		opponentImageLabel.setVisible(true);

		opponentPanel.revalidate();
		opponentPanel.repaint();

		Timer timer = new Timer(70, new ActionListener() {
			float alpha = 0.0f;

			@Override
			public void actionPerformed(ActionEvent e) {
				alpha += 0.1f;
				if (alpha >= 1.0f) {
					alpha = 1.0f;
					((Timer) e.getSource()).stop();
				}

				// 알파 합성 규칙 설정
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				g2d.drawImage(icon.getImage(), 0, 0, null);

				// 새로운 이미지 아이콘 생성 및 설정
				ImageIcon newIcon = new ImageIcon(bufferedImage);
				opponentImageLabel.setIcon(newIcon);
			}
		});
		timer.start();
	}

	private void updatePlayerInfo() {
		playerInfoLabel.setText("플레이어: " + player.getName());
		playerImageLabel.setIcon(player.getCharacterImage()); // 캐릭터 이미지 업데이트
		setupHealthBar(playerHealthBar, player.getHealth(), player.getMaxHealth());
	}

	private void updateOpponentInfo() {
		opponentInfoLabel.setText(opponent.getName());
		setupHealthBar(opponentHealthBar, opponent.getHealth(), opponent.getMaxHealth());
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
		// 게임 클리어 이펙트
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
