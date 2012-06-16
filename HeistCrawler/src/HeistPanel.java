import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class HeistPanel extends JPanel {

	public static HeistPanel mainPanel;

	InputManager input;
	Graphics dbg;
	Image dbi;
	Image invImage;
	Image backgroundImage;
	Image invSelect;

	Player p1;

	ArrayList<Wall> walls;
	ArrayList<Door> doors;
	boolean doorSet;
	ArrayList<Item> items;

	ArrayList<Integer> keys;

	ArrayList<Bullet> bullets;
	boolean triggerDown;

	final Point[] invSlots = new Point[] { new Point(70, 641),
			new Point(136, 641), new Point(202, 641), new Point(268, 641),
			new Point(334, 641), new Point(400, 641) };

	int itemSelected;

	int mousex, mousey;

	public HeistPanel() {
		mainPanel = this;
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

		keys = new ArrayList<Integer>();
		walls = new ArrayList<Wall>();
		doors = new ArrayList<Door>();
		items = new ArrayList<Item>();
		bullets = new ArrayList<Bullet>();
		p1 = new Player(393, 560);
		addWalls();
		addDoors();
		addItems();
		itemSelected = 1;
	}

	public void addWalls() {
		walls.add(new Wall(785, 10, 5, 580));// right
		walls.add(new Wall(10, 10, 5, 580));// left
		walls.add(new Wall(10, 10, 780, 5));// top
		walls.add(new Wall(10, 585, 780, 5));// bottom
		walls.add(new Wall(196, 10, 5, 580));
		walls.add(new Wall(589, 10, 5, 580));
		walls.add(new Wall(281, 365, 228, 5));
	}

	public void addDoors() {
		doors.add(new Door(196, 365, Door.DoorTypes.OPENS_UP, true));
		doors.add(new Door(281, 365 - Door.width + 5,
				Door.DoorTypes.OPENS_LEFT, false));

		doors.add(new Door(200, 100, Door.DoorTypes.OPENS_UP, true));
		doors.add(new Door(300, 100, Door.DoorTypes.OPENS_DOWN, true));
		doors.add(new Door(400, 100, Door.DoorTypes.OPENS_LEFT, true));
		doors.add(new Door(500, 100, Door.DoorTypes.OPENS_RIGHT, true));

	}

	public void addItems() {
		items.add(new ItemLockPick("Test"));
		items.add(new ItemTranqPistol("Test2"));
		items.add(new ItemLockPick("Test3"));
		items.add(new ItemLockPick("Test4"));
		items.add(new ItemLockPick("Test5"));
		items.add(new ItemLockPick("Test6"));
	}

	public void paintComponent(Graphics g) {
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
		g.drawImage(backgroundImage, 0, 0, null);
		p1.draw((Graphics2D) g);
		drawInv(g);
		for (int i = 0; i < walls.size(); i++) {
			walls.get(i).draw(g);
		}
		for (int i = 0; i < doors.size(); i++) {
			doors.get(i).draw(g);
		}
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw((Graphics2D) g);
		}

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
				for (int i = 0; i < doors.size(); i++) {
					if (findDistance((int) Math.round(p1.getX()),
							Math.round((int) p1.getY()), doors.get(i)
									.getCurrentX(), doors.get(i).getCurrentY()) < 40) {
						ItemLockPick temp = (ItemLockPick) items
								.get(itemSelected - 1);
						temp.unlockDoor(doors.get(i));

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
			for (int i = 0; i < doors.size(); i++) {
				if (!doorSet
						&& findDistance((int) Math.round(p1.getX()),
								Math.round((int) p1.getY()), doors.get(i)
										.getCurrentX(), doors.get(i)
										.getCurrentY()) < 40) {
					if (!doors.get(i).isLocked()) {
						doors.get(i).toggleDoor();
					}
					doorSet = true;
				}
			}
		} else {
			doorSet = false;
		}

		if (input.isKeyPressed(KeyEvent.VK_W)
				&& input.isKeyPressed(KeyEvent.VK_D) && p1.getY() > 0
				&& (p1.getX() + 15) < HeistCore.width) {
			p1.moveUpRight(Player.baseSpeed);
			for (int i = 0; i < walls.size(); i++) {
				if (p1.collisionCheck(walls.get(i))) {
					p1.moveDownLeft(Player.baseSpeed);
				}
			}
			for (int i = 0; i < doors.size(); i++) {
				if (p1.collisionCheck(doors.get(i))) {
					p1.moveDownLeft(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_S)
				&& input.isKeyPressed(KeyEvent.VK_D)
				&& (p1.getY() + 15) < HeistCore.height
				&& (p1.getX() + 15) < HeistCore.width) {
			p1.moveDownRight(Player.baseSpeed);
			for (int i = 0; i < walls.size(); i++) {
				if (p1.collisionCheck(walls.get(i))) {
					p1.moveUpLeft(Player.baseSpeed);
				}
			}
			for (int i = 0; i < doors.size(); i++) {
				if (p1.collisionCheck(doors.get(i))) {
					p1.moveUpLeft(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_S)
				&& input.isKeyPressed(KeyEvent.VK_A)
				&& (p1.getY() + 15) < HeistCore.height && p1.getX() > 0) {
			p1.moveDownLeft(Player.baseSpeed);
			for (int i = 0; i < walls.size(); i++) {
				if (p1.collisionCheck(walls.get(i))) {
					p1.moveUpRight(Player.baseSpeed);
				}
			}
			for (int i = 0; i < doors.size(); i++) {
				if (p1.collisionCheck(doors.get(i))) {
					p1.moveUpRight(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_W)
				&& input.isKeyPressed(KeyEvent.VK_A) && p1.getY() > 0
				&& p1.getX() > 0) {
			p1.moveUpLeft(Player.baseSpeed);
			for (int i = 0; i < walls.size(); i++) {
				if (p1.collisionCheck(walls.get(i))) {
					p1.moveDownRight(Player.baseSpeed);
				}
			}
			for (int i = 0; i < doors.size(); i++) {
				if (p1.collisionCheck(doors.get(i))) {
					p1.moveDownRight(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_W) && p1.getY() > 0) {
			p1.moveUp(Player.baseSpeed);
			for (int i = 0; i < walls.size(); i++) {
				if (p1.collisionCheck(walls.get(i))) {
					p1.moveDown(Player.baseSpeed);
				}
			}
			for (int i = 0; i < doors.size(); i++) {
				if (p1.collisionCheck(doors.get(i))) {
					p1.moveDown(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_S)
				&& (p1.getY() + 15) < HeistCore.height) {
			p1.moveDown(Player.baseSpeed);
			for (int i = 0; i < walls.size(); i++) {
				if (p1.collisionCheck(walls.get(i))) {
					p1.moveUp(Player.baseSpeed);
				}
			}
			for (int i = 0; i < doors.size(); i++) {
				if (p1.collisionCheck(doors.get(i))) {
					p1.moveUp(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_A) && p1.getX() > 0) {
			p1.moveLeft(Player.baseSpeed);
			for (int i = 0; i < walls.size(); i++) {
				if (p1.collisionCheck(walls.get(i))) {
					p1.moveRight(Player.baseSpeed);
				}
			}
			for (int i = 0; i < doors.size(); i++) {
				if (p1.collisionCheck(doors.get(i))) {
					p1.moveRight(Player.baseSpeed);
				}
			}
		} else if (input.isKeyPressed(KeyEvent.VK_D)
				&& (p1.getX() + 15) < HeistCore.width) {
			p1.moveRight(Player.baseSpeed);
			for (int i = 0; i < walls.size(); i++) {
				if (p1.collisionCheck(walls.get(i))) {
					p1.moveLeft(Player.baseSpeed);
				}
			}
			for (int i = 0; i < doors.size(); i++) {
				if (p1.collisionCheck(doors.get(i))) {
					p1.moveLeft(Player.baseSpeed);
				}
			}
		}

		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update();
		}
		for (int i = 0; i < bullets.size(); i++) {
			for (int j = 0; j < walls.size(); j++) {
				if (bullets.get(i).collisionCheck(walls.get(j))) {
					bullets.remove(i);
					break;
				}
			}

		}
		for (int i = 0; i < bullets.size(); i++) {
			for (int j = 0; j < doors.size(); j++) {
				if (bullets.get(i).collisionCheck(doors.get(j))) {
					bullets.remove(i);
					break;
				}
			}

		}

		p1.movePlayer(input.getMouseX(), input.getMouseY());
	}

}