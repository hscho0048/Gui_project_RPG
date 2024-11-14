package controller;

import model.Player;
import model.Opponent;
import view.GameView;
import java.util.Random;

public class GameController {
    private Player player;
    private Opponent opponent;
    private GameView view;
    private UserController userController;
    private Random random;

    public GameController(Player player, Opponent opponent, GameView view, UserController userController) {
        this.player = player;
        this.opponent = opponent;
        this.view = view;
        this.userController = userController;
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
        int damage = random.nextInt(10) + 5;
        player.takeDamage(damage);
        view.updateStatus("상대가 " + damage + " 데미지를 입혔습니다!");
        view.updatePlayerInfo();

        if (player.getHealth() <= 0) {
            view.updateStatus("플레이어가 패배했습니다!");
            view.disableAttackButton();
            view.returnToHome(); // 홈 화면으로 돌아가기
        }
        return true;
    }
    public UserController getUserController() {
        return userController; // UserController 반환
    }
}
