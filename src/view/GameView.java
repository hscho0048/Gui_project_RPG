package view;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import controller.GameController;
import controller.UserController;
import model.Player;
import model.BossMonster;
import model.Item;
import model.Opponent;

import java.util.Iterator;
import java.util.List;

public class GameView extends JFrame {
    private static final long serialVersionUID = 1L;

    private JLabel playerImageLabel, opponentImageLabel;
    private JLabel playerInfoLabel, opponentInfoLabel;
    private JLabel stageLabel;

    private JProgressBar playerHealthBar, opponentHealthBar;
    private JButton attackButton, itemButton, defendButton, nextButton;
    private JPanel logPanel, stagePanel, opponentPanel;
    private JScrollPane logScrollPane;
    private Player player;
    private Opponent opponent;
    private GameController controller;
    private boolean playerTurn;

    public GameView(String playerName, UserController userController, Player player) {
        // 초기화
        this.player = player;
        opponent = new Opponent("상대", 100);
        controller = new GameController(player, opponent, this, userController);
        playerTurn = true;

        setTitle("RPG 전투");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 스테이지 레이블 초기화
        stageLabel = new JLabel("Stage 1"); // 스테이지 번호를 초기화
        stageLabel.setFont(new Font("Serif", Font.BOLD, 24));
        stageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        stagePanel = new JPanel();
        stagePanel.add(stageLabel);
        stagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // 로그 패널
        logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logScrollPane = new JScrollPane(logPanel); // JScrollPane에 logPanel 추가
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // 플레이어 이미지와 정보
        playerImageLabel = new JLabel(new ImageIcon("resources/playerImage.jpg"));
        playerInfoLabel = new JLabel("플레이어: " + player.getName());
        playerHealthBar = new JProgressBar();
        setupHealthBar(playerHealthBar, player.getHealth(), player.getMaxHealth());

        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        playerPanel.add(playerImageLabel);
        playerPanel.add(playerInfoLabel);
        playerPanel.add(playerHealthBar);

        // 상대방 이미지와 정보
        opponentPanel = new JPanel();
        opponentPanel.setLayout(new BoxLayout(opponentPanel, BoxLayout.Y_AXIS));

        opponentImageLabel = new JLabel(new ImageIcon("resources/opponentImage.jpg"));
        opponentInfoLabel = new JLabel("상대: " + opponent.getName());
        opponentHealthBar = new JProgressBar();
        setupHealthBar(opponentHealthBar, opponent.getHealth(), opponent.getMaxHealth());

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

        defendButton = new JButton("방어");
        defendButton.addActionListener(e -> handleDefendButton());
        defendButton.setEnabled(true);

        nextButton = new JButton("다음");
        nextButton.addActionListener(e -> handleNextButton());
        nextButton.setEnabled(false);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4)); // 4개 버튼
        buttonPanel.add(attackButton);
        buttonPanel.add(itemButton);
        buttonPanel.add(defendButton);
        buttonPanel.add(nextButton);

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(stagePanel); // 스테이지 패널
        mainPanel.add(playerPanel);
        mainPanel.add(Box.createVerticalStrut(10)); // 플레이어와 상대 간 간격 추가
        mainPanel.add(opponentPanel);

        add(mainPanel, BorderLayout.NORTH);
        add(logScrollPane, BorderLayout.CENTER); // 로그 패널을 CENTER에 배치
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setupHealthBar(JProgressBar healthBar, int currentHealth, int maxHealth) {
        healthBar.setMaximum(maxHealth);
        healthBar.setValue(currentHealth);
        healthBar.setString(currentHealth + " / " + maxHealth);
        healthBar.setStringPainted(true);
    }

    public void updatePlayerInfo() {
        playerInfoLabel.setText("플레이어: " + player.getName());
        setupHealthBar(playerHealthBar, player.getHealth(), player.getMaxHealth());
    }

    public void updateOpponentInfo() {
        opponentInfoLabel.setText("상대: " + opponent.getName());
        setupHealthBar(opponentHealthBar, opponent.getHealth(), opponent.getMaxHealth());
    }

    private void handleAttackButton() {
        if (playerTurn) {
            boolean opponentDefeated = controller.playerAttack();
            flashImage(opponentImageLabel); // 상대 이미지 깜빡임
            attackButton.setEnabled(false);
            itemButton.setEnabled(false);
            defendButton.setEnabled(false);
            nextButton.setEnabled(false);
            playerTurn = false; // 턴을 상대에게 넘김
            scheduleOpponentTurn(); // 상대의 턴 자동 실행
            if (opponentDefeated) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        nextButton.setEnabled(true); // 상대가 패배하면 다음 버튼 활성화
                    }
                }, 2000);
            }
        }
    }

    public void showBossMonster(BossMonster bossMonster) {
        opponentInfoLabel.setText("보스: " + bossMonster.getName());
        setupHealthBar(opponentHealthBar, bossMonster.getHealth(), bossMonster.getMaxHealth());
        opponentImageLabel.setIcon(new ImageIcon("resources/bossImage.jpg")); // 보스 이미지로 변경
        opponentPanel.revalidate();
        opponentPanel.repaint();
    }

    public void updateBossHealthBar(BossMonster bossMonster) {
        setupHealthBar(opponentHealthBar, bossMonster.getHealth(), bossMonster.getMaxHealth());
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
            itemNames[i] = item.getName() + "  (잔여량: " + item.getQuantity() + "개)";
        }

        String selectedItem = (String) JOptionPane.showInputDialog(this, "사용할 아이템을 선택하세요: ", "아이템 선택",
                JOptionPane.PLAIN_MESSAGE, null, itemNames, itemNames[0]);

        if (selectedItem != null) {
            Iterator<Item> iterator = inventory.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if (selectedItem.startsWith(item.getName())) {
                    controller.useItem(item.getName());
                    item.decreaseQuantity(1);

                    if (item.getQuantity() <= 0) {
                        iterator.remove();
                    }
                    break;
                }
            }
        }
    }

    private void handleDefendButton() {
        if (playerTurn) {
            controller.playerDefend();
            showDefenseShield(playerImageLabel);
            attackButton.setEnabled(false);
            itemButton.setEnabled(false);
            defendButton.setEnabled(false);
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
                    } else {
                        return;
                    }
                    playerTurn = true;
                    attackButton.setEnabled(true);
                    itemButton.setEnabled(true);
                    defendButton.setEnabled(true);
                });
            }
        }, 2000);
    }

    private void handleNextButton() {
        controller.nextStage();
        playerTurn = true;
        nextButton.setEnabled(false);
        attackButton.setEnabled(true);
        itemButton.setEnabled(true);
        defendButton.setEnabled(true);
    }

    private void flashImage(JLabel imageLabel) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 4) {
                    timer.cancel();
                    return;
                }
                imageLabel.setVisible(!imageLabel.isVisible());
                count++;
            }
        }, 0, 100);
    }

    private void showDefenseShield(JLabel imageLabel) {
        JLabel shieldLabel = new JLabel(new ImageIcon("resources/shield.png"));
        shieldLabel.setBounds(0, 0, imageLabel.getWidth(), imageLabel.getHeight());

        imageLabel.getParent().add(shieldLabel);
        imageLabel.getParent().setComponentZOrder(shieldLabel, 0);
        imageLabel.getParent().repaint();

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
        }, 2000);
    }

    public void updateStatus(String status) {
        addLogMessage(status, true);
    }

    public void updateStage(int stageNumber) {
        stageLabel.setText("Stage " + stageNumber);
    }

    public void disableAttackButton() {
        attackButton.setEnabled(false);
    }

    public void disableDefendButton() {
        defendButton.setEnabled(false);
    }

    public void enableDefendButton() {
        defendButton.setEnabled(true);
    }

    public void enableNextButton() {
        nextButton.setEnabled(true);
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

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    logScrollPane.getViewport().getView().revalidate();
                    JScrollBar vertical = logScrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                });
            }
        }, 450);
    }

    public void returnToHome() {
        this.dispose();
        HomeView homeView = new HomeView(player.getName(), controller.getUserController());
        homeView.setVisible(true);
    }
}
