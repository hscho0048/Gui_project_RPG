package model;

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
}
