package rescue;
public class StateLineFollowSingle extends State{

	static private StateLineFollowSingle instance = new StateLineFollowSingle();
	
	private StateLineFollowSingle() {
	}
	
	int threshold = 45;

	// this is a singleton
	public static StateLineFollowSingle getInstance() {
		return instance;
	}
	
	public void setThreshold(int thresh) {
		threshold = thresh;
	}
	
	public void enter(Robot robot) {
		debugln("EnterStateLineFollowSingle");
		robot.forward();
	}
	
	public void execute(Robot robot) {
		debugln("ExecuteStateLineFollowSingle");
		
//		if(robot.touch.isPressed()){
//			robot.changeState(StateCommand.getInstance());
//			return;
//		}
		
		int lightVal = 0;

		if(robot.colorsensor != null){
			debugln("color sensor detected");
			lightVal = robot.colorsensor.getLightValue();
		} else {
			lightVal = robot.lightLeft.readValue();
		}
		if(lightVal < threshold) {
			robot.motLeft.setPower(60);
			robot.motRight.setPower(30);
			robot.motLeft.forward();
			robot.motRight.backward();
		}
		else {
			robot.motLeft.setPower(30);
			robot.motRight.setPower(60);
			robot.motLeft.backward();
			robot.motRight.forward();
		}
		
	}
	
	public void exit(Robot robot) {
		robot.stop();
	}
}
