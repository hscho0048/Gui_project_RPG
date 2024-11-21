package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import model.Character;
import model.MyCharacter;
import model.Player;

public class CharacterView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Player player;
	private JButton selectButton;
	private JButton backButton;
	private JLabel playerInfoLabel;
	private DefaultListModel<Character> characterModel;  // 캐릭터 목록을 위한 모델
	private JPanel characterPanel;
	private MyCharacter myCharacter;
	
	private HomeView homeView;  // homeView 객체 추가
	
	public CharacterView(Player player, MyCharacter myCharacter, HomeView homeView) {
		this.player = player;
		this.myCharacter = myCharacter;
		this.homeView = homeView;
		
		setLayout(new BorderLayout());
		initializeUI();
		
		Font font = new Font("Default", Font.BOLD, 15);
		
		// 플레이어 정보
		playerInfoLabel = new JLabel("플레이어: "+player.getName()+" | 금액: "+player.getMoney());
		playerInfoLabel.setFont(font);
	    add(playerInfoLabel, BorderLayout.NORTH);
	    
	    characterPanel = new JPanel(new GridLayout(0, 2, 10, 10));
	    characterPanel.setOpaque(false);
	    List<Character> characters = myCharacter.getCharacter();
	    if(characters != null && !characters.isEmpty()) {
	    	for(Character character : characters) {
	    		JButton characterButton = createCharacterButton(character);
	    		characterPanel.add(characterButton);
	    	}
	    }
	    else{
	    	JOptionPane.showMessageDialog(this, "비었습니다.", "오류", JOptionPane.ERROR_MESSAGE);
	    }
	    
	    add(characterPanel, BorderLayout.CENTER);
	    
	    selectButton = new JButton("선택");
	    selectButton.addActionListener(e -> handleSelectCharacter());
	    
	    backButton = new JButton("Home");
		backButton.addActionListener(e -> handleBack());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(selectButton);
		buttonPanel.add(backButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	 private void initializeUI() {
	        JLabel titleLabel = new JLabel("캐릭터 선택");
	        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        add(titleLabel, BorderLayout.NORTH);

	        // 캐릭터 선택 UI 추가
	        JPanel characterPanel = new JPanel();
	        characterPanel.setLayout(new GridLayout(1, 2)); // 예제: 2개의 캐릭터 버튼 추가

	        JButton warriorButton = new JButton("전사");
	        JButton mageButton = new JButton("마법사");

	        warriorButton.addActionListener(e -> selectCharacter("전사"));
	        mageButton.addActionListener(e -> selectCharacter("마법사"));

	        characterPanel.add(warriorButton);
	        characterPanel.add(mageButton);

	        add(characterPanel, BorderLayout.CENTER);

	        JButton backButton = new JButton("뒤로가기");
	        backButton.addActionListener(e -> returnToHome());
	        add(backButton, BorderLayout.SOUTH);
	    }

	public void updateCharacterList(List<Character> characters) {
		characterModel.clear();
		if(characters != null) {
			for(Character character : characters) {
				characterModel.addElement(character);
			}
		}
	}
	private void selectCharacter(String characterName) {
        player.setCharacterName(characterName); // 캐릭터 설정
        JOptionPane.showMessageDialog(this, characterName + " 캐릭터가 선택되었습니다.");
        returnToHome();
    }

    private void returnToHome() {
        CardLayout cardLayout = (CardLayout) homeView.getParent().getLayout();
        cardLayout.show(homeView.getParent(), "HomeView");
    }
	
	private void handleBack() {
		this.setVisible(false);
		homeView.setVisible(true);
	}

	private void handleSelectCharacter() {
	    Character selectedCharacter = (Character) characterPanel.getClientProperty("selectedCharacter");
	    if (selectedCharacter != null) {
	        // 플레이어의 능력치와 이미지를 선택한 캐릭터로 설정
	        player.setCharacterStats(
	            selectedCharacter.getAttackPower(),
	            selectedCharacter.getSpecialPower(),
	            selectedCharacter.getImage()
	        );

	        JOptionPane.showMessageDialog(this, "캐릭터가 선택되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);

	        // HomeView로 돌아가기
	        CardLayout cardLayout = (CardLayout) homeView.getParent().getLayout();
	        cardLayout.show(homeView.getParent(), "HomeView");
	    } else {
	        JOptionPane.showMessageDialog(this, "캐릭터를 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
	    }
	}
	public void setPlayer(Player player) {
	    this.player = player;
	    
	}




	private JButton createCharacterButton(Character character) {
		JButton characterButton = new JButton("<html><center>" + character.getName() + "</center></html>", character.getImage());
		characterButton.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트를 이미지 아래로 설정
	    characterButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 가운데 정렬
	    characterButton.setFont(new Font("Default", Font.BOLD, 16));
		characterButton.setPreferredSize(new Dimension(10, 50));
		
		characterButton.setBackground(Color.WHITE);
		characterButton.setOpaque(true);
		characterButton.setBorder(BorderFactory.createEmptyBorder());
		
		 // 버튼 선택 여부
	    characterButton.putClientProperty("selected", false);


	    characterButton.addActionListener(e -> {
	        boolean isSelected = (boolean) characterButton.getClientProperty("selected");
	        
	        // 현재 선택된 버튼 클릭 시 선택 해제
	        if (isSelected) {
	            characterButton.setBorder(BorderFactory.createEmptyBorder());
	            characterButton.setBackground(Color.WHITE);
	            characterButton.putClientProperty("selected", false);
	            characterPanel.putClientProperty("selectedCharacter", null);
	        } else {
	            // 다른 버튼의 선택 해제
	            for (Component component : characterPanel.getComponents()) {
	                if (component instanceof JButton) {
	                    ((JButton) component).setBorder(BorderFactory.createEmptyBorder());
	                    ((JButton) component).setBackground(Color.WHITE);
	                    ((JButton) component).putClientProperty("selected", false);
	                }
	            }
	            // 현재 버튼 선택
	            characterButton.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 3));
	            characterButton.setBackground(new Color(240, 240, 240)); // 선택된 배경색
	            characterButton.putClientProperty("selected", true);
	            characterPanel.putClientProperty("selectedCharacter", character);
	        }
	    });

		return characterButton;
	}
}
