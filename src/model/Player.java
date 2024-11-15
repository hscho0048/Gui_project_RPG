package model;

public class Player {
    private String name;
    private int health;
    private int maxHealth; // 최대 체력
    private int baseAttackPower;
    private int attackPower;
    private int healCount; // 회복 횟수 추적
    private final int maxHealCount = 5; // 최대 회복 가능 횟수

    public Player(String name, int health, int baseAttackPower) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.baseAttackPower = baseAttackPower;
        this.attackPower = baseAttackPower;
        this.healCount = 0; // 초기 회복 횟수는 0
    }

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

    public int getHealCount() {
        return healCount;
    }

    public int getMaxHealCount() {
        return maxHealCount;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0)
            health = 0;
    }

    public void heal(int amount) {
        if (healCount < maxHealCount) {
            health += amount;
            if (health > maxHealth) // 체력이 최대 체력을 초과하지 않도록 설정
                health = maxHealth;
            healCount++; // 회복 횟수 증가
        }
    }
    

    public void increaseAttackPower(int amount) {
        attackPower += amount;
    }

    public void resetAttackPower() {
        attackPower = baseAttackPower;
    }

    // 초기 상태로 되돌리는 reset 메소드 추가
    public void reset() {
        this.health = maxHealth; // 최대 체력으로 회복
        this.attackPower = baseAttackPower; // 기본 공격력으로 리셋
        this.healCount = 0; // 회복 횟수 초기화
    }
}
