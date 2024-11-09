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
        int damage = random.nextInt(10) + 5; // 5~15 사이의 공격력
        opponent.takeDamage(damage);
        view.updateStatus("플레이어가 " + damage + " 데미지를 입혔습니다!");
        view.updateOpponentInfo();

        if (opponent.getHealth() <= 0) {
            view.updateStatus("플레이어가 승리했습니다!");
            view.disableAttackButton();
        }
    }

    public void playerHeal() {
        int healAmount = (int) (player.getHealth() * 0.2); // 현재 체력의 20% 회복
        player.heal(healAmount);
        view.updateStatus("플레이어가 " + healAmount + " 만큼 체력을 회복했습니다!");
        view.updatePlayerInfo();
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
            opponent.heal(healAmount);
            view.updateStatus("상대가 " + healAmount + " 만큼 체력을 회복했습니다!");
            view.updateOpponentInfo();
            return false;
        }
    }
}
