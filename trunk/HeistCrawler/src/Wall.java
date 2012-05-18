import java.awt.*;


public class Wall {
	
	private int xPos;
	private int yPos;
	private int width;
	private int height;
	private Color wallColor;
	Rectangle rect;
	
	public Wall(int x, int y, int width, int height) {
		this.xPos = x;
		this.yPos = y;
		this.width = width;
		this.height = height;
		wallColor = Color.BLACK;
		rect = new Rectangle(xPos, yPos, width, height);
	}
	
	public Wall(int x, int y, int width, int height, Color color) {
		this.xPos = x;
		this.yPos = y;
		this.width = width;
		this.height = height;
		wallColor = color;
		rect = new Rectangle(xPos, yPos, width, height);
	}
	
	public void draw(Graphics g) {
		Color temp = g.getColor();
		g.setColor(wallColor);
		g.fillRect(xPos, yPos, width, height);
		g.setColor(temp);
	}

}
