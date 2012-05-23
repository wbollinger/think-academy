import java.awt.*;
import javax.swing.*;

public class HeistCore extends JFrame {

	public static int width = 800;
	public static int height = 800;
	public static HeistCore mainClass;
	private Toolkit t;
	private Dimension s;
	static HeistPanel heist;

	public HeistCore() {

		setSize(width + 8, height + 27);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		heist = new HeistPanel();
		add(heist);
		heist.setSize(width, height);
		getContentPane().setLayout(new BorderLayout());

		setResizable(false);
		setTitle("THE HEIST!");
		t = getToolkit();
		s = t.getScreenSize();
		setLocation(s.width / 2 - getWidth() / 2, s.height / 2 - getHeight()
				/ 2 - 30);
		setVisible(true);
	}

	public static void main(String[] args) {
		mainClass = new HeistCore();
	}

}