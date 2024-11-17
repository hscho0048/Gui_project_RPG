package model;

import java.util.Random;

public class Opponent {
    private String name;
    private int health;
    private int maxHealth; // 최대 체력
    private int attackPower;
    private int defense; // 방어력
    private Random random;
    private int turnCount;

    public Opponent(String name, int health) {
        this.name = name;
        this.health = health;
        this.maxHealth = health; // 초기 체력을 최대 체력으로 설정
        this.random = new Random();
        this.turnCount = 0;
        this.attackPower = 10; // 기본 공격력 설정
        this.defense = 5; // 기본 방어력 설정
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth; // maxHealth 반환 메소드 추가
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int takeDamage(int damage) {
        int reducedDamage = Math.max(0, damage - defense); // 방어력을 반영한 최종 데미지 계산
        health -= reducedDamage; // 체력 감소
        health = Math.max(0, health); // 체력이 0 이하로 내려가지 않도록 제한

        // 디버깅용 출력
        System.out.println(name + "이(가) " + reducedDamage + " 데미지를 입었습니다! (방어력: " + defense + ")");
        return reducedDamage; // 최종 데미지를 반환
    }


    public int specialAttack() {
        return 10 + random.nextInt(5); // 기본 특수 공격력
    }

    public void reset() {
        this.health = maxHealth; // 최대 체력으로 회복
    }

    public void incrementTurnCount() {
        this.turnCount++; // 턴 수 증가
    }

    public int getTurnCount() {
        return turnCount; // 상대의 턴 수 반환
    }

    public boolean decideToAttack() {
        return random.nextBoolean(); // true면 공격
    }

    public void levelUp(int healthIncrease, int attackIncrease) {
        this.maxHealth += healthIncrease; // 최대 체력을 증가
        this.health = maxHealth; // 현재 체력도 최대 체력으로 회복
        this.attackPower += attackIncrease; // 공격력 증가
    }
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, maxHealth)); // 체력을 0 이상, 최대 체력 이하로 제한
    }
}
