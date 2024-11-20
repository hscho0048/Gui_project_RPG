package model;

public class BossMonster extends Opponent {

	// 생성자: 이름, 체력, 공격력, 특수공격력, 방어력, 특수방어력 설정
	public BossMonster(String name, int health, int attackPower, int specialAttackPower, int defensePower,
			int specialDefensePower) {
		super(name, health, attackPower, specialAttackPower, defensePower, specialDefensePower); // 상위 Opponent 생성자 호출
	}

	// 보스 몬스터의 특수 공격 메서드 오버라이드
	@Override
	public int specialAttack() {
		return getSpecialAttackPower() + getAttackPower(); // 보스의 특수 공격력과 일반 공격력의 합 반환
	}

	// 보스 몬스터의 피해 감소: 보스가 받는 피해를 20% 줄임

	@Override
	public void takeDamage(int damage, boolean isSpecialAttack) {
		// 20% 피해 감소 (보스 몬스터의 추가 피해 감소)
		int reducedDamage = (int) (damage * 0.8); // 데미지의 80%만 받기
		// 부모 클래스의 takeDamage 호출 (Opponent 클래스에서 정의된 데미지 처리 로직)
		super.takeDamage(reducedDamage, isSpecialAttack);
	}

	@Override
	public void reset() {
		super.reset(); // 상위 Opponent 클래스의 reset() 호출
		// 필요한 경우 보스 몬스터 특화 초기화 로직 추가 가능
	}

}
