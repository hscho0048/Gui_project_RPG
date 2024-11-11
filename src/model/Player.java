package model;

public class Player {
    private String name;
    private int health;
<<<<<<< HEAD
    private int level;

    public Player(String name, int health, int level) {
        this.name = name;
        this.health = health;
        this.level = level;
=======
    private int maxHealth; // 최대 체력
    private int baseAttackPower;
    private int attackPower;
    private int defensePower;
    private int healCount; // 회복 횟수 추적
    private final int maxHealCount = 5; // 최대 회복 가능 횟수

    public Player(String name, int health, int baseAttackPower) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.baseAttackPower = baseAttackPower;
        this.attackPower = baseAttackPower;
        this.defensePower = 0; // 기본 방어력은 0
        this.healCount = 0; // 초기 회복 횟수는 0
>>>>>>> 0ca0e7c (commit message)
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
<<<<<<< HEAD
    public int getLevel() { return level; }

    public void takeDamage(int damage) {
        health -= damage;
=======
    public int getAttackPower() { return attackPower; }
    public int getDefensePower() { return defensePower; }
    public int getHealCount() { return healCount; }
    public int getMaxHealCount() { return maxHealCount; }

    public void takeDamage(int damage) {
        int reducedDamage = Math.max(damage - defensePower, 0); // 방어력만큼 피해 감소
        health -= reducedDamage;
>>>>>>> 0ca0e7c (commit message)
        if (health < 0) health = 0;
    }

    public void heal(int amount) {
<<<<<<< HEAD
        health += amount;
=======
        if (healCount < maxHealCount) {
            health += amount;
            if (health > maxHealth) health = maxHealth;
            healCount++; // 회복 횟수 증가
        }
    }

    public void increaseAttackPower(int amount) {
        attackPower += amount;
    }

    public void resetAttackPower() {
        attackPower = baseAttackPower;
    }

    public void increaseDefensePower(int amount) {
        defensePower += amount;
    }

    public void resetDefensePower() {
        defensePower = 0;
    }

    // 초기 상태로 되돌리는 reset 메소드 추가
    public void reset() {
        this.health = maxHealth; // 최대 체력으로 회복
        this.attackPower = baseAttackPower; // 기본 공격력으로 리셋
        this.defensePower = 0; // 기본 방어력으로 리셋
        this.healCount = 0; // 회복 횟수 초기화
>>>>>>> 0ca0e7c (commit message)
    }
}
