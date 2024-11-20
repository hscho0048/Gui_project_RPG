package view;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.Iterator;

import controller.UserController;
import model.Player;
import model.BossMonster;
import model.Item;
import model.Opponent;

public class GameView extends JPanel {
    private JLabel playerImageLabel, opponentImageLabel;
    private JLabel playerInfoLabel, opponentInfoLabel, stageLabel;
    private JProgressBar playerHealthBar, opponentHealthBar;
    private JButton attackButton, itemButton, defendButton, nextButton, homeButton;
    private JPanel logPanel;
    private JScrollPane logScrollPane;
    private Player player;
    private Opponent opponent;
    private UserController userController;
    private Random random;
    private int currentStage = 1;
    private int totalOpponentTurnCount = 0;
    private boolean isPlayerDefending;
    private static final int MAX_STAGE = 2;
    private JFrame mainFrame;

    public GameView(String playerName, UserController userController, Player player, JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userController = userController;
        this.player = player;
        this.random = new Random();
        this.isPlayerDefending = false;

        this.opponent = new Opponent("상대", 100);

        setLayout(new BorderLayout());

        // UI 초기화
        initializeUI();

        // 초기 정보 업데이트
        updatePlayerInfo();
        updateOpponentInfo();
        updateStage(currentStage);
    }

    private void initializeUI() {
        // 스테이지 레이블
        stageLabel = new JLabel("Stage 1");
        stageLabel.setFont(new Font("Serif", Font.BOLD, 24));
        stageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 로그 패널
        logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logScrollPane = new JScrollPane(logPanel);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 플레이어와 상대 패널
        initializePlayerPanel();
        initializeOpponentPanel();

        // 버튼 패널
        attackButton = new JButton("공격");
        attackButton.addActionListener(e -> handleAttackButton());

        itemButton = new JButton("아이템 사용");
        itemButton.addActionListener(e -> showItemSelection());

        defendButton = new JButton("방어");
        defendButton.addActionListener(e -> playerDefend());

        nextButton = new JButton("다음");
        nextButton.addActionListener(e -> nextStage());
        nextButton.setEnabled(false);

        homeButton = new JButton("홈으로");
        homeButton.addActionListener(e -> returnToHome());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
        buttonPanel.add(attackButton);
        buttonPanel.add(itemButton);
        buttonPanel.add(defendButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(homeButton);

        add(stageLabel, BorderLayout.NORTH);
        add(logScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializePlayerPanel() {
        playerImageLabel = new JLabel(new ImageIcon("resources/playerImage.jpg"));
        playerInfoLabel = new JLabel();
        playerHealthBar = new JProgressBar();

        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.add(playerImageLabel);
        playerPanel.add(playerInfoLabel);
        playerPanel.add(playerHealthBar);

        add(playerPanel, BorderLayout.WEST);
    }

    private void initializeOpponentPanel() {
        opponentImageLabel = new JLabel(new ImageIcon("resources/opponentImage.jpg"));
        opponentInfoLabel = new JLabel();
        opponentHealthBar = new JProgressBar();

        JPanel opponentPanel = new JPanel();
        opponentPanel.setLayout(new BoxLayout(opponentPanel, BoxLayout.Y_AXIS));
        opponentPanel.add(opponentImageLabel);
        opponentPanel.add(opponentInfoLabel);
        opponentPanel.add(opponentHealthBar);

        add(opponentPanel, BorderLayout.EAST);
    }

    private void setupHealthBar(JProgressBar healthBar, int currentHealth, int maxHealth) {
        healthBar.setMaximum(maxHealth);
        healthBar.setValue(currentHealth);
        healthBar.setString(currentHealth + " / " + maxHealth);
        healthBar.setStringPainted(true);
    }

    private void handleAttackButton() {
        int damage = player.getAttackPower() + random.nextInt(50);
        int finalDamage = opponent.takeDamage(damage);
        updateStatus("플레이어가 " + finalDamage + " 데미지를 입혔습니다!");

        updateOpponentInfo();

        if (opponent.getHealth() <= 0) {
            updateStatus("플레이어가 승리했습니다!");
            nextButton.setEnabled(true);
        } else {
            scheduleOpponentTurn();
        }
    }

    private void showItemSelection() {
        List<Item> inventory = player.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            JOptionPane.showMessageDialog(this, "인벤토리에 아이템이 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] itemNames = new String[inventory.size()];
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            itemNames[i] = item.getName() + " (잔여량: " + item.getQuantity() + ")";
        }

        String selectedItem = (String) JOptionPane.showInputDialog(this, "사용할 아이템을 선택하세요: ", "아이템 선택",
                JOptionPane.PLAIN_MESSAGE, null, itemNames, itemNames[0]);

        if (selectedItem != null) {
            for (Item item : inventory) {
                if (selectedItem.startsWith(item.getName())) {
                    useItem(item.getName());
                    item.decreaseQuantity(1);
                    break;
                }
            }
        }
    }

    private void useItem(String selectedItem) {
        if ("체력 회복 물약".equals(selectedItem)) {
            int healAmount = 30;
            int actualHeal = Math.min(healAmount, player.getMaxHealth() - player.getHealth());
            player.heal(actualHeal);
            updateStatus("체력을 " + actualHeal + "만큼 회복했습니다!");
            updatePlayerInfo();
        } else if ("공격력 증가 물약".equals(selectedItem)) {
            player.increaseAttackPower(10);
            updateStatus("공격력이 증가했습니다!");
        } else {
            updateStatus("알 수 없는 아이템입니다.");
        }
    }

    private void playerDefend() {
        isPlayerDefending = true;
        updateStatus("플레이어가 방어 자세를 취했습니다!");
    }

    private void scheduleOpponentTurn() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (isPlayerDefending) {
                        updateStatus("방어 성공! 상대의 공격이 무효화되었습니다.");
                        isPlayerDefending = false;
                    } else {
                        int damage = opponent.getAttackPower() + random.nextInt(10) + 5;
                        player.takeDamage(damage);
                        updateStatus("상대가 " + damage + " 데미지를 입혔습니다!");
                        updatePlayerInfo();

                        if (player.getHealth() <= 0) {
                            handlePlayerDeath();
                        }
                    }
                    totalOpponentTurnCount++;
                });
            }
        }, 2000);
    }

    private void nextStage() {
        if (currentStage < MAX_STAGE) {
            currentStage++;
            opponent.levelUp(currentStage * 10, currentStage * 5);
            updateStatus("Stage " + currentStage + " 시작!");
            updateStage(currentStage);
            updateOpponentInfo();
        } else {
            endGame();
        }
    }

    private void updatePlayerInfo() {
        playerInfoLabel.setText("플레이어: " + player.getName());
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
        attackButton.setEnabled(false);
        itemButton.setEnabled(false);
        defendButton.setEnabled(false);

        JOptionPane.showMessageDialog(this, "플레이어가 패배했습니다. 홈으로 돌아갑니다.");

        userController.updateScore(player.getName(), totalOpponentTurnCount);
        returnToHome();
    }

    private void endGame() {
        updateStatus("게임이 종료되었습니다!");
        userController.updateScore(player.getName(), totalOpponentTurnCount);
        returnToHome();
    }

    private void returnToHome() {
        CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
        cardLayout.show(mainFrame.getContentPane(), "HomeView");
    }
}
