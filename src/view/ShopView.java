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
		
		int updatedGold = userController.getGold(player.getId());
	    if (updatedGold != -1) {
	        player.setMoney(updatedGold); // 데이터베이스 값으로 갱신
	    }

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
	    Item selectedItem = (Item) itemPanel.getClientProperty("selectedItem");

	    if (selectedItem != null) {
	        // 플레이어가 아이템 가격만큼 돈을 가지고 있는지 확인
	        if (player.getMoney() >= selectedItem.getPrice()) {
	            // 아이템 가격을 음수로 설정하여 차감 처리
	            int goldChange = -selectedItem.getPrice();

	            // 데이터베이스 업데이트
	            boolean updateSuccess = userController.updateGold(player.getId(), goldChange);
	            if (updateSuccess) {
	                // 플레이어 객체 업데이트
	                int newMoney = player.getMoney() + goldChange; // goldChange는 음수
	                player.setMoney(newMoney);

	                // 플레이어 인벤토리에 아이템 추가
	                player.buyItem(selectedItem);

	                // UI 업데이트
	                updatePlayerInfo();
	                updateInventoryPanel();

	                JOptionPane.showMessageDialog(this, selectedItem.getName() + "을(를) 구매했습니다!");
	            } else {
	                JOptionPane.showMessageDialog(this, "데이터베이스 업데이트 실패!", "오류", JOptionPane.ERROR_MESSAGE);
	            }
	        } else {
	            JOptionPane.showMessageDialog(this, "돈이 부족합니다.", "오류", JOptionPane.ERROR_MESSAGE);
	        }
	    } else {
	        JOptionPane.showMessageDialog(this, "아이템을 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
	    }
	}



	private void updateInventoryPanel() {
		inventoryPanel.removeAll(); // 기존 패널 내용 제거

		List<Item> inventory = player.getInventory();
		if (inventory == null || inventory.isEmpty()) {
			JLabel emptyLabel = new JLabel("인벤토리가 비었습니다.");
			emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
			inventoryPanel.add(emptyLabel);
		} else {
			for (Item item : inventory) {
				JLabel itemLabel = new JLabel(item.getName() + " x " + item.getQuantity());
				itemLabel.setFont(new Font("Default", Font.PLAIN, 14));
				inventoryPanel.add(itemLabel);
			}
		}

		inventoryPanel.revalidate();
		inventoryPanel.repaint();
	}

	public void updatePlayerInfo() {
	    // 데이터베이스에서 최신 골드 정보 가져오기
	    int updatedGold = userController.getGold(player.getId());
	    if (updatedGold != -1) { // 유효한 값일 경우에만 업데이트
	        player.setMoney(updatedGold);
	    } else {
	        System.out.println("골드 정보를 불러오는 데 실패했습니다.");
	    }

	    // UI에 갱신된 골드 정보를 반영
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
