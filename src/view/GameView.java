package view;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import controller.GameController;
import model.Player;
import model.Item;
import model.Opponent;

import java.util.Iterator;
import java.util.List;

public class GameView extends JFrame {
	private static final long serialVersionUID = 1L;

    private JLabel playerImageLabel, opponentImageLabel;
    private JLabel playerInfoLabel, opponentInfoLabel;
    private JProgressBar playerHealthBar, opponentHealthBar;
    private JButton attackButton, healButton, itemButton, defendButton, nextButton;
    private JPanel logPanel;
    private Player player;
    private Opponent opponent;
    private GameController controller;
    private boolean playerTurn;

    public GameView(String playerName, Player player) {
        // 초기화
        //player = new Player(playerName, 100, 1);
    	
    	this.player = player;
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

        itemButton = new JButton("아이템 사용");
        itemButton.addActionListener(e -> showItemSelection());
        itemButton.setEnabled(true);

        defendButton = new JButton("방어");
        defendButton.addActionListener(e -> handleDefendButton());
        defendButton.setEnabled(true);

        nextButton = new JButton("다음");
        nextButton.setEnabled(false);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5)); // 5개 버튼이므로 GridLayout(1, 5)
        buttonPanel.add(attackButton);
        //buttonPanel.add(healButton);
        buttonPanel.add(itemButton);
        buttonPanel.add(defendButton);
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

    private void showItemSelection() {
    	// Player의 인벤토리에서 아이템 목록 가져오기
        List<Item> inventory = player.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            JOptionPane.showMessageDialog(
            	this,
            	"인벤토리에 아이템이 없습니다.",
            	"알림",
            	JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        // 아이템 이름 목록 생성
    	String[] itemNames = new String[inventory.size()];
    	for(int i = 0; i < inventory.size(); i++) {
    		Item item = inventory.get(i);
    		itemNames[i] = item.getName() + "  (잔여량: "+ item.getQuantity()+"개)";
    	}
    	
    	// 아이템 목록 선택 다이얼로그
    	String selectedItem = (String) JOptionPane.showInputDialog(
    			this,
    			"사용할 아이템을 선택하세요: ",
    			"아이템 선택",
    			JOptionPane.PLAIN_MESSAGE,
    			null,
    			itemNames,
    			itemNames[0]
    	);
    	
    	// 선택된 아이템에 따라 로직 처리
    	if(selectedItem != null) {
    		Iterator<Item> iterator = inventory.iterator();
    		while(iterator.hasNext()) {
    			Item item = iterator.next();
    			if(selectedItem.startsWith(item.getName())) {
    				controller.useItem(item.getName());
    				item.decreaseQuantity(1);
    				
    				if(item.getQuantity() <= 0) {
    					iterator.remove();
    				}
    				break;
    			}
    		}
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
                    itemButton.setEnabled(true);
                    defendButton.setEnabled(true);
                });
            }
        }, 2000); // 2초 후 상대의 턴 실행
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

    public void disableAttackButton() {
        attackButton.setEnabled(false); // 공격 버튼 비활성화
    }

    public void disableHealButton() {
        healButton.setEnabled(false); // 회복 버튼 비활성화
    }

    public void disableDefendButton() {
        defendButton.setEnabled(false); // 방어 버튼 비활성화
    }

    public void enableDefendButton() {
        defendButton.setEnabled(true); // 방어 버튼 활성화
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
