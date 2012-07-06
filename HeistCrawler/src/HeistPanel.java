import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import javax.swing.*;

public class HeistPanel extends JPanel {

	public static HeistPanel mainPanel;
	
	public static int width = 800;
	public static int height = 800;
	int currentLevel;

	InputManager input;
	
	LevelReader lvlLoader;
	Level lvl;
	
	Image invImage;
	Image backgroundImage;
	Image invSelect;

	Player p1;

	boolean doorSet;
	ArrayList<Item> items;

	ArrayList<Bullet> bullets;
	boolean triggerDown;

	final Point[] invSlots = new Point[] { new Point(70, 641),
			new Point(136, 641), new Point(202, 641), new Point(268, 641),
			new Point(334, 641), new Point(400, 641) };

	int itemSelected;

	public HeistPanel() {
		mainPanel = this;
		
		lvlLoader = new LevelReader("Levels");
		currentLevel =  1;
		lvl = lvlLoader.readLevelFile(currentLevel);
		
		input = new InputManager();
		addKeyListener(input);
		addMouseMotionListener(input);
		
		setFocusable(true);
		requestFocus();
		invImage = Toolkit.getDefaultToolkit().createImage(
				"Images/Invbackground.png");
		backgroundImage = Toolkit.getDefaultToolkit().createImage(
				"Images/Background.png");
		invSelect = Toolkit.getDefaultToolkit().createImage(
				"Images/InvSelection.png");
		prepareImage(invImage, this);
		prepareImage(backgroundImage, this);
		prepareImage(invSelect, this);

		items = new ArrayList<Item>();
		bullets = new ArrayList<Bullet>();
		p1 = new Player(393, 560);

		addItems();
		itemSelected = 1;
	}
	


	public void addItems() {
		items.add(new ItemLockPick("Test"));
		items.add(new ItemTranqPistol("Test2"));
		items.add(new ItemLockPick("Test3"));
		items.add(new ItemLockPick("Test4"));
		items.add(new ItemLockPick("Test5"));
		items.add(new ItemLockPick("Test6"));
	}
	
	public void increaseLevel(){
		currentLevel += 1;
	}
	
	public void decreaseLevel(){
		currentLevel -= 1;
	}
	public void paintComponent(Graphics g) {
		draw(g);
		repaint();
	}

	public void draw(Graphics g) {
		update();
		g.drawImage(backgroundImage, 0, 0, null);
		drawInv(g);
		lvl.draw(g);
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw((Graphics2D) g);
		}
		Color view = new Color(255, 255, 255, 80);
		g.setColor(view);
		g.fillArc((int)(p1.getX()+11-200),(int)(p1.getY()+7-200),400,400,(int)(-p1.heading*180/Math.PI-45),90);
		p1.draw((Graphics2D) g);

	}

	public void drawInv(Graphics g) {
		g.drawImage(invImage, 0, 600, null);
		for (int i = 0; i < items.size(); i++) {
			g.drawImage(items.get(i).invItemImage, invSlots[i].x,
					invSlots[i].y, null);
		}

		g.drawImage(invSelect, invSlots[itemSelected - 1].x,
				invSlots[itemSelected - 1].y, null);

	}

	public double findDistance(int x1, int y1, int x2, int y2) {
		double distance = Math
				.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
		return distance;
	}

	public static double findAngle(double x1, double y1, double x2, double y2) {
		double rise = y2 - y1;
		double run = x2 - x1;
		double arc = Math.atan2(rise, run);
		return arc;
	}

	public void update() {

		if (input.isKeyPressed(KeyEvent.VK_1)) {
			itemSelected = 1;
		} else if (input.isKeyPressed(KeyEvent.VK_2)) {
			itemSelected = 2;
		} else if (input.isKeyPressed(KeyEvent.VK_3)) {
			itemSelected = 3;
		} else if (input.isKeyPressed(KeyEvent.VK_4)) {
			itemSelected = 4;
		} else if (input.isKeyPressed(KeyEvent.VK_5)) {
			itemSelected = 5;
		} else if (input.isKeyPressed(KeyEvent.VK_6)) {
			itemSelected = 6;
		}

		p1.setHeldItem(items.get(itemSelected - 1));

		if (input.isKeyPressed(KeyEvent.VK_SHIFT)) {
			p1.setSneaking(false);
		} else {
			p1.setSneaking(true);
		}

		if (input.isKeyPressed(KeyEvent.VK_SPACE)) {

			if (items.get(itemSelected - 1).getID() == 1) {
				for (int i = 0; i < lvl.doors.size(); i++) {
					if (findDistance((int) Math.round(p1.getX()),
							Math.round((int) p1.getY()), lvl.doors.get(i)
									.getCurrentX(), lvl.doors.get(i).getCurrentY()) < 40) {
						ItemLockPick temp = (ItemLockPick) items
								.get(itemSelected - 1);
						temp.unlockDoor(lvl.doors.get(i));

					}
				}
			} else if (items.get(itemSelected - 1).getID() == 2) {
				if (!triggerDown) {
					bullets.add(p1.shoot());
					triggerDown = true;
				}

			}
		} else {
			triggerDown = false;
		}

		if (input.isKeyPressed(KeyEvent.VK_E)) {
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (!doorSet
						&& findDistance((int) Math.round(p1.getX()),
								Math.round((int) p1.getY()), lvl.doors.get(i)
										.getCurrentX(), lvl.doors.get(i)
										.getCurrentY()) < 40) {
					if (!lvl.doors.get(i).isLocked()) {
						lvl.doors.get(i).toggleDoor();
					}
					doorSet = true;
				}
			}
		} else {
			doorSet = false;
		}

		if (input.isKeyPressed(KeyEvent.VK_W)
				&& input.isKeyPressed(KeyEvent.VK_D) && p1.getY() > 0
				&& (p1.getX() + 15) < width) {
			p1.moveUpRight(Player.baseSpeed);
			for (int i = 0; i < lvl.walls.size(); i++) {
				if (p1.collisionCheck(lvl.walls.get(i))) {
					p1.moveDownLeft(Player.baseSpeed);
				}
			}
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (p1.collisionCheck(lvl.doors.get(i))) {
					p1.moveDownLeft(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_S)
				&& input.isKeyPressed(KeyEvent.VK_D)
				&& (p1.getY() + 15) < height
				&& (p1.getX() + 15) < width) {
			p1.moveDownRight(Player.baseSpeed);
			for (int i = 0; i < lvl.walls.size(); i++) {
				if (p1.collisionCheck(lvl.walls.get(i))) {
					p1.moveUpLeft(Player.baseSpeed);
				}
			}
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (p1.collisionCheck(lvl.doors.get(i))) {
					p1.moveUpLeft(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_S)
				&& input.isKeyPressed(KeyEvent.VK_A)
				&& (p1.getY() + 15) < height && p1.getX() > 0) {
			p1.moveDownLeft(Player.baseSpeed);
			for (int i = 0; i < lvl.walls.size(); i++) {
				if (p1.collisionCheck(lvl.walls.get(i))) {
					p1.moveUpRight(Player.baseSpeed);
				}
			}
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (p1.collisionCheck(lvl.doors.get(i))) {
					p1.moveUpRight(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_W)
				&& input.isKeyPressed(KeyEvent.VK_A) && p1.getY() > 0
				&& p1.getX() > 0) {
			p1.moveUpLeft(Player.baseSpeed);
			for (int i = 0; i < lvl.walls.size(); i++) {
				if (p1.collisionCheck(lvl.walls.get(i))) {
					p1.moveDownRight(Player.baseSpeed);
				}
			}
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (p1.collisionCheck(lvl.doors.get(i))) {
					p1.moveDownRight(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_W) && p1.getY() > 0) {
			p1.moveUp(Player.baseSpeed);
			for (int i = 0; i < lvl.walls.size(); i++) {
				if (p1.collisionCheck(lvl.walls.get(i))) {
					p1.moveDown(Player.baseSpeed);
				}
			}
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (p1.collisionCheck(lvl.doors.get(i))) {
					p1.moveDown(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_S)
				&& (p1.getY() + 15) < height) {
			p1.moveDown(Player.baseSpeed);
			for (int i = 0; i < lvl.walls.size(); i++) {
				if (p1.collisionCheck(lvl.walls.get(i))) {
					p1.moveUp(Player.baseSpeed);
				}
			}
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (p1.collisionCheck(lvl.doors.get(i))) {
					p1.moveUp(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_A) && p1.getX() > 0) {
			p1.moveLeft(Player.baseSpeed);
			for (int i = 0; i < lvl.walls.size(); i++) {
				if (p1.collisionCheck(lvl.walls.get(i))) {
					p1.moveRight(Player.baseSpeed);
				}
			}
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (p1.collisionCheck(lvl.doors.get(i))) {
					p1.moveRight(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_D)
				&& (p1.getX() + 15) < width) {
			p1.moveRight(Player.baseSpeed);
			for (int i = 0; i < lvl.walls.size(); i++) {
				if (p1.collisionCheck(lvl.walls.get(i))) {
					p1.moveLeft(Player.baseSpeed);
				}
			}
			for (int i = 0; i < lvl.doors.size(); i++) {
				if (p1.collisionCheck(lvl.doors.get(i))) {
					p1.moveLeft(Player.baseSpeed);
				}
			}
		}

		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update();
		}
		for (int i = 0; i < bullets.size(); i++) {
			for (int j = 0; j < lvl.walls.size(); j++) {
				if (bullets.get(i).collisionCheck(lvl.walls.get(j))) {
					bullets.remove(i);
					break;
				}
			}

		}
		for (int i = 0; i < bullets.size(); i++) {
			for (int j = 0; j < lvl.doors.size(); j++) {
				if (bullets.get(i).collisionCheck(lvl.doors.get(j))) {
					bullets.remove(i);
					break;
				}
			}

		}

		p1.movePlayer(input.getMouseX(), input.getMouseY());
	}

}