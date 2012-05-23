import java.awt.Image;
import java.awt.Toolkit;


public class Item {
	private String name;
	private int id;
	public Image itemImage;
	public Image invItemImage;
	
	public Item(int id, String name) {
		this.id = id;
		this.name = name;
		itemImage = Toolkit.getDefaultToolkit().createImage("Images/item_"+Integer.toString(id)+".png");
		invItemImage = Toolkit.getDefaultToolkit().createImage("Images/invItem_"+Integer.toString(id)+".png");
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
}
