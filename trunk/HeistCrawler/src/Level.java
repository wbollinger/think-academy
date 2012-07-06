import java.awt.Graphics;
import java.util.ArrayList;


public class Level {
	
	String name;
	
	ArrayList<Wall> walls;
	ArrayList<Door> doors;
	
	public Level(String name) {
		this.name = name;
		walls = new ArrayList<Wall>();
		doors = new ArrayList<Door>();
	}
	
	public void draw(Graphics g) {
		for (int i = 0; i < walls.size(); i++) {
			walls.get(i).draw(g);
		}
		for (int i = 0; i < doors.size(); i++) {
			doors.get(i).draw(g);
		}
	}
	
	
}
