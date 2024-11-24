package controller;

import model.Player;
import model.Opponent;
import model.BossMonster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class GameController {
	private Player player;
	private Opponent opponent;
	private int currentStage = 1;
	private static final int MAX_STAGE = 10;
	private Random random;
	private Connection connection;

	public GameController(Player player, Opponent opponent) {

		this.player = player;
		this.opponent = opponent;
		this.random = new Random();
	}

	public void startGame(String username) {
		String query = "UPDATE users SET turn_count = turn_count + 1 WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, username);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("게임 시작 업데이트 실패: " + e.getMessage());
		}
	}

	// 플레이어 방어 상태 처리
	public void playerDefend() {
		player.setDefending(true);
	}

	// 상대 공격 처리
	public int opponentAttack() {
		if (opponent instanceof BossMonster) {
			BossMonster boss = (BossMonster) opponent;
			if (boss.getTurnCount() % 5 == 0) {
				return boss.specialAttack();
			}
		}
		return opponent.getAttackPower() + random.nextInt(10) + 5;
	}

	// 다음 스테이지로 전환
	public void nextStage() {
		if (currentStage < MAX_STAGE) {
			
			currentStage++;

			if (currentStage == 10) {
				// 플레이어와 보스 몬스터 레벨업
				player.levelUp(currentStage * 4, currentStage + random.nextInt(8), currentStage + random.nextInt(8),
						1 + random.nextInt(4), 1 + random.nextInt(4));
				opponent = new BossMonster("드래곤", 1000, 50, 50, 40, 40); // 보스 몬스터 설정
				System.out.println("보스 몬스터 등장: " + opponent.getName());
			} else {
				// 일반 상대 레벨업
				opponent.levelUp(currentStage * 5, currentStage + random.nextInt(8), currentStage + random.nextInt(8),
						1 + random.nextInt(4), 1 + random.nextInt(4));

				// 플레이어 레벨업
				player.levelUp(currentStage * 4, currentStage + random.nextInt(8), currentStage + random.nextInt(8),
						1 + random.nextInt(4), 1 + random.nextInt(4));
			}
		}
	}

	public void resetGame() {
		player.reset(); // 플레이어 상태 초기화
		opponent.reset(); // 상대 상태 초기화
		currentStage = 1; // 스테이지를 처음으로 초기화
		if (opponent instanceof BossMonster) {
			opponent = new Opponent("상대", 100, 10, 10, 10, 10); // 일반 몬스터로 교체
		}
	}

	// 현재 스테이지 반환
	public int getCurrentStage() {
		return currentStage;
	}

	// 최대 스테이지 확인
	public boolean isLastStage() {
		return currentStage >= MAX_STAGE;
	}

	// 보스 몬스터 확인
	public boolean isBossMonster() {
		return opponent instanceof BossMonster;
	}

	// 상대 정보 반환
	public Opponent getOpponent() {
		return opponent;
	}
	

}