package model;

import javax.swing.ImageIcon;

public class Character {
	private String name;
	private int attackPower; // 공격력
	private int specialAttackPower; // 특수공격력
	private int defensePower; // 방어력
	private int specialDefensePower; // 특수 방어력
	private ImageIcon image;  // 사진저장
	
	public Character(String name, int attackPower, int specialAttackPower, int defensePower, int specialDefensePower,
			ImageIcon image) {
		this.name = name;
		this.attackPower = attackPower;
		this.specialAttackPower = specialAttackPower;
		this.defensePower = defensePower;
		this.specialDefensePower = specialDefensePower;
		this.image = image;
	}
	
	public String getName() {
		return name;
	}
	
	public ImageIcon getImage() {
		return image;
	}

	public int getAttackPower() {
		return attackPower;
	}

	public int getSpecialAttackPower() {
		return specialAttackPower;
	}

	public int getDefensePower() {
		return defensePower;
	}

	public int getSpecialDefensePower() {
		return specialDefensePower;
	}
}
