package model;

import java.util.ArrayList;
import java.util.List;

public class Store {
	private List<Item> items;
	
	public Store() {
		items = new ArrayList<>();
		items.add(new Item("체력 회복 물약", 100));
		items.add(new Item("공격력 증가 물약", 200));
		items.add(new Item("방어력 강화 물약", 150));
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
