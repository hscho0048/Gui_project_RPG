package model;

public class Player {
    private String name;
    private int health;
    private int level;

    public Player(String name, int health, int level) {
        this.name = name;
        this.health = health;
        this.level = level;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getLevel() { return level; }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public void heal(int amount) {
        health += amount;
    }
}
