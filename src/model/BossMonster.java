package model;

<<<<<<< HEAD
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class BossMonster extends Opponent {
	private int specialAttackPower;
	private JProgressBar healthBar;

	// 생성자: 이름, 체력, 특수 공격력 설정
	public BossMonster(String name, int health, int attackPower, int specialAttackPower) {
		super(name, health); // 상위 Opponent 생성자 호출
		this.specialAttackPower = specialAttackPower;
		setAttackPower(attackPower); // 상위 클래스의 setAttackPower 메서드 사용
	}

	// 보스 몬스터의 특수 공격 메서드 오버라이드
	@Override
	public int specialAttack() {
		System.out.println(getName() + "이(가) 강력한 특수 공격을 사용합니다!");
		return specialAttackPower + super.getAttackPower(); // 보스의 특수 공격력과 일반 공격력의 합 반환
	}

	// 보스 몬스터의 피해 감소: 보스가 받는 피해를 20% 줄임
	@Override
	public void takeDamage(int damage) {
		int reducedDamage = (int) (damage * 0.8); // 20% 피해 감소
		super.takeDamage(reducedDamage);
		System.out.println(getName() + "이(가) " + reducedDamage + "의 피해를 입었습니다! (보스의 피해 감소 적용)");
	}

	@Override
	public void reset() {
		super.reset(); // Opponent 클래스의 reset() 호출
		System.out.println(getName() + " 보스 몬스터의 체력을 초기화합니다: " + getHealth() + " / " + getMaxHealth());
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

=======
public class BossMonster extends Opponent {
    private int specialAttackPower;

    // 생성자: 이름, 체력, 특수 공격력 설정
    public BossMonster(String name, int health, int specialAttackPower) {
        super(name, health);
        this.specialAttackPower = specialAttackPower;
    }

    // 보스 몬스터의 특수 공격 메서드 오버라이드
    @Override
    public int specialAttack() {
        System.out.println(getName() + "이(가) 강력한 특수 공격을 사용합니다!");
        return specialAttackPower + super.specialAttack(); // 기본 특수 공격력에 보스의 특수 공격력 추가
    }

    // 보스 몬스터의 피해 감소: 보스가 받는 피해를 20% 줄임
    @Override
    public void takeDamage(int damage) {
        int reducedDamage = (int) (damage * 0.8); // 20% 피해 감소
        super.takeDamage(reducedDamage);
        System.out.println(getName() + "이(가) " + reducedDamage + "의 피해를 입었습니다! (보스의 피해 감소 적용)");
    }

    // 보스 몬스터의 특수 공격력 반환
    public int getSpecialAttackPower() {
        return specialAttackPower;
    }
>>>>>>> 35f4c543b586ee3eacd249c2c8f43679c629fab6
}
