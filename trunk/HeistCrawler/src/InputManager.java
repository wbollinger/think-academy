import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class InputManager implements KeyListener, MouseMotionListener {

	ArrayList<Integer> pressedKeys;
	Point mousePos;

	public InputManager() {
		pressedKeys = new ArrayList<Integer>();
		mousePos = new Point();
	}

	public boolean isKeyPressed(int key) {
		if (pressedKeys.contains(key)) {
			return true;
		} else {
			return false;
		}
	}

	public int getMouseX() {
		return mousePos.x;
	}

	public int getMouseY() {
		return mousePos.y;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePos.x = e.getX();
		mousePos.y = e.getY();

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!pressedKeys.contains(e.getKeyCode())) {
			pressedKeys.add(e.getKeyCode());
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		pressedKeys.remove(new Integer(e.getKeyCode()));

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
