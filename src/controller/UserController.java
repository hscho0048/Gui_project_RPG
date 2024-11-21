package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import model.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserController {
	private Connection connection;

	public UserController() {
		try {
			String url = "jdbc:mysql://localhost:3306/RPGGame";
			String user = "root";
			String password = "kkero0418"; // 자신의 데이터베이스 비밀번호로 수정
			connection = DriverManager.getConnection(url, user, password);
			System.out.println("데이터베이스에 연결되었습니다.");
		} catch (SQLException e) {
			System.out.println("데이터베이스 연결 실패: " + e.getMessage());
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
	public boolean updateTurns(String username, int turnCount) {
	    // 업데이트 쿼리 작성
	    String query = "UPDATE users SET turns = ? WHERE username = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        // 쿼리 파라미터 설정
	        stmt.setInt(1, turnCount);  // turnCount 값을 첫 번째 파라미터로 설정
	        stmt.setString(2, username);  // username 값을 두 번째 파라미터로 설정

	        // 쿼리 실행
	        int rowsAffected = stmt.executeUpdate();

	        // 영향 받은 행 수 출력
	        if (rowsAffected > 0) {
	            System.out.println(username + "의 턴수가 " + turnCount + "으로 업데이트되었습니다.");
	            return true;
	        } else {
	            System.out.println("사용자 " + username + "이(가) 존재하지 않거나 턴수 업데이트가 이루어지지 않았습니다.");
	            return false;
	        }
	    } catch (SQLException e) {
	        // 예외 처리
	        System.out.println("턴수 업데이트 실패: " + e.getMessage());
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
	public void updateCharacter(int userId, String characterName) {
	    String query = "UPDATE users SET character_name = ? WHERE id = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, characterName);
	        stmt.setInt(2, userId);
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        System.out.println("캐릭터 업데이트 실패: " + e.getMessage());
	    }
	}

	public ResultSet getRanking() {
	    String initializeRankQuery = "SET @rank = 0;";  // rank 변수 초기화
	    String rankingQuery = "SELECT (@rank := @rank + 1) AS `rank`, " +
	                          "u.username AS player, " +
	                          "`character_name` AS 'character', " + 
	                          "IFNULL(u.items, '없음') AS 'items', " + 
	                          "u.turns AS turns, " +  // 턴수 추가
	                          "us.stage AS stage " +  // 완료한 스테이지 추가
	                          "FROM users u " +
	                          "LEFT JOIN user_stage us ON u.username = us.username " +
	                          "ORDER BY u.turns DESC, us.stage DESC;";  // 턴수와 스테이지에 따라 정렬

	    try {
	        Statement stmt = connection.createStatement();
	        stmt.execute(initializeRankQuery); // @rank 초기화
	        return stmt.executeQuery(rankingQuery);  // 랭킹 데이터를 가져오는 쿼리 실행
	    } catch (SQLException e) {
	        System.err.println("랭킹 데이터를 가져오는 데 실패했습니다: " + e.getMessage());
	        return null;
	    }
	}
	// 랭킹 업데이트 메서드
	public void updateRanking(DefaultTableModel tableModel) {
	    // 기존 데이터 초기화
	    tableModel.setRowCount(0);

	    String rankingQuery = "SELECT (@rank := @rank + 1) AS `rank`, " +
	                          "u.username AS player, " +
	                          "`character_name` AS 'character', " + 
	                          "IFNULL(u.items, '없음') AS 'items', " +  // 아이템 정보 추가
	                          "u.turns AS turns, " + 
	                          "us.stage AS stage " + 
	                          "FROM users u " +
	                          "LEFT JOIN user_stage us ON u.username = us.username " +
	                          "ORDER BY u.turns DESC, us.stage DESC;";

	    try (Statement stmt = connection.createStatement();
	         ResultSet rs = stmt.executeQuery(rankingQuery)) {

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
	            tableModel.addRow(new Object[]{rank, player, character, items, turns, stage});
	        }

	        if (!hasData) {
	            System.out.println("아직 플레이한 사용자가 없습니다.");
	        }
	    } catch (SQLException e) {
	        System.err.println("랭킹 데이터를 처리하는 중 오류 발생: " + e.getMessage());
	    }
	}
	
	public void recordPurchase(int userId, String itemName) {
	    String query = "INSERT INTO purchases (user_id, item_name) VALUES (?, ?)";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, userId);        // 사용자 ID
	        stmt.setString(2, itemName);  // 아이템 이름

	        int rowsInserted = stmt.executeUpdate();
	        
	        if (rowsInserted > 0) {
	            System.out.println("아이템 구매 기록이 저장되었습니다: " + itemName);
	        } else {
	            System.out.println("아이템 구매 기록 저장 실패.");
	        }
	    } catch (SQLException e) {
	        System.out.println("아이템 구매 기록 저장 실패: " + e.getMessage());
	    }
	}

	public Player getPlayerInfo(String userId) {
	    String query = "SELECT id, username, money FROM users WHERE username = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, userId);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            int id = rs.getInt("id");
	            String name = rs.getString("username");
	            int money = rs.getInt("money"); // money 컬럼 읽기
	            return new Player(id, name, money);
	        }
	    } catch (SQLException e) {
	        System.out.println("플레이어 정보 조회 실패: " + e.getMessage());
	    }
	    return null;
	}
	public boolean updateItem(String username, String itemName) {
	    String query = "UPDATE users SET items = ? WHERE username = ?";
	    try (PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setString(1, itemName);  // 아이템 이름 설정
	        stmt.setString(2, username);  // 사용자 이름 설정

	        int rowsAffected = stmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            System.out.println(username + "의 아이템이 " + itemName + "으로 업데이트되었습니다.");
	            return true;
	        } else {
	            System.out.println("사용자 " + username + "이(가) 존재하지 않거나 아이템 업데이트가 이루어지지 않았습니다.");
	            return false;
	        }
	    } catch (SQLException e) {
	        System.out.println("아이템 업데이트 실패: " + e.getMessage());
	        return false;
	    }
	}

}