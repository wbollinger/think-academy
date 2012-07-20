import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;


public class Bullet {
	double heading;
	double x;
	double y;
	double speed;
	Rectangle rect;
	Image bullet;
	AffineTransform affineTransform = new AffineTransform(); 
	
	public Bullet(double x, double y, double heading, int speed) {
		this.x = x;
		this.y = y;
		this.heading = heading;
		this.speed = speed;
		rect = new Rectangle((int)x, (int)y, 2, 2);
		bullet = Toolkit.getDefaultToolkit().createImage("Images/Bullet.png");
		try {
			MakeSound.playSound("Sounds/Gun_Shot.wav");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(bullet, affineTransform, HeistCore.heist);
	}
	
	public boolean collisionCheck(Wall w) {
		if (rect.intersects(w.rect)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean collisionCheck(Door d) {
		if (rect.intersects(d.rect)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void update() {
		x += Math.cos(heading)*speed;
		y += Math.sin(heading)*speed;
		rect.setLocation((int)Math.round(x),(int)Math.round(y));
		affineTransform.setToTranslation(x, y);
		affineTransform.rotate(heading+(Math.PI/2)); 
	}
}
