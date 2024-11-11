package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import model.Item;
import model.Player;
import model.Store;

public class StoreView extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private Player player;
	private JLabel playerInfoLabel;
	private JList<Item> itemList;  // 아이템 목록
	private JButton buyButton;
	private JButton backButton;
	private JTextArea itemInfoArea;  // 아이템 소개 창
	private DefaultListModel<Item> itemModel;  // 아이템 목록을 위한 모델  
	private JLabel itemImageLabel;
	private Store store; 
	
	private JFrame previousFrame; // 이전 화면
	
	public StoreView(Player player, Store store, JFrame previousFrame) {
		this.player = player;
		this.store = store;
		this.previousFrame = previousFrame;

		setTitle("상점");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		// 플레이어 정보
		playerInfoLabel = new JLabel("플레이어: "+player.getName()+" | 금액: "+player.getMoney());
	    add(playerInfoLabel, BorderLayout.NORTH);
		
		// 왼쪽 패널에 상품 정보와 구매한 목록을 표시
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(1, 1)); // 위 아래로 분할
        leftPanel.setPreferredSize(new Dimension(600, getHeight()));
        add(leftPanel, BorderLayout.WEST);

        // 상품 정보 영역
        itemInfoArea = new JTextArea(10, 20);
        itemInfoArea.setEditable(false);
        JScrollPane itemInfoScrollPane = new JScrollPane(itemInfoArea);
        leftPanel.add(itemInfoScrollPane);  // 왼쪽 상단에 상품 정보 표시


        // 오른쪽 패널에 아이템 선택 목록과 구매 버튼을 표시
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        add(rightPanel, BorderLayout.CENTER);
		
		// 아이템 목록
        itemModel = new DefaultListModel<>();
        List<Item> items = store.getItem();
        if(items != null && !items.isEmpty()) {
        	for(Item item: items) {
        		itemModel.addElement(item);
        	}
        }
        else {
        	JOptionPane.showMessageDialog(this, "비었습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
		
		itemList = new JList<>(itemModel);
		itemList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		itemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && itemList.getSelectedValue() != null) {
                showItemInfo(itemList.getSelectedValue());
            }
        });
		JScrollPane itemListScrollPane = new JScrollPane(itemList);
		add(itemListScrollPane, BorderLayout.CENTER);
	
		buyButton = new JButton("구매");
		buyButton.addActionListener(e -> handleBuyItem());
		
		backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> handleBackButton());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        buttonPanel.add(buyButton);
        buttonPanel.add(backButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
		setVisible(true);
	}

	private void handleBackButton() {
		this.setVisible(false);
		previousFrame.setVisible(true);
	}

	public void updateItemList(List<Item> items) {
		itemModel.clear();
		if (items != null) {
            for (Item item : items) {
                itemModel.addElement(item);
            }
        }
	}
	
	public void showItemInfo(Item selectedItem) {
		itemInfoArea.setText("이름: " +selectedItem.getName()+"\n");
		itemInfoArea.append("가격: " +selectedItem.getPrice()+"\n");
	}
	
	private void handleBuyItem() {
		Item selectedItem = itemList.getSelectedValue();
		if(selectedItem != null) {			
			if(player.getMoney() >= selectedItem.getPrice()) {
				player.buyItem(selectedItem);
				player.setMoney(player.getMoney() - selectedItem.getPrice());
				updatePlayerInfo();
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
}
