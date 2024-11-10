package view;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import controller.GameController;
import model.Player;
import model.Opponent;

public class GameView extends JFrame {
    private JLabel playerImageLabel, opponentImageLabel;
    private JLabel playerInfoLabel, opponentInfoLabel;
    private JProgressBar playerHealthBar, opponentHealthBar;
    private JButton attackButton, nextButton, healButton, itemButton;
    private JPanel logPanel;
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

        // 로그 패널
        logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(logPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 플레이어 이미지와 정보
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

        // 상대방 이미지와 정보
        opponentImageLabel = new JLabel(new ImageIcon("resources/opponentImage.jpg"));
        opponentInfoLabel = new JLabel("상대: " + opponent.getName());
        opponentHealthBar = new JProgressBar(0, 100);
        opponentHealthBar.setValue(opponent.getHealth());
        opponentHealthBar.setStringPainted(true);
        JPanel opponentPanel = new JPanel();
        opponentPanel.setLayout(new BoxLayout(opponentPanel, BoxLayout.Y_AXIS));
        opponentPanel.add(opponentImageLabel);
        opponentPanel.add(opponentInfoLabel);
        opponentPanel.add(opponentHealthBar);

        // 전투 버튼
        attackButton = new JButton("공격");
        attackButton.addActionListener(e -> handleAttackButton());
        attackButton.setEnabled(true);

        healButton = new JButton("회복 (남은 횟수: " + (player.getMaxHealCount() - player.getHealCount()) + ")");
        healButton.addActionListener(e -> handleHealButton());
        healButton.setEnabled(true);

        itemButton = new JButton("아이템 사용");
        itemButton.addActionListener(e -> showItemSelection());
        itemButton.setEnabled(true);

        nextButton = new JButton("다음");
        nextButton.setEnabled(false);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4)); // 4개 버튼이므로 GridLayout(1, 4)
        buttonPanel.add(attackButton);
        buttonPanel.add(healButton);
        buttonPanel.add(itemButton);
        buttonPanel.add(nextButton);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1));
        mainPanel.add(playerPanel);
        mainPanel.add(opponentPanel);

        add(mainPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleAttackButton() {
        if (playerTurn) {
            controller.playerAttack();
            attackButton.setEnabled(false);
            healButton.setEnabled(false);
            itemButton.setEnabled(false);
            nextButton.setEnabled(false);
            playerTurn = false; // 턴을 상대에게 넘김
            scheduleOpponentTurn(); // 상대의 턴 자동 실행
        }
    }

    private void handleHealButton() {
        if (playerTurn) {
            controller.playerHeal(); // 플레이어 체력 회복
            updateHealButtonText(); // 버튼 텍스트 업데이트
            attackButton.setEnabled(false);
            healButton.setEnabled(false);
            itemButton.setEnabled(false);
            nextButton.setEnabled(false);
            playerTurn = false; // 턴을 상대에게 넘김
            scheduleOpponentTurn(); // 상대의 턴 자동 실행
        }
    }

    public void updateHealButtonText() {
        healButton.setText("회복 (남은 횟수: " + (player.getMaxHealCount() - player.getHealCount()) + ")");
        if (player.getHealCount() >= player.getMaxHealCount()) {
            healButton.setEnabled(false); // 남은 횟수가 0일 경우 버튼 비활성화
        }
    }

    private void showItemSelection() {
        // 아이템 목록 정의
        String[] items = {"체력 회복 물약", "공격력 증가 물약", "방어력 강화 물약"};
        
        // 사용자가 아이템을 선택하도록 다이얼로그 표시
        String selectedItem = (String) JOptionPane.showInputDialog(
            this,
            "사용할 아이템을 선택하세요:",
            "아이템 선택",
            JOptionPane.PLAIN_MESSAGE,
            null,
            items,
            items[0]
        );

        // 선택된 아이템에 따라 로직 처리
        if (selectedItem != null) {
            controller.useItem(selectedItem); // 선택된 아이템 사용
            attackButton.setEnabled(false);
            healButton.setEnabled(false);
            itemButton.setEnabled(false);
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
                    playerTurn = true; // 플레이어 턴으로 변경
                    attackButton.setEnabled(true);
                    healButton.setEnabled(player.getHealCount() < player.getMaxHealCount()); // 회복 버튼 활성화 여부 설정
                    itemButton.setEnabled(true);
                });
            }
        }, 2000); // 2초 후 상대의 턴 실행
    }

    public void updateStatus(String status) {
        addLogMessage(status, true); // 로그 패널에 메시지 추가
    }
    
    public void disableHealButton() {
        healButton.setEnabled(false); // 회복 버튼 비활성화
    }

    public void disableAttackButton() {
        attackButton.setEnabled(false); // 공격 버튼 비활성화
    }

    public void updatePlayerInfo() {
        playerInfoLabel.setText("플레이어: " + player.getName());
        playerHealthBar.setValue(player.getHealth());
    }

    public void updateOpponentInfo() {
        opponentInfoLabel.setText("상대: " + opponent.getName());
        opponentHealthBar.setValue(opponent.getHealth());
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
    }
}
