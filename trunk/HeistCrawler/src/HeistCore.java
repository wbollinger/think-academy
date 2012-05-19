import javax.swing.*;

public class HeistCore extends JFrame {
	
	public static int width = HeistPanel.width;
	public static int height = HeistPanel.height+InventoryPanel.height;
	
	public static HeistCore mainClass;
	
	public static HeistPanel mainPane;
	public static InventoryPanel inventoryPane;
	
	public HeistCore() {
		setSize(width, height);
		setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		mainPane = new HeistPanel();
		inventoryPane = new InventoryPanel();
		add(mainPane);
		add(inventoryPane);
		mainPane.setBounds(0, 0, HeistPanel.width, HeistPanel.height);
		inventoryPane.setBounds(0, HeistPanel.height, InventoryPanel.width, InventoryPanel.height);
		setResizable(false);
		setTitle("THE HEIST!");
		
		setVisible(true);
	}
	
	
	
	public static void main(String[] args) {
		mainClass = new HeistCore();
	}
	
}
