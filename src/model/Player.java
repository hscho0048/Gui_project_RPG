package model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class Player {
	private String name;
	private int health;
	private int maxHealth; // 최대 체력
	private int specialPower;  // 특수공격력
	private int attackPower;
	private int baseAttackPower;
	private int healCount; // 회복 횟수 추적
	private final int maxHealCount = 5; // 최대 회복 가능 횟수

	// 추가
	private int money = 100; // 현재 보유 중인 돈
	private List<Item> inventory; // 인벤토리
	private Shop shop;
	private ImageIcon image;
	private String imagePath;  // 플레이어 이미지 경로

	public Player(String name, int health) {
		this.name = name;
		this.health = health;
		this.maxHealth = health;
		this.healCount = 0; // 초기 회복 횟수는 0

		// 추가
		this.money = 100; // 초기 자금 100
		this.inventory = new ArrayList<>(); // 인벤토리 초기화
		this.shop = new Shop();
		this.image = null;  // 이미지 설정
		this.imagePath = null;
	}
	
	public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }
    
    // 캐릭터 능력치와 이미지를 업데이트
    public void setCharacterStats(int attackPower, int specialPower, ImageIcon imageIcon) {
        this.health = this.maxHealth; // 현재 체력도 초기화
        this.attackPower = attackPower;
        this.specialPower = specialPower; // 공격력 초기화
        this.image = imageIcon;  // 이미지 경로 설정
    }
    
	public String getName() {
		return name;
	}

	public int getHealth() {
		return health;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public int getSpecialPower() {
		return specialPower;
	}
	
	public int getMaxHealth() {
		return maxHealth;
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
			if (health > maxHealth)
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

	// 추가
	public void buyItem(Item item) {
	    boolean itemFound = false;

	    // 인벤토리에서 해당 아이템을 찾음
	    for (Item inventoryItem : inventory) {
	        if (inventoryItem.getName().equals(item.getName())) {
	            inventoryItem.increaseQuantity(1); // 수량 증가
	            itemFound = true;
	            break;
	        }
	    }

	    if (!itemFound) {
	        // 새로운 아이템 추가
	        Item newItem = new Item(item.getName(), item.getPrice(), item.getImage());
	        newItem.increaseQuantity(1); // 첫 번째 구매 시 수량을 1로 설정
	        inventory.add(newItem);
	    }
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

	public List<Item> getInventory() {
		return inventory;
	}

	public void increaseDefencePower(int amount) {
		attackPower += amount;
	}
}