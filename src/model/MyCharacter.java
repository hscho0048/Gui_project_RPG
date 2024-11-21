package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class MyCharacter {
	private List<Character> characters;
	
	public MyCharacter() {
		characters = new ArrayList<>();
		
		// 이미지 경로
		ImageIcon warriorImage = new ImageIcon(getClass().getClassLoader().getResource("warrior.png"));
		ImageIcon wizardImage = new ImageIcon(getClass().getClassLoader().getResource("wizard.png"));
		
		// 캐릭터 생성 - 임의로 공격력, 특수공격력 지정
		characters.add(new Character("전사", 15, 10, warriorImage));
		characters.add(new Character("법사", 10, 15, wizardImage));
	}
	
	public List<Character> getCharacter(){
		return characters;
	}
	
	public Character getCharacterByName(String name) {
		for(Character character : characters) {
			if(character.getName().equals(name)) {
				return character;
			}
		}
		return null;
	}
}
