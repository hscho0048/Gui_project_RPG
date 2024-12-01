package view;

import javax.swing.*;
import java.awt.*;
import model.Character;
import model.MyCharacter;
import model.Player;
import util.PopupLabelUtil;
import util.UIUtils;

import java.util.List;
import java.util.ArrayList;

public class CharacterView extends JPanel {
	private Player player;
	private JButton selectButton;
	private JButton backButton;
	private JLayeredPane layeredPane;
	private JLabel playerInfoLabel;
	private JPanel characterPanel;
	private HomeView homeView;

	public CharacterView(Player player, MyCharacter myCharacter, HomeView homeView) {
		this.player = player;
		this.homeView = homeView;

		setLayout(new BorderLayout());

		// LayeredPane 초기화
		layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(800, 700));

		// 상단 패널 (타이틀 + 플레이어 정보)
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBounds(0, 0, 800, 80);

		// 타이틀
		JLabel titleLabel = new JLabel("캐릭터 선택");
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(titleLabel, BorderLayout.CENTER);

		playerInfoLabel = new JLabel(
				"플레이어: " + player.getName() + " | 직업: " + (player.isJobEmpty() ? "없음" : player.getCharacterName()));
		playerInfoLabel.setFont(new Font("Dialog", Font.BOLD, 15));

		playerInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		topPanel.add(playerInfoLabel, BorderLayout.SOUTH);

		layeredPane.add(topPanel, JLayeredPane.DEFAULT_LAYER);

		// 캐릭터 패널
		characterPanel = new JPanel(new GridLayout(0, 2, 30, 30));
		characterPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50)); // 여백 추가
		characterPanel.setOpaque(false);
		characterPanel.setBounds(0, 100, 800, 480);

		// MyCharacter에서 캐릭터 목록 가져오기
		List<Character> characters = myCharacter.getCharacter();
		if (characters != null && !characters.isEmpty()) {
			for (Character character : characters) {
				JButton characterButton = createCharacterButton(character);
				characterPanel.add(characterButton);
			}
		}

		layeredPane.add(characterPanel, JLayeredPane.DEFAULT_LAYER);

		// 버튼 패널
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		buttonPanel.setBounds(0, 600, 800, 100);

		selectButton = new JButton("선택");
		backButton = new JButton("홈으로");

		Dimension buttonSize = new Dimension(120, 40);
		Font buttonFont = new Font("Dialog", Font.BOLD, 14);

		for (JButton button : new JButton[] { selectButton, backButton }) {
			button.setPreferredSize(buttonSize);
			button.setFont(buttonFont);
			button.setFocusPainted(false);
			button.setBorder(BorderFactory.createRaisedBevelBorder());
		}

		selectButton.addActionListener(e -> handleSelectCharacter());
		backButton.addActionListener(e -> handleBack());

		buttonPanel.add(selectButton);
		buttonPanel.add(backButton);

		layeredPane.add(buttonPanel, JLayeredPane.DEFAULT_LAYER);

		add(layeredPane);
	}

	// 캐릭터 버튼 생성
	private JButton createCharacterButton(Character character) {
		JButton characterButton = new JButton("<html><center>" + character.getName() + "</center></html>",
				character.getImage());
		characterButton.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트를 이미지 아래로 설정
		characterButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 가운데 정렬
		characterButton.setFont(new Font("Default", Font.BOLD, 16));
		characterButton.setPreferredSize(new Dimension(300, 150));
		characterButton.setBackground(Color.WHITE);
		characterButton.setOpaque(true);
		characterButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		// 선택 여부 관리
		characterButton.putClientProperty("selected", false);

		// 버튼 클릭 시 선택/해제 처리
		characterButton.addActionListener(e -> {
			boolean isSelected = (boolean) characterButton.getClientProperty("selected");

			// 선택된 버튼 클릭 시 선택 해제
			if (isSelected) {
				characterButton.setBorder(BorderFactory.createEmptyBorder());
				characterButton.setBackground(Color.WHITE);
				characterButton.putClientProperty("selected", false);
				characterPanel.putClientProperty("selectedCharacter", null);
			} else {
				// 다른 버튼 선택 해제
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
				characterPanel.putClientProperty("selectedCharacter", character); // 선택된 캐릭터 설정
			}
		});

		return characterButton;
	}

	// 캐릭터 선택 후 처리
	private void handleSelectCharacter() {
		Character selectedCharacter = (Character) characterPanel.getClientProperty("selectedCharacter");
		if (selectedCharacter != null) {
			// 플레이어의 능력치와 이미지를 선택한 캐릭터로 설정
			player.setCharacterName(selectedCharacter.getName());
			player.setCharacterStats(selectedCharacter.getAttackPower(), selectedCharacter.getSpecialAttackPower(),
					selectedCharacter.getDefensePower(), selectedCharacter.getSpecialDefensePower(),
					selectedCharacter.getImage());

			updatePlayerInfo();
			PopupLabelUtil.showPopupLabel(this, player.getCharacterName() + "를 선택하였습니다.", "successSymbol.png");
			updateCharacterInDatabase(selectedCharacter.getName()); // 캐릭터 이름을 DB에 업데이트
			homeView.enableBattleButton();
		} else {
			UIUtils.indicateError(selectButton);
			PopupLabelUtil.showPopupLabel(this, "캐릭터를 선택해주세요.", "failSymbol.png");
		}
	}

	// 데이터베이스에 캐릭터 이름 업데이트
	private void updateCharacterInDatabase(String characterName) {
		if (homeView != null) {
			boolean success = homeView.updateCharacter(characterName); // HomeView에서 character_name을 업데이트
			if (!success) {
		        throw new RuntimeException("HomeView에서 캐릭터 업데이트 실패");
			}
		}
	}

	public void updatePlayerInfo() {
		playerInfoLabel.setText(
				"플레이어: " + player.getName() + " | 직업: " + (player.isJobEmpty() ? "없음" : player.getCharacterName()));
		revalidate();
		repaint();
	}

	private void handleBack() {
		this.setVisible(false);
		homeView.setVisible(true);
	}
}
