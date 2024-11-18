package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

public class Shop {
	private List<Item> items;
	
	public Shop() {
		items = new ArrayList<>();
		
		// 이미지 경로
		ImageIcon hpImage = new ImageIcon(getClass().getClassLoader().getResource("hpPotion.jpg"));
        ImageIcon attackImage = new ImageIcon(getClass().getClassLoader().getResource("attackPotion.jpg"));
		
		// 임의의 아이템 설정
		items.add(new Item("체력 회복 물약", 50, hpImage));
		items.add(new Item("공격력 증가 물약", 80, attackImage));
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
