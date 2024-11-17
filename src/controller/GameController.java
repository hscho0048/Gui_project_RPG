package controller;

import model.Player;
import model.BossMonster;
import model.Opponent;
import view.GameView;
import java.util.Random;

import javax.swing.JOptionPane;

public class GameController {
	private Player player;
	private Opponent opponent;
	private GameView view;
	private Random random;
	private int currentStage = 1;
	private int totalOpponentTurnCount = 0;
	private boolean isPlayerDefending; // 방어 상태를 나타내는 필드
	private static final int MAX_STAGE = 2;
	private UserController userController;

	public GameController(Player player, Opponent opponent, GameView view, UserController userController) {
		this.player = player;
		this.opponent = opponent;
		this.view = view;
		this.random = new Random();
		this.isPlayerDefending = false; // 초기 상태는 방어 아님
		this.userController = userController;
	}

	public boolean playerAttack() {
	    int damage = player.getAttackPower() + random.nextInt(50); // 플레이어의 공격력에 랜덤 추가
	    int finalDamage; // 방어력을 반영한 최종 데미지

	    if (opponent instanceof BossMonster) {
	        BossMonster boss = (BossMonster) opponent;
	        finalDamage = boss.takeDamage(damage); // 방어력을 반영한 데미지 계산
	        view.updateBossHealthBar(boss); // 보스 체력 UI 갱신
	    } else {
	        finalDamage = opponent.takeDamage(damage); // 방어력을 반영한 데미지 계산
	        view.updateOpponentInfo(); // 일반 상대 체력 UI 갱신
	    }

	    // 상태 로그 출력 (최종 데미지를 포함)
	    view.updateStatus("플레이어가 " + finalDamage + " 데미지를 입혔습니다!");

	    if (opponent.getHealth() <= 0) {
	        view.updateStatus("플레이어가 승리했습니다!");
	    }

	    return opponent.getHealth() <= 0; // 상대방이 패배했는지 여부 반환
	}


	public void playerDefend() {
		isPlayerDefending = true; // 방어 상태 활성화
		view.updateStatus("플레이어가 방어 자세를 취했습니다! 다음 공격을 방어합니다.");
	}

	public void useItem(String selectedItem) {
		switch (selectedItem) {
		case "체력 회복 물약":
			int healAmount = 30; // 예시로 30만큼 회복
			int actualHeal = Math.min(healAmount, 100 - player.getHealth());
			player.heal(actualHeal);
			view.updateStatus("플레이어가 '" + selectedItem + "'을(를) 사용하여 " + actualHeal + " 만큼 체력을 회복했습니다!");
			view.updatePlayerInfo();
			break;
		case "공격력 증가 물약":
			player.increaseAttackPower(10); // 공격력 10 증가
			view.updateStatus("플레이어가 '" + selectedItem + "'을(를) 사용하여 공격력이 증가했습니다!");
			break;
		default:
			view.updateStatus("알 수 없는 아이템입니다.");
			break;
		}
	}

	public boolean opponentTurn() {
		int damage;
		if (opponent.getHealth() <= 0) { // 상대방 체력이 0 이하인지 확인
			totalOpponentTurnCount++; // 총 턴 수 증가
			return false; // 행동하지 않고 바로 종료
		}

		if (isPlayerDefending) {
			view.updateStatus("플레이어가 방어에 성공했습니다! 상대의 공격이 무효화되었습니다.");
			isPlayerDefending = false; // 방어 상태 해제
			totalOpponentTurnCount++; // 총 턴 수 증가
			return true; // 공격이 무효화되었음을 알림
		}

		if (opponent instanceof BossMonster) {
			BossMonster boss = (BossMonster) opponent;

			// 보스몬스터가 5턴마다 특수공격 실행
			if (boss.getTurnCount() > 0 && boss.getTurnCount() % 5 == 0) {
				damage = boss.specialAttack(); // 보스몬스터의 특수공격
				view.updateStatus("보스몬스터가 특수공격을 사용했습니다! 데미지: " + damage);
			} else {
				damage = boss.getAttackPower() + random.nextInt(10) + 5; // 기본 공격력 + 랜덤 추가 데미지
			}
		} else {
			damage = opponent.getAttackPower() + random.nextInt(10) + 5; // 기본 공격력 + 랜덤 추가 데미지
		}

		player.takeDamage(damage);
		view.updateStatus("상대가 " + damage + " 데미지를 입혔습니다!");
		view.updatePlayerInfo();

		if (player.getHealth() <= 0) {
			handlePlayerDeath(); // 플레이어가 패배하면 처리
		}
		totalOpponentTurnCount++; // 총 턴 수 증가
		opponent.incrementTurnCount();
		return true;
	}

	public void nextStage() {
		if (currentStage < MAX_STAGE) {
			currentStage++;
			opponent.levelUp(currentStage * 10, currentStage * 5);

			// 보스 몬스터 등장 (예: 2단계에서 보스 등장)
			if (currentStage == 2) {
				opponent = new BossMonster("드래곤", 300, 10, 50); // 보스 몬스터 생성
				if (opponent instanceof BossMonster) {
					view.showBossMonster((BossMonster) opponent); // 보스 체력바 업데이트
				}
				view.updateStatus("보스 몬스터가 등장했습니다: " + opponent.getName());
			} else {
				opponent.reset(); // 일반 상대의 체력 초기화
				view.updateOpponentInfo(); // 일반 상대 체력바 업데이트
			}

			player.reset(); // 플레이어 체력 초기화
			view.updatePlayerInfo(); // 플레이어 정보 업데이트

			view.updateStage(currentStage); // 스테이지 번호 업데이트
			view.updateStatus("Stage " + currentStage + " 시작!");
		} else {
			view.updateStatus("모든 스테이지를 완료했습니다!");
			view.disableAttackButton();
			endGame();
		}
	}

	public int getCurrentStage() {
		return currentStage;
	}

	// 플레이어가 패배했을 때 처리 메서드
	private void handlePlayerDeath() {
		totalOpponentTurnCount++; // 총 턴 수 증가
		view.updateStatus("플레이어가 패배했습니다!");
		view.disableAttackButton();

		JOptionPane.showMessageDialog(view, "플레이어가 패배했습니다. 홈으로 돌아갑니다.");
		
		// 데이터베이스에 턴 수 기록
		userController.updateScore(player.getName(), totalOpponentTurnCount);

		// 홈 화면으로 돌아가기
		view.returnToHome();
	}

	public void endGame() {
		view.disableAllButtons(); // 모든 버튼 비활성화
		view.updateStatus("게임이 종료되었습니다.");
		view.updateStatus("총 턴 수: " + totalOpponentTurnCount);

		JOptionPane.showMessageDialog(view, "홈으로 돌아갑니다.");
		// 데이터베이스에 턴 수 기록
		userController.updateScore(player.getName(), totalOpponentTurnCount);

		view.returnToHome();

	}

	public UserController getUserController() {
		return userController; // UserController 반환
	}
}
