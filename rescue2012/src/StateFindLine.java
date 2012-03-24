import lejos.*;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

//------------------------------------------------------------------------
//  robot starting state - decides what to do when robot is turned on
//------------------------------------------------------------------------
public class StateFindLine extends State {

	static private StateFindLine instance = new StateFindLine();

	private StateFindLine() {
	}

	// this is a singleton
	public static StateFindLine getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		LCD.drawString("Robot: FindLine enter", 0, 0);
		robot.stop();
		robot.sleep(70);
		robot.forward(6);

	}

	public void execute(Robot robot) {
		if (robot.avoidedLeft) {
			robot.findLineLeft();
		} else {
			robot.findLineRight();
		}
		robot.changeState(StateLineFollow.getInstance());
	}

	public void exit(Robot robot) {
		robot.debug("Line Straddled\n");
	}
}
