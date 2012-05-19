import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class InventoryPanel extends JPanel implements KeyListener {
	public static int width = HeistPanel.width;
	public static int height = 100;
	
	Graphics dbg;
	Image dbi;
	
	ArrayList<Integer> keys;
	
	public void paint(Graphics g) {
		if (dbg == null) {
			if (getWidth() != width
					|| getHeight() != height) {
				HeistCore.mainClass.setSize(HeistCore.width * 2 - getWidth(),
						HeistCore.height * 2 - getHeight());
			}
			while (dbi == null) {
				dbi = createImage(width, height);
			}
			while (dbg == null) {
				dbg = dbi.getGraphics();
			}
		}
		draw(dbg);
		g.drawImage(dbi, 0, 0, this);
		repaint();
	}
	
	public void drawBackground(Graphics g){
		g.setColor(Color.MAGENTA);
		g.fillRect(0, 0, 795, 75);
		g.setColor(Color.BLUE);
		g.fillRect(5, 5, width-10, height-10);
	}

	public void draw(Graphics g) {
		update();
		drawBackground(g);

	}
	
	public void update() {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (!keys.contains(e.getKeyCode())) {
			keys.add(e.getKeyCode());
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys.remove(new Integer(e.getKeyCode()));

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
	
}
