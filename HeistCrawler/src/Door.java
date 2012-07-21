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
		OPENS_UP_HINGE_RIGHT, OPENS_UP_HINGE_LEFT, OPENS_DOWN_HINGE_RIGHT, OPENS_DOWN_HINGE_LEFT,
		OPENS_LEFT_HINGE_TOP, OPENS_LEFT_HINGE_BOTTOM, OPENS_RIGHT_HINGE_TOP, OPENS_RIGHT_HINGE_BOTTOM
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
		try {
			MakeSound.playSound("Sounds/Pick_Lock.wav");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.locked = locked;
	}

	public void setClosed(boolean closed) {
		switch (door) {
		case OPENS_UP_HINGE_RIGHT:
			setClosedUpRight(closed);
			break;
		case OPENS_UP_HINGE_LEFT:
			setClosedUpLeft(closed);
			break;
		case OPENS_DOWN_HINGE_RIGHT:
			setClosedDownRight(closed);
			break;
		case OPENS_DOWN_HINGE_LEFT:
			setClosedDownLeft(closed);
			break;
		case OPENS_LEFT_HINGE_TOP:
			setClosedLeftTop(closed);
			break;
        case OPENS_LEFT_HINGE_BOTTOM:
        	setClosedLeftBottom(closed);
			break;
        case OPENS_RIGHT_HINGE_TOP:
        	setClosedRightTop(closed);
        	break;
        case OPENS_RIGHT_HINGE_BOTTOM:
        	setClosedRightBottom(closed);
        	break;
		}
	}

	public void setClosedUpRight(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos+5, yPos, width, height);
		} else {
			rect.setBounds(xPos+width, yPos-width+5, height, width);
		}
	}
	
	public void setClosedUpLeft(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, width, height);
		} else {
			rect.setBounds(xPos, yPos- width + 5, height, width);
		}
	}

	public void setClosedDownRight(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, width, height);
		} else {
			rect.setBounds(xPos+width, yPos, height, width);
		}
	}
	
	public void setClosedDownLeft(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, width, height);
		} else {
			rect.setBounds(xPos, yPos, height, width);
		}
	}
	
	public void setClosedLeftTop(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, height, width);
		} else {
			rect.setBounds(xPos - width+5, yPos, width, height);
		}
	}
	
	public void setClosedLeftBottom(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, height, width);
		} else {
			rect.setBounds(xPos - width+5, yPos+width, width, height);
		}
	}
	
	public void setClosedRightTop(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, height, width);
		} else {
			rect.setBounds(xPos, yPos, width, height);
		}
	}
	
	public void setClosedRightBottom(boolean closed) {
		this.closed = closed;
		if (closed) {
			rect.setBounds(xPos, yPos, height, width);
		} else {
			rect.setBounds(xPos, yPos+width, width, height);
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
		case OPENS_UP_HINGE_RIGHT:
			if (closed) {
				currentXPos = xPos+5;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, width, height);
			} else {
				currentXPos = xPos+width;
				currentYPos = yPos - width + 5;
				g.fillRect(currentXPos, currentYPos, height, width);
			}
			break;
		case OPENS_UP_HINGE_LEFT:
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
		case OPENS_DOWN_HINGE_RIGHT:
			if (closed) {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, width, height);
			} else {
				currentXPos = xPos+width;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, height, width);
			}
			break;
		case OPENS_DOWN_HINGE_LEFT:
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
		case OPENS_LEFT_HINGE_TOP:
			if (closed) {
			currentXPos = xPos;
			currentYPos = yPos;
			g.fillRect(currentXPos, currentYPos, height, width);
		} else {
			currentXPos = xPos - width + 5;
			currentYPos = yPos;
			g.fillRect(currentXPos, currentYPos, width, height);
		}
			break;
        case OPENS_LEFT_HINGE_BOTTOM:
        	if (closed) {
    			currentXPos = xPos;
    			currentYPos = yPos;
    			g.fillRect(currentXPos, currentYPos, height, width);
    		} else {
    			currentXPos = xPos - width + 5;
    			currentYPos = yPos+width;
    			g.fillRect(currentXPos, currentYPos, width, height);
    		}
			break;
        case OPENS_RIGHT_HINGE_TOP:
        	if (closed) {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, height, width);
			} else {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, width, height);
			}
        	break;
        case OPENS_RIGHT_HINGE_BOTTOM:
        	if (closed) {
				currentXPos = xPos;
				currentYPos = yPos;
				g.fillRect(currentXPos, currentYPos, height, width);
			} else {
				currentXPos = xPos;
				currentYPos = yPos+width;
				g.fillRect(currentXPos, currentYPos, width, height);
			}
        	break;
		}
		g.setColor(temp);
	}

}