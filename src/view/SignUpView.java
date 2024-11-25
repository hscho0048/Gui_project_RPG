package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import controller.UserController;
import util.PopupLabelUtil;

public class SignUpView extends JPanel {
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton signUpButton, backButton;
	private UserController userController;
	private JFrame mainFrame; // CardLayout 관리용 프레임

	public SignUpView(UserController userController, JFrame mainFrame) {
		this.userController = userController;
		this.mainFrame = mainFrame;

		setPreferredSize(new Dimension(800, 600));
		setLayout(null); // 절대 위치 사용

		// 타이틀 라벨 생성 및 설정
		JLabel titleLabel = new JLabel("회원가입");
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		// 타이틀을 중앙 패널 위쪽에 배치 (x: 250, y: 120)
		titleLabel.setBounds(250, 120, 300, 40);
		add(titleLabel);

		// 중앙 패널 생성 (입력 필드와 버튼을 포함)
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(null); // 절대 위치 사용
		centerPanel.setBounds(0, 0, 800, 600);

		JLabel usernameLabel = new JLabel("아이디:");
		usernameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		usernameField = new JTextField();
		usernameField.setFont(new Font("Dialog", Font.PLAIN, 14));

		JLabel passwordLabel = new JLabel("비밀번호:");
		passwordLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Dialog", Font.PLAIN, 14));

		signUpButton = new JButton("회원가입");
		signUpButton.setFont(new Font("Dialog", Font.BOLD, 14));
		backButton = new JButton("뒤로가기");
		backButton.setFont(new Font("Dialog", Font.BOLD, 14));

		usernameLabel.setBounds(250, 180, 100, 30);
		usernameField.setBounds(250, 210, 300, 40);

		passwordLabel.setBounds(250, 270, 100, 30);
		passwordField.setBounds(250, 300, 300, 40);

		signUpButton.setBounds(250, 370, 140, 40);
		backButton.setBounds(410, 370, 140, 40);

		signUpButton.addActionListener(e -> signUp());
		backButton.addActionListener(e -> {
			CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
			cardLayout.show(mainFrame.getContentPane(), "LoginView"); // LoginView로 전환
		});

		// 컴포넌트 추가
		centerPanel.add(usernameLabel);
		centerPanel.add(usernameField);
		centerPanel.add(passwordLabel);
		centerPanel.add(passwordField);
		centerPanel.add(signUpButton);
		centerPanel.add(backButton);

		add(centerPanel, BorderLayout.CENTER);
	}

	private void signUp() {
		String username = usernameField.getText();
		String password = new String(passwordField.getPassword());

		if (username.isEmpty() || password.isEmpty()) {
			PopupLabelUtil.showPopupLabel(this, "아이디와 비밀번호를 입력하세요.", "failSymbol.png");
			return;
		}

		boolean success = userController.signUp(username, password);
		if (success) {
			PopupLabelUtil.showPopupLabel(this, "회원가입 성공!", "successSymbol.png");
			CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
			Timer timer = new javax.swing.Timer(1000, (ActionEvent e) -> {
				cardLayout.show(mainFrame.getContentPane(), "LoginView"); // 회원가입 성공 시 LoginView로 이동
				((Timer) e.getSource()).stop();
			});
			timer.setRepeats(false);
			timer.start();
		} else {
			PopupLabelUtil.showPopupLabel(this, "이미 존재하는 아이디입니다.", "failSymbol.png");
		}
	}
}
