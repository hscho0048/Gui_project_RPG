package model;

import java.util.Random;

public class Opponent {
    private String name;
    private int health;
    private int maxHealth; // 최대 체력
    private Random random;

    public Opponent(String name, int health) {
        this.name = name;
        this.health = health;
        this.maxHealth = health; // 초기 체력을 최대 체력으로 설정
        this.random = new Random();
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) health = maxHealth; // 체력이 최대 체력을 넘지 않도록 제한
    }

    public boolean decideToAttack() {
        return random.nextBoolean(); // true면 공격, false면 방어 또는 회복
    }
}