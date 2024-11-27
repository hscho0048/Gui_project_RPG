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

	public int getDefense() {
		return defense;
	}

	public int getMaxHealth() {
		return maxHealth; // maxHealth 반환 메소드 추가
	}

	public int getAttackPower() {
		return attackPower;
	}

	public int getSpecialAttackPower() {
		return specialAttackPower;
	}

	public void setOpponentName(int num) {
		if (num == 1)
			this.name = "도스기르오스";
		else
			this.name = "참조룡";
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

	public void reset() {
		this.maxHealth = 100;
		this.health = this.maxHealth; // 체력을 최대 체력으로 초기화
		this.attackPower = 25; // 기본 공격력 초기화
		this.specialAttackPower = 25; // 기본 특수 공격력 초기화
		this.defense = 8;
		this.specialDefense = 8;
	}

	public void incrementTurnCount() {
		this.turnCount++; // 턴 수 증가
	}

	public int getTurnCount() {
		return turnCount; // 상대의 턴 수 반환
	}

	public void resetTurnCount() {
		turnCount = 0;
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
}
