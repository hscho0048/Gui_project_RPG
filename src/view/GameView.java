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
    private JButton attackButton, nextButton, healButton;
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
        playerHealthBar = new JProgressBar(0, player.getHealth());
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
        opponentHealthBar = new JProgressBar(0, opponent.getHealth());
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

        healButton = new JButton("회복");
        healButton.addActionListener(e -> handleHealButton());
        healButton.setEnabled(true);

        nextButton = new JButton("다음");
        nextButton.setEnabled(false);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.add(attackButton);
        buttonPanel.add(healButton);
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
            nextButton.setEnabled(false);
            addLogMessage("플레이어가 공격했습니다.", true);
            playerTurn = false; // 턴을 상대에게 넘김
            scheduleOpponentTurn(); // 상대의 턴 자동 실행
        }
    }

    private void handleHealButton() {
        if (playerTurn) {
            controller.playerHeal(); // 플레이어 체력 회복
            addLogMessage("플레이어가 체력을 회복했습니다.", true);
            attackButton.setEnabled(false);
            healButton.setEnabled(false);
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
                    addLogMessage("상대의 차례입니다...", false);
                    boolean opponentAction = controller.opponentTurn(); // 상대가 공격 또는 회복 결정
                    if (opponentAction) {
                        addLogMessage("상대가 공격했습니다! 플레이어의 체력이 감소했습니다.", false);
                    } else {
                        addLogMessage("상대가 체력을 회복했습니다!", false);
                    }
                    playerTurn = true; // 플레이어 턴으로 변경
                    addLogMessage("플레이어의 턴입니다! 공격 또는 회복 버튼을 눌러주세요.", true);
                    attackButton.setEnabled(true);
                    healButton.setEnabled(true);
                });
            }
        }, 2000); // 2초 후 상대의 턴 실행
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

    public void updateStatus(String status) {
        addLogMessage(status, true);
    }

    public void updatePlayerInfo() {
        playerInfoLabel.setText("플레이어: " + player.getName());
        playerHealthBar.setValue(player.getHealth());
    }

    public void updateOpponentInfo() {
        opponentInfoLabel.setText("상대: " + opponent.getName());
        opponentHealthBar.setValue(opponent.getHealth());
    }

    public void disableAttackButton() {
        attackButton.setEnabled(false);
    }

    public void enableAttackButton() {
        attackButton.setEnabled(true);
    }

    public void enableNextButton() {
        nextButton.setEnabled(true);
    }

    public void switchToPlayerTurn() {
        attackButton.setEnabled(true);
        healButton.setEnabled(true);
        nextButton.setEnabled(false);
        addLogMessage("플레이어의 턴입니다! 공격 또는 회복 버튼을 눌러주세요.", true);
    }
}

