import java.awt.*;
import javax.swing.*;

public class HeistCore extends JFrame {

	
	public static HeistCore mainClass;
	static HeistPanel heist;

	public HeistCore() {

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("THE HEIST!");
		setLocation(0, 0);
		heist = new HeistPanel();
		setContentPane(heist);
		heist.setPreferredSize(new Dimension(heist.width, heist.height));
		pack();
		
		
		setVisible(true);
	}

	public static void main(String[] args) {
		mainClass = new HeistCore();
	}

}