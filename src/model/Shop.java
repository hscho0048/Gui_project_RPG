package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class Shop {
	private List<Item> items;
	
	public Shop() {
		items = new ArrayList<>();
		
		// 이미지 경로
		ImageIcon hpImage = new ImageIcon(getClass().getClassLoader().getResource("hpPotion.png"));
        ImageIcon attackImage = new ImageIcon(getClass().getClassLoader().getResource("attackPotion.png"));
        ImageIcon defenceImage = new ImageIcon(getClass().getClassLoader().getResource("defencePotion.png"));
		
		// 임의의 아이템 설정
		items.add(new Item("체력 회복 물약", 40, hpImage));
		items.add(new Item("공격력 증가 물약", 50, attackImage));
		items.add(new Item("방어력 증가 물약", 30, defenceImage));
	}
	
	public List<Item> getItem(){
		return items;
	}
	
	public Item getItemByName(String name) {
		for(Item item : items) {
			if(item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}
}
