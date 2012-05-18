import javax.swing.*;

public class HeistCore extends JFrame {
	
	public static int width = 800;
	public static int height = 600;
	public static HeistCore mainClass;
	
	public HeistCore() {
		setSize(width, height);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		add(new HeistPanel());
		setResizable(false);
		setTitle("THE HEIST!");
		
		setVisible(true);
	}
	
	
	
	public static void main(String[] args) {
		mainClass = new HeistCore();
	}
	
}
