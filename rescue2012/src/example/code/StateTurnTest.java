package example.code;
import rescue.Robot;
import rescue.State;
import rescue.StateExit;


public class StateTurnTest extends State{
	static private StateTurnTest instance = new StateTurnTest();

	private StateTurnTest() {
	}

	// this is a singleton
	public static StateTurnTest getInstance() {
		return instance;
	}

	public void enter(Robot robot) {
		debug("StTurnTest enter\n");
	}

	public void execute(Robot robot) {
		debug("StTurnTest execute\n");
		robot.forward(30);
		robot.changeState(StateExit.getInstance());
	}

	public void exit(Robot robot) {
		debug("StTurnTest exit\n");
	}
}
