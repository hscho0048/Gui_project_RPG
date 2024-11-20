package model;

import java.util.Random;

public class Opponent {
    private String name;
    private int health;
    private int maxHealth; // 최대 체력
    private int attackPower; // 공격력
    private int specialAttackPower; // 특수공격력
    private int defensePower; // 방어력
    private int specialDefensePower; // 특수방어력
    private Random random;
    private int turnCount;

    public Opponent(String name, int health, int attackPower, int specialAttackPower, int defensePower, int specialDefensePower) {
        this.name = name;
        this.health = health;
        this.maxHealth = health; // 초기 체력을 최대 체력으로 설정
        this.attackPower = attackPower;
        this.specialAttackPower = specialAttackPower;
        this.defensePower = defensePower;
        this.specialDefensePower = specialDefensePower;
        this.random = new Random();
        this.turnCount = 0;
    }

    // Getter and Setter
    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getSpecialAttackPower() {
        return specialAttackPower;
    }

    public void setSpecialAttackPower(int specialAttackPower) {
        this.specialAttackPower = specialAttackPower;
    }

    public int getDefensePower() {
        return defensePower;
    }

    public void setDefensePower(int defensePower) {
        this.defensePower = defensePower;
    }

    public int getSpecialDefensePower() {
        return specialDefensePower;
    }

    public void setSpecialDefensePower(int specialDefensePower) {
        this.specialDefensePower = specialDefensePower;
    }

    // Methods
    public void takeDamage(int damage, boolean isSpecialAttack) {
        // 특수 공격인 경우 특수 방어력을 적용
        if (isSpecialAttack) {
            damage -= specialDefensePower; // 특수 방어력 적용
        } else {
            damage -= defensePower; // 일반 방어력 적용
        }

        // 방어력이나 특수 방어력 적용 후 데미지가 0보다 적으면 0으로 처리
        if (damage < 0) {
            damage = 0;
        }

        // 체력 감소
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public int specialAttack() {
        return specialAttackPower + random.nextInt(10); // 특수 공격력 + 랜덤 추가 데미지
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

    public void levelUp(int healthIncrease, int attackIncrease, int specialAttackIncrease, int defenseIncrease, int specialDefenseIncrease) {
        this.maxHealth += healthIncrease;
        this.health = maxHealth; // 최대 체력으로 회복
        this.attackPower += attackIncrease;
        this.specialAttackPower += specialAttackIncrease;
        this.defensePower += defenseIncrease;
        this.specialDefensePower += specialDefenseIncrease;
    }
}
