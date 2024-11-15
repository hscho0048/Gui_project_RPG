package view;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import controller.GameController;
import model.Player;
import model.BossMonster;
import model.Opponent;

public class GameView extends JFrame {
	private static final long serialVersionUID = 1L;
	private JLabel playerImageLabel, opponentImageLabel;
	private JLabel playerInfoLabel, opponentInfoLabel;
	private JLabel stageLabel;
	private JProgressBar playerHealthBar, opponentHealthBar;
	private JButton attackButton, healButton, itemButton, defendButton, nextButton;
	private JPanel logPanel, stagePanel, opponentPanel;
	private JScrollPane logScrollPane;
	private Player player;
	private Opponent opponent;
	private GameController controller;
	private boolean playerTurn;

	public GameView(String playerName) {
		// 초기화
		player = new Player(playerName, 100, 1);
		opponent = new Opponent("상대", 100);
		controller = new GameController(player, opponent, this);
		playerTurn = true;

		setTitle("RPG 전투");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// 스테이지 레이블 초기화
		stageLabel = new JLabel("Stage 1"); // 스테이지 번호를 초기화
		stageLabel.setFont(new Font("Serif", Font.BOLD, 24));
		stageLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel stagePanel = new JPanel();
		stagePanel.add(stageLabel);
		stagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		// 로그 패널 초기화
		logPanel = new JPanel();
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
		logScrollPane = new JScrollPane(logPanel); // JScrollPane에 logPanel 추가

		// 플레이어 이미지와 정보 초기화
		playerImageLabel = new JLabel(new ImageIcon("resources/playerImage.jpg"));
		playerInfoLabel = new JLabel("플레이어: " + player.getName());
		playerHealthBar = new JProgressBar(0, 100);
		playerHealthBar.setValue(player.getHealth());
		playerHealthBar.setStringPainted(true);
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
		playerPanel.add(playerImageLabel);
		playerPanel.add(playerInfoLabel);
		playerPanel.add(playerHealthBar);

		// 상대방 이미지와 정보 초기화
		opponentPanel = new JPanel();
		opponentPanel.setLayout(new BoxLayout(opponentPanel, BoxLayout.Y_AXIS));

		opponentImageLabel = new JLabel(new ImageIcon("resources/opponentImage.jpg"));
		opponentInfoLabel = new JLabel("상대: " + opponent.getName());
		opponentHealthBar = new JProgressBar(0, opponent.getMaxHealth());
		opponentHealthBar.setValue(opponent.getHealth());
		opponentHealthBar.setStringPainted(true);

		opponentPanel.add(opponentImageLabel);
		opponentPanel.add(opponentInfoLabel);
		opponentPanel.add(opponentHealthBar);

		// 전투 버튼 초기화

		attackButton = new JButton("공격");
		attackButton.addActionListener(e -> handleAttackButton());
		attackButton.setEnabled(true);

		healButton = new JButton("회복 (남은 횟수: " + (player.getMaxHealCount() - player.getHealCount()) + ")");
		healButton.addActionListener(e -> handleHealButton());
		healButton.setEnabled(true);

		itemButton = new JButton("아이템 사용");
		itemButton.addActionListener(e -> showItemSelection());
		itemButton.setEnabled(true);

		defendButton = new JButton("방어");
		defendButton.addActionListener(e -> handleDefendButton());
		defendButton.setEnabled(true);

		nextButton = new JButton("다음");
		nextButton.addActionListener(e -> handleNextButton()); // 버튼에 이벤트 리스너 추가
		nextButton.setEnabled(false);

		// 버튼 패널 초기화
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 5)); // 5개 버튼을 가로로 배치
		buttonPanel.add(attackButton);
		buttonPanel.add(healButton);
		buttonPanel.add(itemButton);
		buttonPanel.add(defendButton);
		buttonPanel.add(nextButton);

		// 메인 패널 초기화
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(stagePanel); // 스테이지 패널
		mainPanel.add(playerPanel); // 플레이어 정보 패널
		mainPanel.add(Box.createVerticalStrut(10)); // 플레이어와 상대 간 간격 추가
		mainPanel.add(opponentPanel); // 상대 정보 패널

		// 레이아웃 설정
		add(mainPanel, BorderLayout.NORTH); // 메인 패널을 NORTH에 배치
		add(logScrollPane, BorderLayout.CENTER); // 로그 패널을 CENTER에 배치
		add(buttonPanel, BorderLayout.SOUTH); // 버튼 패널을 SOUTH에 배치

		setVisible(true);
	}

	public void showBossMonster(BossMonster bossMonster) {

		opponentInfoLabel.setText("보스: " + bossMonster.getName());
		opponentHealthBar.setMaximum(bossMonster.getMaxHealth());
		opponentHealthBar.setValue(bossMonster.getHealth());
		opponentHealthBar.setStringPainted(true);

		opponentImageLabel.setIcon(new ImageIcon("resources/bossImage.jpg")); // 보스 이미지로 변경
		opponentPanel.revalidate();
		opponentPanel.repaint();
	}
	public void updateBossHealthBar(BossMonster bossMonster) {
	    opponentHealthBar.setValue(bossMonster.getHealth()); // 체력 갱신
	    
	}
	

	private void handleAttackButton() {
		if (playerTurn) {
			boolean opponentDefeated = controller.playerAttack();
			flashImage(opponentImageLabel); // 상대 이미지 깜빡임
			attackButton.setEnabled(false);
			healButton.setEnabled(false);
			itemButton.setEnabled(false);
			defendButton.setEnabled(false);
			nextButton.setEnabled(false);
			playerTurn = false; // 턴을 상대에게 넘김
			scheduleOpponentTurn(); // 상대의 턴 자동 실행
			if (opponentDefeated) {
				nextButton.setEnabled(true); // 상대가 패배하면 다음 버튼 활성화
			}
		}
	}

	private void handleHealButton() {
		if (playerTurn) {
			controller.playerHeal(); // 플레이어 체력 회복
			updateHealButtonText(); // 버튼 텍스트 업데이트
			attackButton.setEnabled(false);
			healButton.setEnabled(false);
			itemButton.setEnabled(false);
			defendButton.setEnabled(false);
			nextButton.setEnabled(false);
			playerTurn = false; // 턴을 상대에게 넘김
			scheduleOpponentTurn(); // 상대의 턴 자동 실행
		}
	}

	private void handleDefendButton() {
		if (playerTurn) {
			controller.playerDefend(); // 플레이어 방어
			showDefenseShield(playerImageLabel); // 방어막 표시
			attackButton.setEnabled(false);
			healButton.setEnabled(false);
			itemButton.setEnabled(false);
			defendButton.setEnabled(false);
			nextButton.setEnabled(false);
			playerTurn = false; // 턴을 상대에게 넘김
			scheduleOpponentTurn(); // 상대의 턴 자동 실행
		}
	}

	private void handleNextButton() {
		// 새로운 스테이지로 넘어가기 전, 현재 스테이지 처리
		controller.nextStage();
		nextButton.setEnabled(false); // 다음 버튼 비활성화
		attackButton.setEnabled(true); // 공격 버튼 활성화
		healButton.setEnabled(true); // 회복 버튼 활성화
		itemButton.setEnabled(true); // 아이템 버튼 활성화
		defendButton.setEnabled(true); // 방어 버튼 활성화
		
	}

	private void showItemSelection() {
		// 아이템 목록 정의
		String[] items = { "체력 회복 물약", "공격력 증가 물약", "방어력 강화 물약" };

		// 사용자가 아이템을 선택하도록 다이얼로그 표시
		String selectedItem = (String) JOptionPane.showInputDialog(this, "사용할 아이템을 선택하세요:", "아이템 선택",
				JOptionPane.PLAIN_MESSAGE, null, items, items[0]);

		// 선택된 아이템에 따라 로직 처리
		if (selectedItem != null) {
			controller.useItem(selectedItem); // 선택된 아이템 사용
			if ("방어력 강화 물약".equals(selectedItem)) {
				showDefenseShield(playerImageLabel); // 방어막 표시
			}
			attackButton.setEnabled(false);
			healButton.setEnabled(false);
			itemButton.setEnabled(false);
			defendButton.setEnabled(false);
			nextButton.setEnabled(false);
			playerTurn = false; // 턴을 상대에게 넘김
			scheduleOpponentTurn(); // 상대의 턴 자동 실행
		}
	}

	private void scheduleOpponentTurn() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> {
					boolean opponentAction = controller.opponentTurn(); // 상대의 행동
					if (opponentAction) {
						flashImage(playerImageLabel); // 플레이어 이미지 깜빡임
					}
					playerTurn = true; // 플레이어 턴으로 변경
					attackButton.setEnabled(true);
					healButton.setEnabled(player.getHealCount() < player.getMaxHealCount()); // 회복 버튼 활성화 여부 설정
					itemButton.setEnabled(true);
					defendButton.setEnabled(true);
				});
			}
		}, 1000); // 2초 후 상대의 턴 실행
	}

	private void flashImage(JLabel imageLabel) {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			int count = 0;

			@Override
			public void run() {
				if (count >= 6) {
					timer.cancel();
					return;
				}
				imageLabel.setVisible(!imageLabel.isVisible());
				count++;
			}
		}, 0, 100); // 100ms 간격으로 깜빡임
	}

	private void showDefenseShield(JLabel imageLabel) {
		// 방어막 이미지를 로드하여 JLabel로 생성
		JLabel shieldLabel = new JLabel(new ImageIcon("resources/shield.png"));
		shieldLabel.setBounds(0, 0, imageLabel.getWidth(), imageLabel.getHeight());

		// 이미지 레이블의 부모 패널에 방어막 레이블 추가
		imageLabel.getParent().add(shieldLabel);
		imageLabel.getParent().setComponentZOrder(shieldLabel, 0); // 방어막 이미지를 맨 위에 표시
		imageLabel.getParent().repaint();

		// 2초 후 방어막 이미지를 제거
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> {
					shieldLabel.setVisible(false);
					imageLabel.getParent().remove(shieldLabel);
					imageLabel.getParent().revalidate();
					imageLabel.getParent().repaint();
				});
			}
		}, 2000); // 방어막이 2초 동안 표시됨
	}

	public void updateStatus(String status) {
		addLogMessage(status, true); // 로그 패널에 메시지 추가
	}

	public void updateStage(int stageNumber) {
		stageLabel.setText("Stage " + stageNumber); // 스테이지 번호를 업데이트
	}

	public void disableAttackButton() {
		attackButton.setEnabled(false); // 공격 버튼 비활성화
	}

	public void disableHealButton() {
		healButton.setEnabled(false); // 회복 버튼 비활성화
	}

	public void disableDefendButton() {
		defendButton.setEnabled(false); // 방어 버튼 비활성화
	}

	private void disableItemButton() {
		itemButton.setEnabled(false);
	}

	public void enableDefendButton() {
		defendButton.setEnabled(true); // 방어 버튼 활성화
	}

	public void updateHealButtonText() {
		healButton.setText("회복 (남은 횟수: " + (player.getMaxHealCount() - player.getHealCount()) + ")");
		if (player.getHealCount() >= player.getMaxHealCount()) {
			disableHealButton(); // 남은 횟수가 0일 경우 버튼 비활성화
		}
	}

	public void updatePlayerInfo() {
		playerInfoLabel.setText("플레이어: " + player.getName());
		playerHealthBar.setValue(player.getHealth());
	}

	public void updateOpponentInfo() {
		opponentInfoLabel.setText("상대: " + opponent.getName());

		// 상대가 보스 몬스터일 경우
		if (opponent instanceof BossMonster) {
			BossMonster boss = (BossMonster) opponent;

			// 보스 몬스터의 체력바 갱신
			opponentHealthBar.setMaximum(boss.getMaxHealth());
			opponentHealthBar.setValue(boss.getHealth());
		} else {
			// 일반 상대의 체력바 갱신
			opponentHealthBar.setMaximum(opponent.getMaxHealth());
			opponentHealthBar.setValue(opponent.getHealth());
		}

		
	}

	public void disableAllButtons() {
		attackButton.setEnabled(false);
		itemButton.setEnabled(false);
		defendButton.setEnabled(false);
		nextButton.setEnabled(false);
	}

	private void addLogMessage(String message, boolean isPlayer) {
		JLabel logLabel = new JLabel(message);
		if (isPlayer) {
			logLabel.setHorizontalAlignment(SwingConstants.LEFT);
		} else {
			logLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		logPanel.add(logLabel);
		logPanel.revalidate();
		logPanel.repaint();

		SwingUtilities.invokeLater(
				() -> logScrollPane.getVerticalScrollBar().setValue(logScrollPane.getVerticalScrollBar().getMaximum()));
	}

	public void enableNextButton() {
		nextButton.setEnabled(true); // 다음 버튼 활성화
	}
}
