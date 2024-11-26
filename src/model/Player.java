package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class Player {
	private int userId;
	private String name;
	private int health;
	private int maxHealth;
	private int attackPower;
	private int specialAttackPower; // 특수공격력
	private int defensePower; // 방어력
	private int specialDefensePower; // 특수 방어력

	private ImageIcon characterImage; // 캐릭터 이미지
	private boolean isDefending;
	private List<Item> inventory; // 인벤토리 추가
	private int money; // 돈 필드 추가
	private Connection connection;
	private int id;
	private String characterName;

	public Player(String name, int maxHealth, int attackPower) {
		this.name = name;
		this.health = maxHealth;
		this.maxHealth = maxHealth;
		this.attackPower = attackPower;
		this.isDefending = false;
		this.inventory = new ArrayList<>(); // 빈 인벤토리 초기화
		this.money = 100; // 초기 돈 설정 (예: 100)
	}

	public Player(int id, String name, int money) {
		this.id = id;
		this.name = name;
		this.maxHealth = 100; // 기본 최대 체력
		this.health = this.maxHealth; // 현재 체력
		this.attackPower = 10; // 기본 공격력
		this.specialAttackPower = 0; // 초기 특수 공격력
		this.characterImage = null; // 초기 캐릭터 이미지
		this.isDefending = false;
		this.inventory = new ArrayList<>(); // 인벤토리 초기화
		this.money = 100; // 소지금 초기화
	}

	// 체력 회복
	public void heal(int amount) {
		health = Math.min(health + amount, maxHealth);
	}

	// 데미지 처리
	public void takeDamage(int damage) {
		if (isDefending) {
			damage /= 2; // 방어 상태일 경우 데미지를 절반으로 감소
		}
		health = Math.max(health - damage, 0);
	}

	// 방어 상태 설정
	public void setDefending(boolean defending) {
		this.isDefending = defending;
	}

	// 방어 상태 확인
	public boolean isDefending() {
		return isDefending;
	}

	// 공격력 증가
	public void increaseAttackPower(int amount) {
		attackPower += amount;
	}

	// 방어력 증가
	public void increaseDefencePower(int amount) {
		attackPower += amount;
	}

	// Getter and Setter
	public String getName() {
		return name;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
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

	// 캐릭터 이름 Getter
	public String getCharacterName() {

		return characterName;
	}

	public void setCharacterImage(ImageIcon characterImage) {
		this.characterImage = characterImage;
	}

	// 캐릭터 이름 Setter
	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public void reset() {
		this.maxHealth = 100;
		this.health = this.maxHealth; // 체력을 최대 체력으로 초기화
		this.isDefending = false; // 방어 상태 초기화
		this.attackPower = 10; // 기본 공격력 초기화
		this.specialAttackPower = 10; // 기본 특수 공격력 초기화
		this.defensePower = 10;
		this.specialDefensePower = 10;
		this.characterImage = null; // 캐릭터 이미지 초기화
		this.inventory.clear(); // 인벤토리 초기화
		this.money = 100; // 기본 소지금 초기화
	}

	// 인벤토리 관리
	public List<Item> getInventory() {
		return inventory;
	}

	public int getUserId() {
		return userId;
	}

	// Getter and setter for id
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id; // Player 객체에 ID 설정
	}

	public void addItem(Item item) {
		inventory.add(item);
	}

	public void removeItem(Item item) {
		inventory.remove(item);
	}

	// 돈 관리 메서드
	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public void increaseMoney(int amount) {
		this.money += amount;
	}

	public boolean decreaseMoney(int amount) {
		if (this.money >= amount) {
			this.money -= amount;
			return true;
		}
		return false; // 돈이 부족하면 false 반환
	}

	// 아이템 구매
	public boolean buyItem(Item item) {
		boolean itemExists = false;

		// 이미 인벤토리에 있는 아이템인지 확인
		for (Item existingItem : inventory) {
			if (existingItem.getName().equals(item.getName())) {
				existingItem.increaseQuantity(); // 수량 증가
				itemExists = true;
				break;
			}
		}

		if (!itemExists) {
			inventory.add(item); // 인벤토리에 추가
			item.increaseQuantity();
		}
		return true; // 구매 성공
	}

	// 캐릭터 상태 설정 메서드
	public void setCharacterStats(int attackPower, int specialAttackPower, int defensePower, int specialDefensePower,
			ImageIcon characterImage) {
		this.attackPower = attackPower;
		this.specialAttackPower = specialAttackPower; // 공격력 초기화
		this.defensePower = defensePower;
		this.specialDefensePower = specialDefensePower;
		this.characterImage = characterImage;
	}

	public int takeDamage(int damage, boolean isSpecialAttack) {
		int reducedDamage;
		if (isSpecialAttack) {
			reducedDamage = Math.max(0, damage - specialDefensePower); // 방어력을 반영한 최종 데미지 계산
		} else {
			reducedDamage = Math.max(0, damage - defensePower); // 방어력을 반영한 최종 데미지 계산
		}

		health -= reducedDamage; // 체력 감소
		health = Math.max(0, health); // 체력이 0 이하로 내려가지 않도록 제한

		return reducedDamage; // 최종 데미지를 반환
	}

	public ImageIcon getCharacterImage() {
		return characterImage;
	}

	public Player() {
		this.name = "기본 플레이어";
		this.health = 100;
		this.maxHealth = 100;
		this.attackPower = 1;
		this.specialAttackPower = 0;
		this.characterImage = null;
		this.inventory = new ArrayList<>();
		this.money = 100; // 기본 소지금
	}

	public void setPlayerId(int id) {
		this.id = id;
	}

	public boolean isJobEmpty() {
		return characterName == null || characterName.isEmpty();
	}

	public void levelUp(int healthIncrease, int attackIncrease, int specialAttackIncrease, int defenseIncrease,
			int specialDefenseIncrease) {
		this.maxHealth += healthIncrease;
		this.health = maxHealth; // 최대 체력으로 회복
		this.attackPower += attackIncrease;
		this.specialAttackPower += specialAttackIncrease;
		this.defensePower += defenseIncrease;
		this.specialDefensePower += specialDefenseIncrease;
	}

	public Player getPlayerInfo(String userId) {
		String query = "SELECT id, username, money FROM users WHERE username = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				int id = rs.getInt("id");
				String username = rs.getString("username");
				int money = rs.getInt("money");
				return new Player(id, username, money); // Player 객체 생성
			}
		} catch (SQLException e) {
			System.out.println("플레이어 정보 조회 실패: " + e.getMessage());
		}
		return null; // 실패 시 null 반환
	}

}
