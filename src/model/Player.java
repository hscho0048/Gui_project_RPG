package model;

public class Player {
    private String name;
    private int health;
    private int maxHealth = 100; // 최대 체력
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
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getAttackPower() { return attackPower; }
    public int getDefensePower() { return defensePower; }
    public int getHealCount() { return healCount; }
    public int getMaxHealCount() { return maxHealCount; }

    public void takeDamage(int damage) {
        int reducedDamage = Math.max(damage - defensePower, 0); // 방어력만큼 피해 감소
        health -= reducedDamage;
        if (health < 0) health = 0;
    }

    public void heal(int amount) {
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
}
