package view;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import controller.GameController;
import controller.UserController;
import model.Player;
import model.Opponent;

public class GameView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel playerImageLabel, opponentImageLabel;
    private JLabel playerInfoLabel, opponentInfoLabel;
    private JProgressBar playerHealthBar, opponentHealthBar;
    private JButton attackButton, itemButton, nextButton;
    private JPanel logPanel;
    private Player player;
    private Opponent opponent;
    private GameController controller;
    private boolean playerTurn;

    public GameView(String playerName, UserController userController) {
        // 초기화
        player = new Player(playerName, 100, 1);
        opponent = new Opponent("상대", 100);
        controller = new GameController(player, opponent, this, userController);
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

        itemButton = new JButton("아이템 사용");
        itemButton.addActionListener(e -> showItemSelection());
        itemButton.setEnabled(true);

        nextButton = new JButton("다음");
        nextButton.setEnabled(false);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3)); // 3개 버튼이므로 GridLayout(1, 3)
        buttonPanel.add(attackButton);
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
            flashImage(opponentImageLabel); // 상대 이미지 깜빡임
            attackButton.setEnabled(false);
            itemButton.setEnabled(false);
            nextButton.setEnabled(false);
            playerTurn = false; // 턴을 상대에게 넘김
            scheduleOpponentTurn(); // 상대의 턴 자동 실행
        }
    }

    private void showItemSelection() {
        // 아이템 목록 정의
        String[] items = {"체력 회복 물약", "공격력 증가 물약"};
        
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
            controller.useItem(selectedItem);
            attackButton.setEnabled(false);
            itemButton.setEnabled(false);
            nextButton.setEnabled(false);
            playerTurn = false;
            scheduleOpponentTurn();
        }
    }

    private void scheduleOpponentTurn() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    boolean opponentAction = controller.opponentTurn();
                    if (opponentAction) {
                        flashImage(playerImageLabel);
                    }
                    playerTurn = true;
                    attackButton.setEnabled(true);
                    itemButton.setEnabled(true);
                });
            }
        }, 2000);
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
        }, 0, 100);
    }

    public void updateStatus(String status) {
        addLogMessage(status, true);
    }

    public void disableAttackButton() {
        attackButton.setEnabled(false);
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
        logLabel.setHorizontalAlignment(isPlayer ? SwingConstants.LEFT : SwingConstants.RIGHT);
        logPanel.add(logLabel);
        logPanel.revalidate();
        logPanel.repaint();
    }
    public void returnToHome() {
        JOptionPane.showMessageDialog(this, "플레이어가 패배했습니다. 홈으로 돌아갑니다.");
        this.dispose(); // 현재 게임 창 닫기
        HomeView homeView = new HomeView(player.getName(), controller.getUserController());
        homeView.setVisible(true);
    }
}
