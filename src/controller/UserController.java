package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserController {
	private Connection connection;

	public UserController() {
		try {
			String url = "jdbc:mysql://localhost:3306/RPGGame";
			String user = "root";
			String password = "1111"; // 자신의 데이터베이스 비밀번호로 수정
			connection = DriverManager.getConnection(url, user, password);
			System.out.println("데이터베이스에 연결되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("데이터베이스 연결 실패.");
		}
	}

	// 비밀번호 해시 메서드 (SHA-256)
	private String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(password.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("해시 알고리즘을 사용할 수 없습니다.");
			return null;
		}
	}

	// 회원가입 메서드: 새 사용자 추가 시 score를 0으로 설정
	public boolean signUp(String username, String password) {
		String hashedPassword = hashPassword(password); // 비밀번호 해시화
		if (hashedPassword == null)
			return false;

		String query = "INSERT INTO users (username, password, score) VALUES (?, ?, 0)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, hashedPassword);
			stmt.executeUpdate();
			System.out.println("회원가입이 완료되었습니다. 초기 점수는 0입니다.");
			return true;
		} catch (SQLException e) {
			System.out.println("회원가입 실패: " + e.getMessage());
			return false;
		}
	}

	// 인증 메서드: 해시된 비밀번호와 비교
	public boolean authenticate(String username, String password) {
		String hashedPassword = hashPassword(password); // 비밀번호 해시화
		if (hashedPassword == null)
			return false;

		String query = "SELECT * FROM users WHERE username = ? AND password = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, hashedPassword);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				System.out.println("인증 성공!");
				return true;
			} else {
				System.out.println("인증 실패: 잘못된 ID 또는 비밀번호.");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("인증 중 오류 발생: " + e.getMessage());
			return false;
		}
	}

	// 점수 업데이트 메서드: 게임이 끝날 때 턴 수로 점수를 업데이트
	public boolean updateScore(String username, int turnCount) {
		String query = "UPDATE users SET score = ? WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, turnCount);
			stmt.setString(2, username);
			int rowsAffected = stmt.executeUpdate();
			System.out.println(username + "의 점수가 " + turnCount + "으로 업데이트되었습니다.");
			return rowsAffected > 0;
		} catch (SQLException e) {
			System.out.println("점수 업데이트 실패: " + e.getMessage());
			return false;
		}
	}

	// 특정 사용자의 점수 조회 메서드
	public int getScore(String username) {
		String query = "SELECT score FROM users WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("score");
			} else {
				System.out.println("사용자를 찾을 수 없습니다.");
				return -1;
			}
		} catch (SQLException e) {
			System.out.println("점수 조회 실패: " + e.getMessage());
			return -1;
		}
	}

	// 랭킹 조회 메서드: 턴 수가 적은 순서로 정렬하여 가져옴
	public void displayRanking() {
		String query = "SELECT username, score FROM users ORDER BY score ASC";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			System.out.println("랭킹:");
			while (rs.next()) {
				System.out.println("ID: " + rs.getString("username") + ", 턴 수: " + rs.getInt("score"));
			}
		} catch (SQLException e) {
			System.out.println("랭킹 조회 실패: " + e.getMessage());
		}
	}

	// 특정 사용자 삭제 메서드
	public boolean deleteUser(String username) {
		String query = "DELETE FROM users WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println(username + " 사용자가 삭제되었습니다.");
				return true;
			} else {
				System.out.println("삭제할 사용자를 찾을 수 없습니다.");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("사용자 삭제 실패: " + e.getMessage());
			return false;
		}
	}

	// UserController 클래스 내에 추가
	public ResultSet getRanking() {
		String query = "SELECT username, score FROM users ORDER BY score ASC";
		try {
			PreparedStatement stmt = connection.prepareStatement(query);
			return stmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("랭킹 조회 실패: " + e.getMessage());
			return null;
		}
	}

}