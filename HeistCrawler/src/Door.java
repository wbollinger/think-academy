import java.awt.*;

public class Door {

	private int xPos;
	private int yPos;
	private int currentXPos;
	private int currentYPos;
	public static int width = 45;
	public static int height = 5;
	private Color doorColor;
	private boolean closed = true;
	Rectangle rect;
	private DoorTypes door;
	private boolean locked;

	public enum DoorTypes {
		OPENS_UP, OPENS_DOWN, OPENS_LEFT, OPENS_RIGHT
	};

	public Door(int x, int y, DoorTypes door, boolean closed) {
		this.xPos = x;
		this.yPos = y;
		this.door = door;
		rect = new Rectangle(xPos, yPos, width, height);
		setClosed(closed);
		doorColor = Color.DARK_GRAY;
		locked = true;

	}

	public Door(int x, int y, DoorTypes door, boolean closed, Color color) {
		this.xPos = x;
		this.yPos = y;
		this.door = door;
		rect = new Rectangle(xPos, yPos, width, height);
		setClosed(closed);
		doorColor = color;
		locked = true;
	}

	public int getX() {
		return xPos;
	}

	public int getY() {
		return yPos;
	}

	public int getCurrentX() {
		return currentXPos;
	}

	public int getCurrentY() {
		return currentYPos;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setClosed(boolean closed) {
		switch (door) {
		case OPENS_UP:
			setClosedUp(closed);
			break;
		case OPENS_DOWN:
			setClosedDown(closed);
			break;
		case OPENS_LEFT:
			setClosedLeft(closed);
			break;
		case OPENS_RIGHT:
			setClosedRight(closed);
			break;
		}
	}

	public void setClosedDown(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, width, height);
		} else {
			rect.setBounds(xPos, yPos, height, width);
		}
	}

	public void setClosedUp(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, width, height);
		} else {
			rect.setBounds(xPos, yPos - width + 5, height, width);
		}
	}

	public void setClosedLeft(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, height, width);
		} else {
			rect.setBounds(xPos - width + 5, yPos + width - 5, width, height);
		}
	}

	public void setClosedRight(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, height, width);
		} else {
			rect.setBounds(xPos - width, yPos, width, height);
		}
	}

	public boolean getClosed() {
		return closed;
	}

	public void toggleDoor() {
		setClosed(!getClosed());
	}

	public void draw(Graphics g) {
		Color temp = g.getColor();
		g.setColor(doorColor);
		switch (this.door) {
		case OPENS_UP:
			if (closed) {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, width, height);
			} else {
				currentXPos = xPos;
				currentYPos = yPos - width + 5;
				g.fillRect(currentXPos, currentYPos, height, width);
			}
			break;
		case OPENS_DOWN:
			if (closed) {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, width, height);
			} else {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, height, width);
			}
			break;
		case OPENS_LEFT:
			if (closed) {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, height, width);
			} else {
				currentXPos = xPos - width + 5;
				currentYPos = yPos + width - 5;
				g.fillRect(currentXPos, currentYPos, width, height);
			}
			break;
		case OPENS_RIGHT:
			if (closed) {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, height, width);
			} else {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos - width, currentYPos, width, height);
			}
			break;

		}
		g.setColor(temp);
	}

}