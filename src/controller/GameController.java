package controller;

import model.Player;
<<<<<<< HEAD
import model.BossMonster;
=======
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
import model.Opponent;
import view.GameView;
import java.util.Random;

public class GameController {
<<<<<<< HEAD
	private Player player;
	private Opponent opponent;
	private GameView view;
	private Random random;
	private int currentStage;
	private int totalOpponentTurnCount = 0;
	private boolean isPlayerDefending; // 방어 상태를 나타내는 필드
	private static final int MAX_STAGE = 2;

	public GameController(Player player, Opponent opponent, GameView view) {
		this.player = player;
		this.opponent = opponent;
		this.view = view;
		this.random = new Random();
		this.isPlayerDefending = false; // 초기 상태는 방어 아님
		this.currentStage = 1;
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

	public boolean playerAttack() {
	    int damage = player.getAttackPower() + random.nextInt(50); // 플레이어의 공격력에 랜덤 추가
	    if (opponent instanceof BossMonster) {
	        BossMonster boss = (BossMonster) opponent;
	        boss.takeDamage(damage);
	        view.updateBossHealthBar(boss); // 보스 체력 바 갱신
	    } else {
	        opponent.takeDamage(damage); // 상대의 체력 감소
	        view.updateOpponentInfo();
	    }

	    // 공격 후 상태 로그 업데이트
	    view.updateStatus("플레이어가 " + damage + " 데미지를 입혔습니다!");

	    // 상대 체력이 0 이하가 되면 승리 메시지
	    if (opponent.getHealth() <= 0) {
	        view.updateStatus("플레이어가 승리했습니다!");
	        view.disableAttackButton(); // 공격 버튼 비활성화
	        view.enableNextButton(); // 다음 버튼 활성화
	    }

	    return opponent.getHealth() <= 0; // 상대가 패배한 경우
	}

	public void playerHeal() {
	    // 최대 체력의 20% 계산
	    int healAmount = (int) (player.getMaxHealth() * 0.2); 
	    // 실제 회복량은 최대 체력을 초과하지 않도록 제한
	    int actualHeal = Math.min(healAmount, player.getMaxHealth() - player.getHealth());
	    
	    player.heal(actualHeal); // 회복 수행
	    view.updateStatus("플레이어가 " + actualHeal + " 만큼 체력을 회복했습니다!");
	    view.updatePlayerInfo();
	    view.updateHealButtonText(); // 회복 버튼 텍스트 업데이트
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
			return false; // 행동하지 않고 바로 종료
		}

		if (isPlayerDefending) {
			view.updateStatus("플레이어가 방어에 성공했습니다! 상대의 공격이 무효화되었습니다.");
			isPlayerDefending = false; // 방어 상태 해제
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
			view.updateStatus("플레이어가 패배했습니다!");
			view.disableAllButtons();
			endGame();
		}
		totalOpponentTurnCount++; // 총 턴 수 증가
		opponent.incrementTurnCount();
		return true;
	}

	public int getCurrentStage() {
		return currentStage;
	}

	public void endGame() {
		view.updateStatus("게임이 종료되었습니다.");
		view.updateStatus("상대의 총 턴 수: " + totalOpponentTurnCount);
		view.disableAllButtons(); // 모든 버튼 비활성화
	}
=======
    private Player player;
    private Opponent opponent;
    private GameView view;
    private Random random;
<<<<<<< HEAD

=======
    private int currentStage;
    private boolean isPlayerDefending; // 방어 상태를 나타내는 필드
    private static final int MAX_STAGE = 100;

    
>>>>>>> 0ca0e7c (commit message)
    public GameController(Player player, Opponent opponent, GameView view) {
        this.player = player;
        this.opponent = opponent;
        this.view = view;
        this.random = new Random();
<<<<<<< HEAD
    }

    public void playerAttack() {
        int damage = random.nextInt(10) + 5; // 5~15 사이의 공격력
=======
        this.isPlayerDefending = false; // 초기 상태는 방어 아님
        this.currentStage = 1; 
    }
    public void nextStage() {
    	 if (currentStage <= MAX_STAGE) {
    	        currentStage++; // 스테이지 증가
    	        // 상태 업데이트
    	        view.updateStatus("Stage " + currentStage + " 시작!");
    	    } else {
    	        view.updateStatus("게임이 끝났습니다! 모든 스테이지를 완료했습니다.");
    	        view.disableAttackButton(); // 공격 버튼 비활성화
    	    }
    }

   

    public boolean playerAttack() {
        int damage = player.getAttackPower() + random.nextInt(50); // 플레이어의 공격력에 랜덤 추가
>>>>>>> 0ca0e7c (commit message)
        opponent.takeDamage(damage);
        view.updateStatus("플레이어가 " + damage + " 데미지를 입혔습니다!");
        view.updateOpponentInfo();

        if (opponent.getHealth() <= 0) {
            view.updateStatus("플레이어가 승리했습니다!");
            view.disableAttackButton();
<<<<<<< HEAD
        }
    }

    public void playerHeal() {
        int healAmount = (int) (player.getHealth() * 0.2); // 현재 체력의 20% 회복
        player.heal(healAmount);
        view.updateStatus("플레이어가 " + healAmount + " 만큼 체력을 회복했습니다!");
        view.updatePlayerInfo();
=======
            view.enableNextButton(); // 상대 체력이 0이 되면 다음 버튼 활성화
        }
        return opponent.getHealth() <= 0;
    }


    public void playerHeal() {
        int healAmount = (int) (player.getHealth() * 0.2); // 현재 체력의 20% 회복
        int actualHeal = Math.min(healAmount, 100 - player.getHealth()); // 실제 회복량 계산
        player.heal(actualHeal);
        view.updateStatus("플레이어가 " + actualHeal + " 만큼 체력을 회복했습니다!");
        view.updatePlayerInfo();
        view.updateHealButtonText(); // 회복 버튼 텍스트 업데이트
    }

    public void playerDefend() {
        isPlayerDefending = true; // 방어 상태 활성화
        player.increaseDefensePower(10); // 방어력 증가
        view.updateStatus("플레이어가 방어 자세를 취했습니다! 다음 공격이 방어됩니다.");
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
            case "방어력 강화 물약":
                player.increaseDefensePower(5); // 방어력 5 증가
                view.updateStatus("플레이어가 '" + selectedItem + "'을(를) 사용하여 방어력이 증가했습니다!");
                break;
            default:
                view.updateStatus("알 수 없는 아이템입니다.");
                break;
        }
>>>>>>> 0ca0e7c (commit message)
    }

    public boolean opponentTurn() {
        boolean isAttacking = random.nextBoolean(); // true면 공격, false면 회복

        if (isAttacking) {
<<<<<<< HEAD
=======
            if (isPlayerDefending) {
                view.updateStatus("플레이어가 방어에 성공했습니다! 데미지를 받지 않았습니다.");
                isPlayerDefending = false; // 방어 상태 해제
                player.resetDefensePower(); // 방어력 초기화
                return true;
            }

>>>>>>> 0ca0e7c (commit message)
            int damage = random.nextInt(10) + 5;
            player.takeDamage(damage);
            view.updateStatus("상대가 " + damage + " 데미지를 입혔습니다!");
            view.updatePlayerInfo();
            if (player.getHealth() <= 0) {
                view.updateStatus("플레이어가 패배했습니다!");
                view.disableAttackButton();
            }
            return true;
        } else {
            int healAmount = (int) (opponent.getHealth() * 0.2); // 현재 체력의 20% 회복
<<<<<<< HEAD
            opponent.heal(healAmount);
            view.updateStatus("상대가 " + healAmount + " 만큼 체력을 회복했습니다!");
=======
            int actualHeal = Math.min(healAmount, 100 - opponent.getHealth()); // 실제 회복량 계산
            opponent.heal(actualHeal);
            view.updateStatus("상대가 " + actualHeal + " 만큼 체력을 회복했습니다!");
>>>>>>> 0ca0e7c (commit message)
            view.updateOpponentInfo();
            return false;
        }
    }
<<<<<<< HEAD
=======
    public int getCurrentStage() {
        return currentStage;
    }
>>>>>>> 0ca0e7c (commit message)
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
}
