package model;

import java.util.Random;

public class Opponent {
	private String name;
	private int health;
	private int maxHealth; // 최대 체력
	private int attackPower;
	private Random random;
	private int turnCount;

	public Opponent(String name, int health) {
		this.name = name;
		this.health = health;
		this.maxHealth = health; // 초기 체력을 최대 체력으로 설정
		this.attackPower = attackPower;
		this.random = new Random();
		this.turnCount = 0;
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
	
	public void takeDamage(int damage) {
		health -= damage;
		if (health < 0)
			health = 0;
	}

	public boolean decideToAttack() {
		return random.nextBoolean(); // true면 공격,
	}

	public int specialAttack() {
		return 10; // 기본 특수 공격력
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
	public void levelUp(int healthIncrease, int attackIncrease) {
		this.maxHealth += healthIncrease;
		this.health = maxHealth; // 최대 체력으로 회복
		this.attackPower += attackIncrease;
	}
}
