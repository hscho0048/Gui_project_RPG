package model;

import javax.swing.ImageIcon;

public class Character {
	private String name;
	private int attackPower;  // 공격력
	private int specialPower;  // 특수공격력
	private ImageIcon image;  // 사진저장
	
	public Character(String name, int attackPower, int specialPower, ImageIcon image) {
		this.name = name;
		this.attackPower = attackPower;
		this.specialPower = specialPower;
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

	public int getSpecialPower() {
		return specialPower;
	}
}
