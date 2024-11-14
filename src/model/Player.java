package model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int health;
    private int maxHealth = 100; // 최대 체력
    private int baseAttackPower;
    private int attackPower;
    private int defensePower;
    
    // 추가
    private int money;  // 현재 보유 중인 돈
    private List<Item> inventory;  // 인벤토리
    private Shop shop;

    public Player(String name, int health, int baseAttackPower) {
        this.name = name;
        this.health = health;
        this.maxHealth = health;
        this.baseAttackPower = baseAttackPower;
        this.attackPower = baseAttackPower;
        this.defensePower = 0; // 기본 방어력은 0
        
        // 추가
        this.money = 100;  // 초기 자금 100
        this.inventory = new ArrayList<>();  // 인벤토리 초기화
        this.shop = new Shop();
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getAttackPower() { return attackPower; }
    public int getDefensePower() { return defensePower; }

    public void takeDamage(int damage) {
        int reducedDamage = Math.max(damage - defensePower, 0); // 방어력만큼 피해 감소
        health -= reducedDamage;
        if (health < 0) health = 0;
    }

    public void heal(int amount) {
            health += amount;
            if (health > maxHealth) health = maxHealth;
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
    
    // 추가 
    public void buyItem(Item item) {
    	inventory.add(item);
    }
    
    public void setMoney(int money) {
    	this.money = money;
    }
    
    public int getMoney() {	
    	return money;
    }
    
    public Shop getShop() {
    	return shop;
    }
    
    public List<Item> getInventory(){
    	return inventory;
    }
}
