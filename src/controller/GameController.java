package controller;

import model.Player;
import model.Opponent;
import model.BossMonster;
import java.util.Random;

public class GameController {
    private Player player;
    private Opponent opponent;
    private int currentStage = 1;
    private static final int MAX_STAGE = 2;
    private Random random;

    public GameController(Player player, Opponent opponent) {
        this.player = player;
        this.opponent = opponent;
        this.random = new Random();
    }

    // 플레이어 공격
    public int playerAttack() {
        int damage = player.getAttackPower() + random.nextInt(50);
        return opponent.takeDamage(damage); // 최종 데미지를 반환
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
            opponent.levelUp(currentStage * 10, currentStage * 5);

            if (currentStage == 2) {
                opponent = new BossMonster("드래곤", 300, 10, 50);
            } else {
                opponent.reset();
            }
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
