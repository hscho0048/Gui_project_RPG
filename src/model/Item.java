package model;

import java.awt.*;
import javax.swing.*;

public class Item {
	private String name;
	private int price;
	
	public Item(String name, int price) {
		this.name = name;
		this.price = price;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPrice() {
		return price;
	}

	public String[] split(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
