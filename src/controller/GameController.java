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

    public GameController(Player player, Opponent opponent, GameView view) {
        this.player = player;
        this.opponent = opponent;
        this.view = view;
        this.random = new Random();
    }

    public void playerAttack() {
        int damage = player.getAttackPower() + random.nextInt(6); // 플레이어의 공격력에 랜덤 추가
        opponent.takeDamage(damage);
        view.updateStatus("플레이어가 " + damage + " 데미지를 입혔습니다!");
        view.updateOpponentInfo();

        if (opponent.getHealth() <= 0) {
            view.updateStatus("플레이어가 승리했습니다!");
            view.disableAttackButton();
        }
    }

    public void playerHeal() {
        if (player.getHealCount() < player.getMaxHealCount()) {
            int healAmount = (int) (player.getHealth() * 0.2); // 현재 체력의 20% 회복
            int actualHeal = Math.min(healAmount, 100 - player.getHealth()); // 실제 회복량 계산
            player.heal(actualHeal);
            view.updateStatus("플레이어가 " + actualHeal + " 만큼 체력을 회복했습니다!");
            view.updatePlayerInfo();

            if (player.getHealCount() >= player.getMaxHealCount()) {
                view.updateStatus("최대 회복 횟수에 도달했습니다. 더 이상 회복할 수 없습니다.");
                view.disableHealButton(); // 회복 버튼 비활성화
            }
        } else {
            view.updateStatus("회복 횟수를 초과하여 더 이상 회복할 수 없습니다.");
        }
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
    }

    public boolean opponentTurn() {
        boolean isAttacking = random.nextBoolean(); // true면 공격, false면 회복

        if (isAttacking) {
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
            int actualHeal = Math.min(healAmount, 100 - opponent.getHealth()); // 실제 회복량 계산
            opponent.heal(actualHeal);
            view.updateStatus("상대가 " + actualHeal + " 만큼 체력을 회복했습니다!");
            view.updateOpponentInfo();
            return false;
        }
    }
}
