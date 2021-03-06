package rescue;
//------------------------------------------------------------------------
//  Find the black line
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
		debugln("FindLine enter");
		robot.stop();
		robot.sleep(70);
		//robot.forward(6);

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
		robot.dropCompass();
		robot.debug("Line Straddled\n");
	}
}
