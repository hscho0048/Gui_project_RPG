package controller;

import model.Player;
import model.Opponent;
import view.GameView;
import java.util.Random;

public class GameController {
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
}
