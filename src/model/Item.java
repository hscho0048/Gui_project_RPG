package model;

import javax.swing.ImageIcon;

public class Item {
	private String name;
	private int price;
	private int quantity;
	private ImageIcon image;  // 사진 저장
	
	public Item(String name, int price, ImageIcon image) {
		this.name = name;
		this.price = price;
		this.quantity = 0;
		this.image = image;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPrice() {
		return price;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public ImageIcon getImage() {
		return image;
	}
	
	public void increaseQuantity(int amount) {
		this.quantity += amount;
	}
	
	public void decreaseQuantity(int amount) {
		this.quantity -= amount;
	}
	
	@Override
	public String toString() {
		return name + " - " + price + "원 (구매 개수: " +quantity+")";
	}
}
