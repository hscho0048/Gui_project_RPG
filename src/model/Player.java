package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Player {
	private int userId;
    private String name;
    private int health;
    private int maxHealth;
    private int attackPower;
    private boolean isDefending;
    private List<Item> inventory; // 인벤토리 추가
    private int money; // 돈 필드 추가
    private Connection connection;
	private int id;

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
        this.isDefending = false;
        this.inventory = new ArrayList<>(); // 인벤토리 초기화
        this.money = money; // 소지금 초기화
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

    public void reset() {
        this.health = this.maxHealth; // 체력을 최대 체력으로 초기화
        this.isDefending = false; // 방어 상태 해제
    }


    // 인벤토리 관리
    public List<Item> getInventory() {
        return inventory;
    }
    
    public int getUserId() {
        return userId;
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
        if (money >= item.getPrice()) {
            money -= item.getPrice(); // 돈 차감
            boolean itemExists = false;

            // 이미 인벤토리에 있는 아이템인지 확인
            for (Item existingItem : inventory) {
                if (existingItem.getName().equals(item.getName())) {
                    existingItem.increaseQuantity(item.getQuantity()); // 수량 증가
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                inventory.add(item); // 인벤토리에 추가
            }
            return true; // 구매 성공
        } else {
            return false; // 돈 부족
        }
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
