package util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class PopupLabelUtil {
	public static void showPopupLabel(Container container, String text, String imagePath) {
		// 컨테이너에 LayeredPane이 없다면 생성하여 추가
		JLayeredPane layeredPane;
		if (container instanceof JLayeredPane) {
			layeredPane = (JLayeredPane) container;
		} else {
			// 기존 레이아웃 저장
			LayoutManager originalLayout = container.getLayout();
			Component[] originalComponents = container.getComponents();

			// 새로운 LayeredPane 생성
			layeredPane = new JLayeredPane();
			layeredPane.setBounds(0, 0, 800, 700);

			// 컨테이너 내용을 LayeredPane으로 이동
			container.removeAll();
			container.setLayout(null);

			// 원래 컴포넌트들을 DEFAULT_LAYER에 추가
			JPanel originalPanel = new JPanel(originalLayout);
			originalPanel.setBounds(0, 0, 800, 700);
			for (Component comp : originalComponents) {
				originalPanel.add(comp);
			}
			layeredPane.add(originalPanel, JLayeredPane.DEFAULT_LAYER);
			container.add(layeredPane);
		}

		// 반투명한 어두운 배경 패널 생성
		JPanel darkBackground = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(0, 0, 0, 150)); // 알파값 150으로 반투명 검은색
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		darkBackground.setOpaque(false);
		darkBackground.setBounds(0, 0, 800, 700);

		// 마우스 이벤트 가로채기
		darkBackground.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				e.consume(); // 마우스 클릭 이벤트 소비
			}
		});

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(Color.WHITE);

		// 이미지 라벨
		ImageIcon icon = new ImageIcon(PopupLabelUtil.class.getClassLoader().getResource(imagePath));
		JLabel imageLabel = new JLabel(icon);
		imageLabel.setPreferredSize(new Dimension(400, 140));
		imageLabel.setMinimumSize(new Dimension(400, 140));
		imageLabel.setMaximumSize(new Dimension(400, 140));
		imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 텍스트 라벨
		JLabel titleLabel = new JLabel(text);
		titleLabel.setPreferredSize(new Dimension(400, 55));
		titleLabel.setMinimumSize(new Dimension(400, 55));
		titleLabel.setMaximumSize(new Dimension(400, 55));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));

		// 컴포넌트 추가
		contentPanel.add(imageLabel);
		contentPanel.add(Box.createVerticalStrut(5));
		contentPanel.add(titleLabel);

		// 컨텐츠 패널 위치 설정
		int width = 400;
		int height = 200;
		int x = (800 - width) / 2;
		int y = (700 - height) / 2;
		contentPanel.setBounds(x, y, width, height);

		// 레이어드판에 추가
		layeredPane.add(darkBackground, JLayeredPane.POPUP_LAYER);
		layeredPane.add(contentPanel, Integer.valueOf(JLayeredPane.POPUP_LAYER + 1));

		// 1초 후 제거하는 타이머
		Timer timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				layeredPane.remove(darkBackground);
				layeredPane.remove(contentPanel);
				layeredPane.revalidate(); // 추가
				layeredPane.repaint(); // 추가
				((Timer) e.getSource()).stop();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
}
