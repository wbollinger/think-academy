//------------------------------------------------------------------------
//  robot starting state - decides what to do when robot is turned on
//------------------------------------------------------------------------
//import lejos.nxt.*;
//import lejos.nxt.comm.RConsole;

public class StateCompositeSweep extends State {

	static private StateCompositeSweep instance = new StateCompositeSweep();
	
	int xShift = 0;
	int yShift = 0;
	int heading = 0;
	
	
	int[][] storage = new int[250][2];

	private StateCompositeSweep() {
	}

	// this is a singleton
	public static StateCompositeSweep getInstance() {
		return instance;
	}
	
	private void sweep(Robot robot) {
		
		int dist;
		double angle;
		int x, y;
		int n = 0;
		
		robot.resetAngle();
		robot.motLeft.forward();
		robot.motRight.backward();
		
		while(Math.abs(robot.getAngle()) < 360.0) {
			//debug(""+robot.getAngle());
			dist = robot.ultrasonic.getDistance();
			angle = robot.getAngle();
			x = (int) (dist*Math.cos(angle*(Math.PI/180.0)));
			y = (int) (dist*Math.sin(angle*(Math.PI/180.0)));
			storage[n][0] = x;
			storage[n][1] = y;
			n++;
		}
		
		robot.stop();
		robot.resetAngle();
	}

	public void enter(Robot robot) {
	}

	public void execute(Robot robot) {
		debug("StCompSweep execute\n");
		
		//debug("first sweep\n");
		sweep(robot);
		for(int i = 0; i < 150; i++) {
			debugln((storage[i][0]+xShift)+"	"+(storage[i][1]+yShift));
		}
		
//		robot.forward(30);
//		xShift = 30;
//		
//		debug("second sweep\n");
//		sweep(robot);
//		for(int i = 0; i < 150; i++) {	
//			debugln((storage[i][0]+xShift)+"	"+(storage[i][1]+yShift));
//		}
//		
//		robot.left(90);
//		heading = 90;
//		robot.forward(30);
//		xShift += 30*Math.cos(heading*Math.PI/180.0);
//		yShift += 30*Math.sin(heading*Math.PI/180.0);
//		
//		robot.right(90);
//		heading = 0;
//		
//		debug("third sweep\n");
//		sweep(robot);
//		for(int i = 0; i < 150; i++) {	
//			debug((storage[i][0]+xShift)+"	"+(storage[i][1]+yShift)+"\n");
//		}
		
		robot.changeState(StateExit.getInstance());
	}

	public void exit(Robot robot) {
		debug("StCompSweep exit\n");
	}
}
