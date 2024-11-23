package view;

import javax.swing.*;
import java.awt.*;
import model.Character;
import model.MyCharacter;
import model.Player;
import java.util.List;
import java.util.ArrayList;

public class CharacterView extends JPanel {
	private Player player;
	private JButton selectButton;
	private JButton backButton;
	private JLabel playerInfoLabel;
	private JPanel characterPanel;
	private MyCharacter myCharacter;
	private HomeView homeView;

	public CharacterView(Player player, MyCharacter myCharacter, HomeView homeView) {
		this.player = player;
		this.myCharacter = myCharacter;
		this.homeView = homeView;

		setLayout(new BorderLayout());

		Font font = new Font("Default", Font.BOLD, 15);

		// 플레이어 정보
		playerInfoLabel = new JLabel("플레이어: " + player.getName() + " | 금액: " + player.getMoney());
		playerInfoLabel.setFont(font);
		add(playerInfoLabel, BorderLayout.NORTH);

		// 캐릭터 패널 생성
		characterPanel = new JPanel(new GridLayout(0, 1, 10, 10));
		characterPanel.setOpaque(false);

		// MyCharacter에서 캐릭터 목록 가져오기
		List<Character> characters = myCharacter.getCharacter();
		if (characters != null && !characters.isEmpty()) {
			for (Character character : characters) {
				JButton characterButton = createCharacterButton(character);
				characterPanel.add(characterButton);
			}
		} else {
			JOptionPane.showMessageDialog(this, "캐릭터 목록이 비어있습니다.", "오류", JOptionPane.ERROR_MESSAGE);
		}

		add(characterPanel, BorderLayout.CENTER);

		// 선택  버튼 추가
		selectButton = new JButton("선택");
		selectButton.addActionListener(e -> handleSelectCharacter());

		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(selectButton);

		add(buttonPanel, BorderLayout.SOUTH);

		setVisible(true);
	}

	// 캐릭터 버튼 생성
	private JButton createCharacterButton(Character character) {
		JButton characterButton = new JButton("<html><center>" + character.getName() + "</center></html>",
				character.getImage());
		characterButton.setVerticalTextPosition(SwingConstants.BOTTOM); // 텍스트를 이미지 아래로 설정
		characterButton.setHorizontalTextPosition(SwingConstants.CENTER); // 텍스트 가운데 정렬
		characterButton.setFont(new Font("Default", Font.BOLD, 16));
		characterButton.setPreferredSize(new Dimension(10, 50));

		characterButton.setBackground(Color.WHITE);
		characterButton.setOpaque(true);
		characterButton.setBorder(BorderFactory.createEmptyBorder());

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

			JOptionPane.showMessageDialog(this, selectedCharacter.getName() + " 캐릭터가 선택되었습니다.", "알림",
					JOptionPane.INFORMATION_MESSAGE);

			// HomeView로 돌아가기
			updateCharacterInDatabase(selectedCharacter.getName()); // 캐릭터 이름을 DB에 업데이트
			returnToHome();
		} else {
			JOptionPane.showMessageDialog(this, "캐릭터를 선택해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 데이터베이스에 캐릭터 이름 업데이트
	private void updateCharacterInDatabase(String characterName) {
		if (homeView != null) {
			boolean success = homeView.updateCharacter(characterName); // HomeView에서 character_name을 업데이트
			if (!success) {
				System.out.println("디버깅: HomeView에서 캐릭터 업데이트 실패");
			}
		}
	}

	public void updatePlayerInfo() {
		playerInfoLabel.setText("플레이어: " + player.getName() + " | 금액: " + player.getMoney());
		revalidate();
		repaint();
	}

	// Home으로 돌아가기
	private void returnToHome() {
		CardLayout cardLayout = (CardLayout) homeView.getParent().getLayout();
		cardLayout.show(homeView.getParent(), "HomeView");
	}

	private void handleBack() {
		this.setVisible(false);
		homeView.setVisible(true);
	}
}
