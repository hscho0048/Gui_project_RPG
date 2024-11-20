package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import model.Item;
import model.Player;
import model.Shop;

public class ShopView extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private Player player;
	private JButton buyButton;
	private JButton backButton;
	private JLabel playerInfoLabel;
	private DefaultListModel<Item> itemModel;  // 아이템 목록을 위한 모델  
	private JPanel itemPanel;
	private JPanel inventoryPanel;  // 인벤토리 패널
	private Shop shop; 
	
	private HomeView homeView;  // homeView 객체 추가
	
	public ShopView(Player player, Shop shop, HomeView homeView) {
		this.player = player;
		this.shop = shop;
		this.homeView = homeView;
		
		setTitle("상점");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		Font font = new Font("Default", Font.BOLD, 15);
		
		// 플레이어 정보
		playerInfoLabel = new JLabel("플레이어: "+player.getName()+" | 금액: "+player.getMoney());
		playerInfoLabel.setFont(font);
	    add(playerInfoLabel, BorderLayout.NORTH);
	   	    
	    // 인벤토리
	    inventoryPanel = new JPanel();
	    inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS)); // 개별적으로 BoxLayout 설정
	    inventoryPanel.setBorder(BorderFactory.createTitledBorder("인벤토리"));
	    updateInventoryPanel();
	    add(new JScrollPane(inventoryPanel), BorderLayout.WEST); // 스크롤 추가

	    
	    itemPanel = new JPanel(new GridLayout(0, 3, 10, 10));
	    itemPanel.setOpaque(false);
	    List<Item> items = shop.getItem();
	    if(items != null && !items.isEmpty()) {
	    	for(Item item : items) {
	    		JButton itemButton = createItemButton(item);
	    		itemPanel.add(itemButton);
	    	}
	    }
	    else {
    		JOptionPane.showMessageDialog(this, "비었습니다.", "오류", JOptionPane.ERROR_MESSAGE);
    	}
	    
        add(itemPanel, BorderLayout.CENTER);
	    
		
		buyButton = new JButton("구매");
		buyButton.addActionListener(e -> handleBuyItem());
		
		backButton = new JButton("Home");
		backButton.addActionListener(e -> handleBack());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(buyButton);
		buttonPanel.add(backButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	private void updateInventoryPanel() {
	    inventoryPanel.removeAll(); // 기존 내용 제거

	    List<Item> inventory = player.getInventory(); // 플레이어 인벤토리 가져오기
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

	public void updateItemList(List<Item> items) {
		itemModel.clear();
		if (items != null) {
            for (Item item : items) {
                itemModel.addElement(item);
            }
        }
	}
	
	private void handleBuyItem() {
		Item selectedItem = (Item) itemPanel.getClientProperty("selectedItem");
		if(selectedItem != null) {			
			if(player.getMoney() >= selectedItem.getPrice()) {
				player.buyItem(selectedItem);
				player.setMoney(player.getMoney() - selectedItem.getPrice());
				selectedItem.increaseQuantity(1);
				updatePlayerInfo();
				updateInventoryPanel();
				JOptionPane.showMessageDialog(this, selectedItem.getName()+"을(를) 구매하였습니다.");
			}
			else {
				JOptionPane.showMessageDialog(this, "금액이 부족합니다.");
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "아이템을 선택하세요.");
		}
	}

	private void updatePlayerInfo() {
		playerInfoLabel.setText("플레이어: "+player.getName()+" | 금액: "+player.getMoney());
		revalidate();
		repaint();		
	}
	
	private void handleBack() {
		this.setVisible(false);
		//previousFrame.setVisible(true);
		homeView.setVisible(true);
	}
	
	private JButton createItemButton(Item item) {
		JButton itemButton = new JButton("<html><center>" + item.getName() + "<br>가격: " + item.getPrice() + "</center></html>", item.getImage());
		itemButton.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트를 이미지 아래로 설정
	    itemButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 가운데 정렬
	    itemButton.setFont(new Font("Default", Font.BOLD, 16));
		itemButton.setPreferredSize(new Dimension(10, 50));
		
		itemButton.setBackground(Color.WHITE);
		itemButton.setOpaque(true);
		itemButton.setBorder(BorderFactory.createEmptyBorder());
		
		 // 버튼 선택 여부
	    itemButton.putClientProperty("selected", false);


	    itemButton.addActionListener(e -> {
	        boolean isSelected = (boolean) itemButton.getClientProperty("selected");
	        
	        // 현재 선택된 버튼 클릭 시 선택 해제
	        if (isSelected) {
	            itemButton.setBorder(BorderFactory.createEmptyBorder());
	            itemButton.setBackground(Color.WHITE);
	            itemButton.putClientProperty("selected", false);
	            itemPanel.putClientProperty("selectedItem", null);
	        } else {
	            // 다른 버튼의 선택 해제
	            for (Component component : itemPanel.getComponents()) {
	                if (component instanceof JButton) {
	                    ((JButton) component).setBorder(BorderFactory.createEmptyBorder());
	                    ((JButton) component).setBackground(Color.WHITE);
	                    ((JButton) component).putClientProperty("selected", false);
	                }
	            }
	            // 현재 버튼 선택
	            itemButton.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 3));
	            itemButton.setBackground(new Color(240, 240, 240)); // 선택된 배경색
	            itemButton.putClientProperty("selected", true);
	            itemPanel.putClientProperty("selectedItem", item);
	        }
	    });

		return itemButton;
	}

}
