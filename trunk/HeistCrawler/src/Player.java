import java.awt.*;

public class Player {
	
	public static final double baseSpeed = 2.0;

	private double x;
	private double y;
	Rectangle rect;
	
	private boolean isSneaking;

	public Player(int x, int y) {
		this.x = x;
		this.y = y;
		rect = new Rectangle(x, y, 15, 15);
		setSneaking(false);
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setSneaking(boolean isSneaking) {
		this.isSneaking = isSneaking;
	}

	public boolean isSneaking() {
		return isSneaking;
	}

	public void moveUp(double n) {
		if(isSneaking) {
			n /= 3.0;
		}
		y -= n;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
	}
	
	public void moveUpRight(double n) {
		if(isSneaking) {
			n /= 3.0;
		}
		double dist = n/Math.sqrt(2);
		y -= dist;
		x += dist;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
	}
	
	public void moveRight(double n) {
		if(isSneaking) {
			n /= 3.0;
		}
		x += n;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
	}
	
	public void moveDownRight(double n) {
		if(isSneaking) {
			n /= 3.0;
		}
		double dist = n/Math.sqrt(2);
		y += dist;
		x += dist;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
	}

	public void moveDown(double n) {
		if(isSneaking) {
			n /= 3.0;
		}
		y += n;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
	}
	
	public void moveDownLeft(double n) {
		if(isSneaking) {
			n /= 3.0;
		}
		double dist = n/Math.sqrt(2);
		y += dist;
		x -= dist;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
	}

	public void moveLeft(double n) {
		if(isSneaking) {
			n /= 3.0;
		}
		x -= n;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
	}
	
	public void moveUpLeft(double n) {
		if(isSneaking) {
			n /= 3.0;
		}
		double dist = n/Math.sqrt(2);
		y -= dist;
		x -= dist;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
	}
	
	public void draw(Graphics g) {
		Color temp = g.getColor();
		g.setColor(Color.BLACK);
		g.drawRect((int) Math.round(x), (int) Math.round(y), 15, 15);
		g.setColor(temp);
	}
	
	public boolean collisionCheck(Wall w){
		if(rect.intersects(w.rect)){
			return true;
		} else {
			return false;
		}
	}

	

}
