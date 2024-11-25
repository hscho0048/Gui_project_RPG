package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import controller.UserController;

import java.awt.*;
import java.util.List;
import model.Item;
import model.Player;
import model.Shop;
import util.PopupLabelUtil;
import util.UIUtils;

public class ShopView extends JPanel {
	private Player player;
	private JLabel playerInfoLabel;
	private JLayeredPane layeredPane;
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

		int updatedGold = userController.getGold(player.getId());
		if (updatedGold != -1) {
			player.setMoney(updatedGold); // 데이터베이스 값으로 갱신
		}

		// 전체 레이아웃 초기화
		setLayout(new BorderLayout());

		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(800, 600));

		// UI 초기화 메서드 호출
		initializeUI();

		add(layeredPane);

	}

	private void initializeUI() {
		Font font = new Font("Default", Font.BOLD, 15);

		// 플레이어 정보
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBounds(0, 0, 800, 50);

		// 상점 타이틀
		JLabel titleLabel = new JLabel("상점");
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(titleLabel, BorderLayout.CENTER);

		playerInfoLabel = new JLabel("플레이어: " + player.getName() + " | 금액: " + player.getMoney());
		playerInfoLabel.setFont(font);
		playerInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // 왼쪽 여백
		topPanel.add(playerInfoLabel, BorderLayout.WEST);
		layeredPane.add(topPanel, JLayeredPane.DEFAULT_LAYER);

		// 인벤토리 패널
		inventoryPanel = new JPanel();
		inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS)); // 개별적으로 BoxLayout 설정
		inventoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JScrollPane inventoryScrollPane = new JScrollPane(inventoryPanel);
		inventoryScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
				"인벤토리", TitledBorder.CENTER, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 16)));
		inventoryScrollPane.setBounds(20, 70, 200, 430);
		updateInventoryPanel();
		layeredPane.add(inventoryScrollPane, JLayeredPane.DEFAULT_LAYER);

		// 아이템 패널
		itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		List<Item> items = shop.getItem();
		if (items != null && !items.isEmpty()) {
			for (Item item : items) {
				JButton itemButton = createItemButton(item);
				itemPanel.add(itemButton);
			}
		}
		JScrollPane itemScrollPane = new JScrollPane(itemPanel);
		itemScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "상품 목록",
				TitledBorder.CENTER, TitledBorder.TOP, new Font("Dialog", Font.BOLD, 16)));
		itemScrollPane.setBounds(240, 70, 540, 430);
		layeredPane.add(itemScrollPane, JLayeredPane.DEFAULT_LAYER);

		// 버튼 패널
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		buttonPanel.setBounds(0, 520, 800, 60);

		Dimension buttonSize = new Dimension(100, 40);
		buyButton = new JButton("구매");
		buyButton.setPreferredSize(buttonSize);
		buyButton.setFont(new Font("Dialog", Font.BOLD, 14));
		buyButton.addActionListener(e -> handleBuyItem());

		backButton = new JButton("홈으로");
		backButton.setPreferredSize(buttonSize);
		backButton.setFont(new Font("Dialog", Font.BOLD, 14));
		backButton.addActionListener(e -> handleBack());
		buttonPanel.add(buyButton);
		buttonPanel.add(backButton);
		buttonPanel.setBounds(0, 500, 800, 50);
		layeredPane.add(buttonPanel, JLayeredPane.DEFAULT_LAYER);
	}

	private JButton createItemButton(Item item) {
		JButton itemButton = new JButton("<html><center>" + item.getName() + "<br><font color='#B8860B'>"
				+ item.getPrice() + " 골드</font></center></html>", item.getImage());
		itemButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		itemButton.setHorizontalTextPosition(SwingConstants.CENTER);
		itemButton.setFont(new Font("Dialog", Font.BOLD, 14));
		itemButton.setPreferredSize(new Dimension(150, 150));
		itemButton.setBackground(Color.WHITE);
		itemButton.setFocusPainted(false);
		itemButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
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
			if (player.getMoney() >= selectedItem.getPrice()) {

				int goldChange = -selectedItem.getPrice();

				// 데이터베이스 업데이트
				boolean updateSuccess = userController.updateGold(player.getId(), goldChange);
				if (updateSuccess) {
					// 플레이어 인벤토리에 아이템 추가
					player.buyItem(selectedItem);

					// UI 업데이트
					updatePlayerInfo();
					updateInventoryPanel();
					userController.recordPurchase(player.getId(), selectedItem.getName()); // 구매 기록 저장
					PopupLabelUtil.showPopupLabel(layeredPane, selectedItem.getName() + "을(를) 구매했습니다!",
							"successSymbol.png");
				} else {
					PopupLabelUtil.showPopupLabel(this, "데이터베이스 업데이트 실패", "failSymbol.png");
				}

			} else {
				PopupLabelUtil.showPopupLabel(layeredPane, "골드가 부족합니다.", "failSymbol.png");
			}
		} else {
			PopupLabelUtil.showPopupLabel(layeredPane, "아이템을 선택하세요.", "failSymbol.png");
		}
	}

	public void updateInventoryPanel() {
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

	private void handleBack() {
		CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
		cardLayout.show(mainFrame.getContentPane(), "HomeView");
	}
}
