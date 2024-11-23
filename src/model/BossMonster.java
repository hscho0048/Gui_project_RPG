package model;

public class BossMonster extends Opponent {
	private int specialAttackPower;

	// 생성자: 이름, 체력, 특수 공격력 설정
	public BossMonster(String name, int health, int attackPower, int specialAttackPower, int defensePower,
			int specialDefensePower) {
		super(name, health, attackPower, specialAttackPower, defensePower, specialDefensePower); // 상위 Opponent 생성자 호출
	}

	// 보스 몬스터의 특수 공격 메서드 오버라이드
	@Override
	public int specialAttack() {
		return specialAttackPower + super.getAttackPower(); // 보스의 특수 공격력과 일반 공격력의 합 반환
	}

	@Override
	public void reset() {
		super.reset(); // Opponent 클래스의 reset() 호출

		// 필요한 경우 추가 로직 작성 가능
	}

}
