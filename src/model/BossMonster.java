package model;

public class BossMonster extends Opponent {
	// 생성자: 이름, 체력, 특수 공격력 설정
	public BossMonster(String name, int health, int attackPower, int specialAttackPower, int defensePower,
			int specialDefensePower) {
		super(name, health, attackPower, specialAttackPower, defensePower, specialDefensePower); // 상위 Opponent 생성자 호출
	}

	// 보스 몬스터의 특수 공격 메서드
	public int specialAttack() {
		return super.getAttackPower() + super.getSpecialAttackPower(); // 공격력 + 특수 공격력
	}
}
