package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import controller.UserController;
import model.MyCharacter;
import model.Player;
import util.*;

public class LoginView extends JPanel {
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginButton, signUpButton;
	private UserController userController;
	private JFrame mainFrame;

	// 로그인 성공 리스너 인터페이스
	public interface OnLoginSuccessListener {
		void onLoginSuccess(String userId); // 로그인 성공 시 호출
	}

	protected OnLoginSuccessListener onLoginSuccessListener; // 리스너 필드

	public LoginView(UserController userController, JFrame mainFrame) {
		this.userController = userController;
		this.mainFrame = mainFrame;

		setPreferredSize(new Dimension(800, 700));
		setLayout(null); // 절대 위치 사용

		// 타이틀 라벨 생성 및 설정
		JLabel titleLabel = new JLabel("RPG 로그인");
		titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		// 타이틀을 중앙 패널 위쪽에 배치 (x: 250, y: 120)
		titleLabel.setBounds(250, 120, 300, 40);
		add(titleLabel);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(null); // 절대 위치 사용
		centerPanel.setBounds(0, 0, 800, 700);

		JLabel usernameLabel = new JLabel("아이디:");
		usernameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		usernameField = new JTextField();
		usernameField.setFont(new Font("Dialog", Font.PLAIN, 14));

		JLabel passwordLabel = new JLabel("비밀번호:");
		passwordLabel.setFont(new Font("Dialog", Font.BOLD, 16));
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Dialog", Font.PLAIN, 14));

		loginButton = new JButton("로그인");
		loginButton.setFont(new Font("Dialog", Font.BOLD, 14));
		signUpButton = new JButton("회원가입");
		signUpButton.setFont(new Font("Dialog", Font.BOLD, 14));

		usernameLabel.setBounds(250, 180, 100, 30);
		usernameField.setBounds(250, 210, 300, 40);

		passwordLabel.setBounds(250, 270, 100, 30);
		passwordField.setBounds(250, 300, 300, 40);

		loginButton.setBounds(250, 550, 140, 40);
		signUpButton.setBounds(410, 550, 140, 40);

		loginButton.addActionListener(e -> login());
		signUpButton.addActionListener(e -> {
			CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
			cardLayout.show(mainFrame.getContentPane(), "SignUpView");
		});

		// 컴포넌트 추가
		centerPanel.add(usernameLabel);
		centerPanel.add(usernameField);
		centerPanel.add(passwordLabel);
		centerPanel.add(passwordField);
		centerPanel.add(loginButton);
		centerPanel.add(signUpButton);

		add(centerPanel, BorderLayout.CENTER);
	}

	public void setOnLoginSuccessListener(OnLoginSuccessListener listener) {
		this.onLoginSuccessListener = listener;
	}

	private void login() {
		String username = usernameField.getText(); // 사용자 ID
		String password = new String(passwordField.getPassword()); // 비밀번호

		if (username.isEmpty() || password.isEmpty()) {
			UIUtils.indicateError(loginButton); // 입력이 비어 있을 경우 버튼 색상 변경
			PopupLabelUtil.showPopupLabel(this, "아이디와 비밀번호를 입력하세요.", "failSymbol.png");
			return;
		}

		if (userController.authenticate(username, password)) {
			// 로그인 성공 시 초기화 작업 수행
			Player player = userController.getPlayerInfo(username); // 플레이어 정보 가져오기
			if (player == null) {
				PopupLabelUtil.showPopupLabel(this, "플레이어 정보를 가져오는 데 실패했습니다.", "failSymbol.png");
				return;
			}
			PopupLabelUtil.showPopupLabel(this, "로그인 성공!", "successSymbol.png");
			// player 객체에 ID를 명시적으로 설정 (로그인 후 받아온 userId를 설정)
			int userId = userController.getUserId(username); // getUserId()는 로그인 후 userId를 가져오는 메서드
			player.setId(userId); // player 객체에 userId 설정

			// MyCharacter 초기화
			MyCharacter globalMyCharacter = new MyCharacter();

			// HomeView 초기화
			HomeView homeView = new HomeView(userController, mainFrame, globalMyCharacter, player);
			mainFrame.getContentPane().add(homeView, "HomeView");

			CardLayout cardLayout = (CardLayout) mainFrame.getContentPane().getLayout();
			Timer timer = new javax.swing.Timer(1000, (ActionEvent e) -> {
				// HomeView로 이동
				cardLayout.show(mainFrame.getContentPane(), "HomeView");
				((Timer) e.getSource()).stop();
			});
			timer.setRepeats(false);
			timer.start();

		} else {
			// 로그인 실패 시 버튼 색상 변경
			UIUtils.indicateError(loginButton);
			PopupLabelUtil.showPopupLabel(this, "아이디 또는 비밀번호가 올바르지 않습니다.", "failSymbol.png");
		}
	}
}