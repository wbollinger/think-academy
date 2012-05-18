import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class HeistPanel extends JPanel implements KeyListener {

	public static HeistPanel mainPanel;

	Graphics dbg;
	Image dbi;

	Player p1;
	
	ArrayList<Wall> walls;

	ArrayList<Integer> keys;

	public HeistPanel() {
		mainPanel = this;
		addKeyListener(this);
		setFocusable(true);
		requestFocus();
		keys = new ArrayList<Integer>();
		walls = new ArrayList<Wall>();
		p1 = new Player(400, 300);
		walls.add(new Wall(785, 10, 5, 580));//right
		walls.add(new Wall(10, 10, 5, 580));//left
		walls.add(new Wall(10, 10, 780, 5));//top
		walls.add(new Wall(50, 585, 740, 5));//bottom
		
	}

	public void paint(Graphics g) {
		if (dbg == null) {
			if (getWidth() != HeistCore.width
					|| getHeight() != HeistCore.height) {
				HeistCore.mainClass.setSize(HeistCore.width * 2 - getWidth(),
						HeistCore.height * 2 - getHeight());
			}
			while (dbi == null) {
				dbi = createImage(HeistCore.width, HeistCore.height);
			}
			while (dbg == null) {
				dbg = dbi.getGraphics();
			}
		}
		draw(dbg);
		g.drawImage(dbi, 0, 0, this);
		repaint();
	}

	public void draw(Graphics g) {
		update();
		drawBackground(g);
		p1.draw(g);
		for(int i = 0; i < walls.size(); i++) {
			walls.get(i).draw(g);
		}
	}
	
	public void drawBackground(Graphics g){
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, HeistCore.width, HeistCore.height);
		g.setColor(Color.WHITE);
		g.fillRect(10, 10, HeistCore.width-20, HeistCore.height-20);
	}

	public void update() {
		if(keys.contains(KeyEvent.VK_SHIFT)){
			p1.setSneaking(false);
		} else {
			p1.setSneaking(true);
		}
		
		if (keys.contains(KeyEvent.VK_W) && keys.contains(KeyEvent.VK_D)
				&& p1.getY() > 0 && (p1.getX() + 15) < HeistCore.width) {
			p1.moveUpRight(Player.baseSpeed);
			for(int i = 0; i < walls.size(); i++) {
				if(p1.collisionCheck(walls.get(i))) {
					p1.moveDownLeft(Player.baseSpeed);
				}
			}
		} else if (keys.contains(KeyEvent.VK_S) && keys.contains(KeyEvent.VK_D)
				&& (p1.getY() + 15) < HeistCore.height
				&& (p1.getX() + 15) < HeistCore.width) {
			p1.moveDownRight(Player.baseSpeed);
			for(int i = 0; i < walls.size(); i++) {
				if(p1.collisionCheck(walls.get(i))) {
					p1.moveUpLeft(Player.baseSpeed);
				}
			}
		} else if (keys.contains(KeyEvent.VK_S) && keys.contains(KeyEvent.VK_A)
				&& (p1.getY() + 15) < HeistCore.height && p1.getX() > 0) {
			p1.moveDownLeft(Player.baseSpeed);
			for(int i = 0; i < walls.size(); i++) {
				if(p1.collisionCheck(walls.get(i))) {
					p1.moveUpRight(Player.baseSpeed);
				}
			}
		} else if (keys.contains(KeyEvent.VK_W) && keys.contains(KeyEvent.VK_A)
				&& p1.getY() > 0 && p1.getX() > 0) {
			p1.moveUpLeft(Player.baseSpeed);
			for(int i = 0; i < walls.size(); i++) {
				if(p1.collisionCheck(walls.get(i))) {
					p1.moveDownRight(Player.baseSpeed);
				}
			}
		} else if (keys.contains(KeyEvent.VK_W) && p1.getY() > 0) {
			p1.moveUp(Player.baseSpeed);
			for(int i = 0; i < walls.size(); i++) {
				if(p1.collisionCheck(walls.get(i))) {
					p1.moveDown(Player.baseSpeed);
				}
			}
		} else if (keys.contains(KeyEvent.VK_S)
				&& (p1.getY() + 15) < HeistCore.height) {
			p1.moveDown(Player.baseSpeed);
			for(int i = 0; i < walls.size(); i++) {
				if(p1.collisionCheck(walls.get(i))) {
					p1.moveUp(Player.baseSpeed);
				}
			}
		} else if (keys.contains(KeyEvent.VK_A) && p1.getX() > 0) {
			p1.moveLeft(Player.baseSpeed);
			for(int i = 0; i < walls.size(); i++) {
				if(p1.collisionCheck(walls.get(i))) {
					p1.moveRight(Player.baseSpeed);
				}
			}
		} else if (keys.contains(KeyEvent.VK_D)
				&& (p1.getX() + 15) < HeistCore.width) {
			p1.moveRight(Player.baseSpeed);
			for(int i = 0; i < walls.size(); i++) {
				if(p1.collisionCheck(walls.get(i))) {
					p1.moveLeft(Player.baseSpeed);
				}
			}
		}
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
