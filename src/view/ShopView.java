package view;

import javax.swing.*;

import controller.UserController;

import java.awt.*;
import java.util.List;
import model.Item;
import model.Player;
import model.Shop;
import util.UIUtils;

public class ShopView extends JPanel {
	private Player player;
	private JLabel playerInfoLabel;
	private JPanel itemPanel;
	private JPanel inventoryPanel; // 인벤토리 패널
	private JButton buyButton;
	private JButton backButton;
	private UserController userController;
	private Shop shop;
	private JFrame mainFrame; // CardLayout 관리용 프레임
	private GameView gameView;
	private HomeView homeView;

	public ShopView(Player player, UserController userController, GameView gameView, JFrame mainFrame,
			HomeView homeView) {
		this.homeView = homeView;
		this.player = player;
		this.userController = userController;
		this.gameView = gameView; // GameView 초기화
		this.mainFrame = mainFrame;
		this.shop = new Shop();
		Font font = new Font("Default", Font.BOLD, 15);

		// 플레이어 정보
		playerInfoLabel = new JLabel("플레이어: " + player.getName() + " | 금액: " + player.getMoney());
		playerInfoLabel.setFont(font);
		add(playerInfoLabel, BorderLayout.NORTH);

		// 인벤토리
		inventoryPanel = new JPanel();
		inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS)); // 개별적으로 BoxLayout 설정
		inventoryPanel.setBorder(BorderFactory.createTitledBorder("인벤토리"));
		updateInventoryPanel();
		add(new JScrollPane(inventoryPanel), BorderLayout.WEST); // 스크롤 추가

		// 아이템 패널
		itemPanel = new JPanel(new GridLayout(0, 3, 10, 10));
		itemPanel.setOpaque(false);
		List<Item> items = shop.getItem();
		if (items != null && !items.isEmpty()) {
			for (Item item : items) {
				JButton itemButton = createItemButton(item);
				itemPanel.add(itemButton);
			}
		} else {
			JOptionPane.showMessageDialog(this, "상점에 아이템이 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}
		add(new JScrollPane(itemPanel), BorderLayout.CENTER);

		// 버튼 패널
		buyButton = new JButton("구매");
		buyButton.addActionListener(e -> handleBuyItem());

		backButton = new JButton("홈으로");
		backButton.addActionListener(e -> handleBack());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(buyButton);
		buttonPanel.add(backButton);

		add(buttonPanel, BorderLayout.SOUTH);
		// 전체 레이아웃 초기화
		setLayout(new BorderLayout());

		// UI 초기화 메서드 호출
		initializeUI();

	}

	private JButton createItemButton(Item item) {
		JButton itemButton = new JButton(
				"<html><center>" + item.getName() + "<br>가격: " + item.getPrice() + "</center></html>", item.getImage());
		itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		itemButton.setHorizontalTextPosition(SwingConstants.CENTER);
		itemButton.setFont(new Font("Default", Font.BOLD, 16));
		itemButton.setPreferredSize(new Dimension(10, 50));
		itemButton.setBackground(Color.WHITE);
		itemButton.setOpaque(true);
		itemButton.setBorder(BorderFactory.createEmptyBorder());
		itemButton.putClientProperty("selected", false);

		itemButton.addActionListener(e -> {
			boolean isSelected = (boolean) itemButton.getClientProperty("selected");

			if (isSelected) {
				itemButton.setBorder(BorderFactory.createEmptyBorder());
				itemButton.setBackground(Color.WHITE);
				itemButton.putClientProperty("selected", false);
				itemPanel.putClientProperty("selectedItem", null);
			} else {
				for (Component component : itemPanel.getComponents()) {
					if (component instanceof JButton) {
						((JButton) component).setBorder(BorderFactory.createEmptyBorder());
						((JButton) component).setBackground(Color.WHITE);
						((JButton) component).putClientProperty("selected", false);
					}
				}
				itemButton.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 3));
				itemButton.setBackground(new Color(240, 240, 240));
				itemButton.putClientProperty("selected", true);
				itemPanel.putClientProperty("selectedItem", item);
			}
		});

		return itemButton;
	}

	private void handleBuyItem() {
		// 선택된 아이템을 가져옵니다.
		Item selectedItem = (Item) itemPanel.getClientProperty("selectedItem");

		if (selectedItem != null) {
			// 플레이어가 아이템을 구매할 수 있는지 확인
			if (player.getMoney() >= selectedItem.getPrice()) {
				// 아이템 구매 처리
				player.buyItem(selectedItem); // 플레이어가 아이템을 구매

				// 아이템 구매 기록을 purchases 테이블에 저장
				userController.recordPurchase(player.getUserId(), selectedItem.getName()); // 구매 기록 저장

				// items 컬럼을 업데이트 (users 테이블)
				if (userController.updateItem(player.getId(), selectedItem.getName())) {
					// 아이템 구매 성공 시, 랭킹 갱신
					homeView.updateRanking(); // 랭킹 테이블 갱신
					gameView.addItemToInventory(selectedItem);
					updateInventoryPanel();
				} else {
					System.out.println("아이템을 데이터베이스에 업데이트하는 데 실패했습니다.");
				}

				// GameView에 아이템 추가
				if (gameView != null) {
					gameView.addItemToInventory(selectedItem); // 필드 gameView를 사용하여 아이템 추가
				} else {
					System.err.println("GameView가 초기화되지 않았습니다.");
				}

				updatePlayerInfo(); // 플레이어 정보 갱신
				JOptionPane.showMessageDialog(this, selectedItem.getName() + "를 구매했습니다!");
			} else {
				JOptionPane.showMessageDialog(this, "돈이 부족합니다.", "오류", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "아이템을 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateInventoryPanel() {
	    inventoryPanel.removeAll(); // 기존 패널 내용 제거

	    // 플레이어의 인벤토리 아이템 가져오기
	    List<Item> inventory = player.getInventory(); // player의 인벤토리 리스트 가져오기
	    if (inventory == null || inventory.isEmpty()) {
	        JLabel emptyLabel = new JLabel("인벤토리가 비었습니다.");
	        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        inventoryPanel.add(emptyLabel);
	    } else {
	        for (Item item : inventory) {
	            // 아이템 이름과 이미지를 사용하여 버튼 생성
	            JButton itemButton = new JButton(item.getName(), item.getImage());
	            itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
	            itemButton.setHorizontalTextPosition(SwingConstants.CENTER);

	            // 버튼 스타일 설정
	            itemButton.setPreferredSize(new Dimension(80, 100)); // 버튼 크기 설정
	            itemButton.setBackground(Color.WHITE);
	            itemButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

	            // 버튼을 인벤토리 패널에 추가
	            inventoryPanel.add(itemButton);
	        }
	    }

	    inventoryPanel.revalidate(); // UI 업데이트
	    inventoryPanel.repaint();
	}



	public void updatePlayerInfo() {
		playerInfoLabel.setText("플레이어: " + player.getName() + " | 금액: " + player.getMoney());
		revalidate();
		repaint();
	}

	private void initializeUI() {
		Font font = new Font("Default", Font.BOLD, 15);

		// 플레이어 정보
		playerInfoLabel = new JLabel("플레이어: " + player.getName() + " | 금액: " + player.getMoney());
		playerInfoLabel.setFont(font);
		add(playerInfoLabel, BorderLayout.NORTH);

		// 인벤토리 패널
		inventoryPanel = new JPanel();
		inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS)); // 개별적으로 BoxLayout 설정
		inventoryPanel.setBorder(BorderFactory.createTitledBorder("인벤토리"));
		updateInventoryPanel();
		add(new JScrollPane(inventoryPanel), BorderLayout.WEST); // 스크롤 추가

		// 아이템 패널
		itemPanel = new JPanel(new GridLayout(0, 2, 10, 10));
		itemPanel.setOpaque(false);
		List<Item> items = shop.getItem();
		if (items != null && !items.isEmpty()) {
			for (Item item : items) {
				JButton itemButton = createItemButton(item);
				itemPanel.add(itemButton);
			}
		} else {
			JOptionPane.showMessageDialog(this, "상점에 아이템이 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}
		add(new JScrollPane(itemPanel), BorderLayout.CENTER);

		// 버튼 패널
		buyButton = new JButton("구매");
		buyButton.addActionListener(e -> handleBuyItem());

		backButton = new JButton("홈으로");
		backButton.addActionListener(e -> handleBack());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(buyButton);
		buttonPanel.add(backButton);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void handleBack() {
		CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
		cardLayout.show(mainFrame.getContentPane(), "HomeView");
	}
}
