package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

import javax.swing.table.DefaultTableModel;

import model.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserController {
	private Connection connection;

	public UserController() {
		try {
			// SQLite 드라이버 명시적 로드
			Class.forName("org.sqlite.JDBC");

			// JAR 내부 데이터베이스 파일을 외부로 복사
			extractDatabaseFromResources();

			// SQLite 데이터베이스 연결
			String dbPath = Paths.get("RPGGame.db").toAbsolutePath().toString(); // 절대 경로로 설정
			String url = "jdbc:sqlite:" + dbPath; // 외부로 복사된 파일 사용
			connection = DriverManager.getConnection(url);

			// 필요한 테이블 생성
			initializeTables();

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("SQLite 드라이버 로드 실패", e);
		} catch (SQLException e) {
			throw new RuntimeException("데이터베이스 연결 실패", e);
		}
	}

	private void extractDatabaseFromResources() {
		// JAR 내부 리소스에서 RPGGame.db 파일을 읽어 외부로 복사
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("RPGGame.db")) {
			if (input == null) {
				throw new FileNotFoundException("RPGGame.db 파일을 리소스 폴더에서 찾을 수 없습니다.");
			}

			Files.copy(input, Paths.get("RPGGame.db"), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("데이터베이스 파일 복사 실패", e);
		}
	}

	private void initializeTables() {
		try (Statement stmt = connection.createStatement()) {
			// users 테이블 생성
			String createUsersTableSQL = "CREATE TABLE IF NOT EXISTS users (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "username TEXT NOT NULL UNIQUE, " + "password TEXT NOT NULL, " + "character_name TEXT, "
					+ "money INTEGER DEFAULT 100, " + "items TEXT, " + "turns INTEGER DEFAULT 0, "
					+ "score INTEGER DEFAULT 0" + ");";
			stmt.execute(createUsersTableSQL);

			// inventory 테이블 생성
			String createInventoryTableSQL = "CREATE TABLE IF NOT EXISTS inventory ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "user_id INTEGER NOT NULL, "
					+ "item_name TEXT NOT NULL, " + "quantity INTEGER NOT NULL" + ");";
			stmt.execute(createInventoryTableSQL);

			// purchases 테이블 생성
			String createPurchasesTableSQL = "CREATE TABLE IF NOT EXISTS purchases ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "user_id INTEGER NOT NULL, "
					+ "item_name TEXT NOT NULL" + ");";
			stmt.execute(createPurchasesTableSQL);

			// user_stage 테이블 생성
			String createUserStageTableSQL = "CREATE TABLE IF NOT EXISTS user_stage (" + "username TEXT NOT NULL, "
					+ "stage INTEGER DEFAULT 0" + ");";
			stmt.execute(createUserStageTableSQL);

		} catch (SQLException e) {
			throw new RuntimeException("테이블 생성 오류", e);
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
			throw new RuntimeException("해시 알고리즘 오류", e);
		}
	}

	// 회원가입 메서드
	public boolean signUp(String username, String password) {
		String hashedPassword = hashPassword(password); // 비밀번호 해시화
		if (hashedPassword == null)
			return false;

		String query = "INSERT INTO users (username, password) VALUES (?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.setString(2, hashedPassword);
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
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
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}

	// 점수 업데이트 메서드: 게임이 끝날 때 턴 수로 점수를 업데이트
	public void updateTurns(int userId, int turnCount) {
		// Check if the connection exists
		if (connection == null) {
			throw new RuntimeException("Database connection is not established.");
		}

		String query = "UPDATE users SET turns = ? WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			// 쿼리 파라미터 설정
			stmt.setInt(1, turnCount); // turnCount 값을 첫 번째 파라미터로 설정
			stmt.setInt(2, userId); // userId 값을 두 번째 파라미터로 설정

			stmt.executeUpdate();
		} catch (SQLException e) {
			// 예외 처리
			throw new RuntimeException("데이터베이스 오류", e);
		}
	}

	public void updateCurrentStageInDatabase(int userId, int currentStage) {
		// Check if the connection exists
		if (connection == null) {
			throw new RuntimeException("Database connection is not established");
		}

		String query = "UPDATE users SET stage = ? WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, currentStage); // Set the currentStage value
			stmt.setInt(2, userId); // Set the user ID

			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("스테이지 오류", e);
		}
	}

	// 유저의 골드를 업데이트
	public boolean updateGold(int userId, int goldChange) {
		String query = "UPDATE users SET money = money + ? WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, goldChange); // 골드 증가/감소 값 설정
			stmt.setInt(2, userId); // 사용자 ID 설정

			int rowsUpdated = stmt.executeUpdate();
			if (rowsUpdated > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}

	// 특정 유저의 현재 골드 조회
	public int getGold(int userId) {
		String query = "SELECT money FROM users WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, userId); // 사용자 ID 설정

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("money"); // 현재 골드 반환
			} else {
				return -1; // 실패 시 -1 반환
			}
		} catch (SQLException e) {
			return -1;
		}
	}

	// 특정 사용자 삭제 메서드
	public boolean deleteUser(String username) {
		String query = "DELETE FROM users WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}

	public void updateCharacter(int userId, String characterName) {
		String query = "UPDATE users SET character_name = ? WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, characterName);
			stmt.setInt(2, userId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("캐릭터 업데이트 실패", e);
		}
	}

	public ResultSet getRanking() {
		String rankingQuery = "SELECT ROW_NUMBER() OVER (ORDER BY stage DESC, turns ASC) AS rank, "
				+ "username AS player, " + "character_name AS character, " + "COALESCE(items, '없음') AS items, "
				+ "turns, stage " + "FROM users " + "ORDER BY stage DESC, turns ASC";
		try {
			// Statement 생성
			Statement stmt = connection.createStatement();

			// 랭킹 쿼리 실행 후 결과 반환
			return stmt.executeQuery(rankingQuery);
		} catch (SQLException e) {
			// 오류 발생 시 메시지 출력
			throw new RuntimeException("랭킹 데이터 가져오기 실패", e);
		}
	}

	// 랭킹 업데이트 메서드
	public void updateRanking(DefaultTableModel tableModel) {
		// 기존 데이터 초기화
		tableModel.setRowCount(0);

		String rankingQuery = "SELECT (@rank := @rank + 1) AS `rank`, " + "u.username AS player, "
				+ "u.character_name AS 'character', " + "IFNULL(u.items, '없음') AS 'items', " + "u.turns AS turns, "
				+ "u.stage AS stage " + "FROM users u " + "ORDER BY u.stage DESC, u.turns ASC;";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(rankingQuery)) {

			boolean hasData = false;
			while (rs.next()) {
				hasData = true;
				int rank = rs.getInt("rank");
				String player = rs.getString("player");
				String character = rs.getString("character") != null ? rs.getString("character") : "UNKNOWN";
				String items = rs.getString("items") != null ? rs.getString("items") : "없음";
				int turns = rs.getInt("turns");
				int stage = rs.getInt("stage");

				// 데이터 추가
				tableModel.addRow(new Object[] { rank, player, character, items, turns, stage });
			}

			if (!hasData) {
				throw new RuntimeException("플레이한 사용자가 없음");
			}
		} catch (SQLException e) {
			throw new RuntimeException("랭킹 데이터 처리 오류", e);
		}
	}

	public boolean recordPurchase(int userId, String itemName) {
		if (userId <= 0) {
			return false;
		}

		String getItemsQuery = "SELECT items FROM users WHERE id = ?";
		String updateItemsQuery = "UPDATE users SET items = ? WHERE id = ?";

		try (PreparedStatement getItemsStmt = connection.prepareStatement(getItemsQuery);
				PreparedStatement updateItemsStmt = connection.prepareStatement(updateItemsQuery)) {
			getItemsStmt.setInt(1, userId);
			ResultSet rs = getItemsStmt.executeQuery();

			String currentItems = null;
			if (rs.next()) {
				currentItems = rs.getString("items");
			}

			// 아이템 수량 처리
			Map<String, Integer> itemMap = new HashMap<>();
			if (currentItems != null && !currentItems.isEmpty()) {
				String[] items = currentItems.split(",");
				for (String item : items) {
					String[] parts = item.split("\\*");
					String name = parts[0];
					int count = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
					itemMap.put(name, itemMap.getOrDefault(name, 0) + count);
				}
			}

			// 새 아이템 추가 또는 수량 업데이트
			itemMap.put(itemName, itemMap.getOrDefault(itemName, 0) + 1);

			// 업데이트된 아이템 문자열 생성
			StringBuilder updatedItemsBuilder = new StringBuilder();
			for (Map.Entry<String, Integer> entry : itemMap.entrySet()) {
				if (updatedItemsBuilder.length() > 0) {
					updatedItemsBuilder.append(",");
				}
				updatedItemsBuilder.append(entry.getKey());
				if (entry.getValue() > 1) {
					updatedItemsBuilder.append("*").append(entry.getValue());
				}
			}
			String updatedItems = updatedItemsBuilder.toString();

			// 데이터베이스 업데이트
			updateItemsStmt.setString(1, updatedItems);
			updateItemsStmt.setInt(2, userId);
			int rowsUpdated = updateItemsStmt.executeUpdate();

			return rowsUpdated > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean updateCharacterName(int userId, String characterName) {
		String query = "UPDATE users SET character_name = ? WHERE id = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, characterName); // 선택된 캐릭터 이름
			stmt.setInt(2, userId); // 플레이어 ID

			int rowsAffected = stmt.executeUpdate();

			// 디버깅: 쿼리 실행 후 결과
			if (rowsAffected > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			// 디버깅: SQL 예외 처리
			return false;
		}
	}

	public int getUserId(String username) {
		String query = "SELECT id FROM users WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username); // username을 사용하여 ID를 가져옴
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int userId = rs.getInt("id");
				// 디버깅: 쿼리에서 반환된 userId 출력
				return userId; // 반환된 userId를 반환
			} else {
				throw new RuntimeException("해당 username에 대한 userId 찾을 수 없음");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // ID를 찾을 수 없는 경우 -1 반환
	}

	// 로그인 후 player 정보 반환
	public Player getPlayerInfo(String username) {
		String query = "SELECT * FROM users WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				int userId = rs.getInt("id");
				String name = rs.getString("username");
				int money = rs.getInt("money");
				return new Player(userId, name, money); // Player 객체 반환
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // 유저가 없으면 null 반환
	}

	public boolean updateItem(int userId, String itemName) {
		String userCheckQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
		String updateQuery = "UPDATE users SET items = ? WHERE id = ?";

		try (PreparedStatement userCheckStmt = connection.prepareStatement(userCheckQuery);
				PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

			// 1. 사용자 존재 여부 확인
			userCheckStmt.setInt(1, userId);
			ResultSet rs = userCheckStmt.executeQuery();
			if (rs.next()) {
				int userCount = rs.getInt(1); // 사용자 수 가져오기
				if (userCount == 0) {
					return false; // 사용자 없음
				}
			}

			// 2. 업데이트 실행
			updateStmt.setString(1, itemName); // 업데이트할 아이템 설정
			updateStmt.setInt(2, userId); // 업데이트할 사용자 ID 설정
			int rowsAffected = updateStmt.executeUpdate();

			return rowsAffected > 0;
		} catch (SQLException e) {
			return false;
		}
	}

	public String getUserItems(int userId) {
		String query = "SELECT items FROM users WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("items"); // items 데이터 반환
			}
		} catch (SQLException e) {
			throw new RuntimeException("아이템 가져오기 실패", e);
		}
		return null; // 실패 시 null 반환
	}

	public boolean clearUserItems(int userId) {
		if (userId <= 0) {
			return false;
		}

		String updateQuery = "UPDATE users SET items = NULL WHERE id = ?";
		// 또는 빈 문자열로 설정하려면: "UPDATE users SET items = '' WHERE id = ?"

		try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
			stmt.setInt(1, userId);
			int rowsUpdated = stmt.executeUpdate();

			return rowsUpdated > 0;
		} catch (SQLException e) {
			return false;
		}
	}

}