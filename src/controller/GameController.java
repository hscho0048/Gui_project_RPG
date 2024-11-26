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

	// 상대 공격 처리
	public int opponentAttack() {
		opponent.incrementTurnCount();
		if (opponent instanceof BossMonster) {
			BossMonster boss = (BossMonster) opponent;
			if (boss.getTurnCount() % 5 == 0) {
				return boss.specialAttack();
			}
		}
		return opponent.getAttackPower() + random.nextInt(40);
	}

	// 다음 스테이지로 전환
	public void nextStage() {
		if (currentStage < MAX_STAGE) {
			currentStage++;
			if (currentStage == 10) {
				// 플레이어와 보스 몬스터 레벨업
				player.levelUp(currentStage * 8, currentStage * 3 + random.nextInt(6),
						currentStage * 3 + random.nextInt(6), 1 + random.nextInt(3), 1 + random.nextInt(3));
				opponent = new BossMonster("리오레이아", 1000, 80, 80, 35, 35); // 보스 몬스터 설정
				System.out.println("보스 몬스터 등장: " + opponent.getName());
			} else {
				// 일반 상대 레벨업
				opponent.levelUp(currentStage * 6, currentStage * 3 + random.nextInt(5),
						currentStage * 3 + random.nextInt(5), 1 + random.nextInt(2), 1 + random.nextInt(2));

				// 플레이어 레벨업
				player.levelUp(currentStage * 8, currentStage * 3 + random.nextInt(6),
						currentStage * 3 + random.nextInt(6), 1 + random.nextInt(3), 1 + random.nextInt(3));
			}
		}
	}

	public void resetGame() {
		player.reset(); // 플레이어 상태 초기화
		opponent.reset(); // 상대 상태 초기화
		currentStage = 1; // 스테이지를 처음으로 초기화
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
