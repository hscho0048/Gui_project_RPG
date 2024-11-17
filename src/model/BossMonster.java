package model;

public class BossMonster extends Opponent {
	private int specialAttackPower;

	// 생성자: 이름, 체력, 특수 공격력 설정
	public BossMonster(String name, int health, int attackPower, int specialAttackPower) {
		super(name, health); // 상위 Opponent 생성자 호출
		this.specialAttackPower = specialAttackPower;
		setAttackPower(attackPower); // 상위 클래스의 setAttackPower 메서드 사용
	}

	// 보스 몬스터의 특수 공격 메서드 오버라이드
	@Override
	public int specialAttack() {
		return specialAttackPower + super.getAttackPower(); // 보스의 특수 공격력과 일반 공격력의 합 반환
	}

	// 보스 몬스터의 피해 감소: 보스가 받는 피해를 20% 줄임
	@Override
	public int takeDamage(int damage) {
	    int reducedDamage = Math.max(0, damage - this.getDefense()); // 방어력을 반영한 데미지 계산
	    this.setHealth(this.getHealth() - reducedDamage); // 체력 감소
	    if (this.getHealth() < 0) this.setHealth(0); // 체력이 0 이하로 내려가지 않도록 설정

	    // 디버깅 출력
	    System.out.println("보스가 받은 최종 데미지: " + reducedDamage + " (방어력: " + this.getDefense() + ")");
	    return reducedDamage; // 최종 데미지를 반환
	}

	@Override
	public void reset() {
		super.reset(); // Opponent 클래스의 reset() 호출

		// 필요한 경우 추가 로직 작성 가능
	}

	// 보스 몬스터의 특수 공격력 반환
	public int getSpecialAttackPower() {
		return specialAttackPower;
	}

	public void setSpecialAttackPower(int specialAttackPower) {
		this.specialAttackPower = specialAttackPower;
	}

	@Override
	public void levelUp(int healthIncrease, int attackIncrease) {
		super.levelUp(healthIncrease, attackIncrease); // 상위 클래스의 levelUp 호출
		this.specialAttackPower += attackIncrease / 2; // 특수 공격력은 공격력 증가량의 절반만큼 증가
		System.out.println(getName() + " 보스 몬스터의 능력치가 상승했습니다! (특수 공격력 증가)");
	}

}
