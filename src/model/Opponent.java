package model;

import java.util.Random;

public class Opponent {
	private String name;
	private int health;
	private int maxHealth; // 최대 체력
	private int attackPower;
	private int specialAttackPower; // 특수공격력
	private int defense; // 방어력
	private int specialDefense; // 특수방어력
	private Random random;
	private int turnCount;

	public Opponent(String name, int health, int attackPower, int specialAttackPower, int defense, int specialDefense) {
		this.name = name;
		this.health = health;
		this.maxHealth = health; // 초기 체력을 최대 체력으로 설정
		this.attackPower = attackPower;
		this.specialAttackPower = specialAttackPower;
		this.defense = defense;
		this.specialDefense = specialDefense;
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

	public int getSpecialAttackPower() {
		return specialAttackPower;
	}

	public void setSpecialAttackPower(int specialAttackPower) {
		this.specialAttackPower = specialAttackPower;
	}

	public int getDefense() {
		return defense;
	}

	public void setDefense(int defense) {
		this.defense = defense;
	}

	public int getSpecialDefense() {
		return specialDefense;
	}

	public void setSpecialDefense(int specialDefense) {
		this.specialDefense = specialDefense;
	}

	public int takeDamage(int damage, boolean isSpecialAttack) {
		int reducedDamage;
		if (isSpecialAttack) {
			reducedDamage = Math.max(0, damage - specialDefense); // 방어력을 반영한 최종 데미지 계산
		} else {
			reducedDamage = Math.max(0, damage - defense); // 방어력을 반영한 최종 데미지 계산
		}

		health -= reducedDamage; // 체력 감소
		health = Math.max(0, health); // 체력이 0 이하로 내려가지 않도록 제한

		return reducedDamage; // 최종 데미지를 반환
	}

	public int specialAttack() {
		return specialAttackPower + random.nextInt(10);// 특수 공격력 + 랜덤 추가 데미지
	}

	public void reset() {
		this.maxHealth = 100;
		this.health = this.maxHealth; // 체력을 최대 체력으로 초기화
		this.attackPower = 10; // 기본 공격력 초기화
		this.specialAttackPower = 10; // 기본 특수 공격력 초기화
		this.defense = 10;
		this.specialDefense = 10;
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

	public void levelUp(int healthIncrease, int attackIncrease, int specialAttackIncrease, int defenseIncrease,
			int specialDefenseIncrease) {
		this.maxHealth += healthIncrease;
		this.health = maxHealth; // 최대 체력으로 회복
		this.attackPower += attackIncrease;
		this.specialAttackPower += specialAttackIncrease;
		this.defense += defenseIncrease;
		this.specialDefense += specialDefenseIncrease;
	}

	public void setHealth(int health) {
		this.health = Math.max(0, Math.min(health, maxHealth)); // 체력을 0 이상, 최대 체력 이하로 제한
	}
}
