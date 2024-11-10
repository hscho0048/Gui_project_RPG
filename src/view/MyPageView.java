package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class MyPageView extends JFrame {
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private JPanel buttonPanel;

	private JPanel recordPanel;
	private JPanel statsPanel;
	private JPanel settingsPanel;

	public MyPageView() {
		setTitle("RPG");
		setSize(800, 600);

		// 메인 패널 설정
		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);

		// 각 화면 패널 초기화
		initializePanels();

		// 하단 버튼 패널 생성
		createButtonPanel();

		// 프레임에 패널 추가
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
	}

	private void initializePanels() {
		// 기록 패널
		recordPanel = new JPanel();
		recordPanel.setBackground(Color.WHITE);
		recordPanel.add(new JLabel("기록 화면"));
		mainPanel.add(recordPanel, "Record");

		// 스탯 패널
		statsPanel = new JPanel();
		statsPanel.setBackground(Color.WHITE);
		statsPanel.add(new JLabel("스탯 화면"));
		mainPanel.add(statsPanel, "Stats");

		// 설정 패널
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new GridLayout(0, 1));
		settingsPanel.setBackground(Color.WHITE);
		JPanel settingsCenter1 = new JPanel();
		JPanel settingsCenter2 = new JPanel();
		JPanel settingsCenter3 = new JPanel();
		JPanel settingsCenter4 = new JPanel();
		JLabel titleLabel = new JLabel("설정");
		JLabel charsetLabel = new JLabel("캐릭터 이미지 변경");
		JLabel themeLabel = new JLabel("테마 변경");
		String[] themes = { "Nimbus", "Windows", "Metal" };
		JComboBox<String> theme = new JComboBox<>(themes);
		theme.addActionListener(e -> {
			String selectedTheme = (String) theme.getSelectedItem();
			changeTheme(selectedTheme);
		});
		JButton restartButton = new JButton("게임 재시작");
		settingsCenter1.add(titleLabel);
		settingsCenter2.add(charsetLabel);
		settingsCenter3.add(themeLabel);
		settingsCenter3.add(theme);
		settingsCenter4.add(restartButton);

		settingsPanel.add(settingsCenter1);
		settingsPanel.add(settingsCenter2);
		settingsPanel.add(settingsCenter3);
		mainPanel.add(settingsPanel, "Settings");
	}

	private void createButtonPanel() {
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 4, 10, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// 버튼 생성
		JButton recordButton = new JButton("기록");
		JButton statsButton = new JButton("스탯");
		JButton settingsButton = new JButton("설정");
		JButton backToGameButton = new JButton("게임으로");

		// 버튼 이벤트 추가
		recordButton.addActionListener(e -> cardLayout.show(mainPanel, "Record"));
		statsButton.addActionListener(e -> cardLayout.show(mainPanel, "Stats"));
		settingsButton.addActionListener(e -> cardLayout.show(mainPanel, "Settings"));

		// 버튼 패널에 버튼 추가
		buttonPanel.add(recordButton);
		buttonPanel.add(statsButton);
		buttonPanel.add(settingsButton);
		buttonPanel.add(backToGameButton);
	}

	public void changeTheme(String theme) {
		try {
			switch (theme) {
			case "Nimbus":
				UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
				break;
			case "Windows":
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				break;
			case "Metal":
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				break;
			default:
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}

			// 프레임의 모든 컴포넌트에 테마 적용
			SwingUtilities.updateComponentTreeUI(this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MyPageView frame = new MyPageView();
			frame.setVisible(true);
		});
	}

}
