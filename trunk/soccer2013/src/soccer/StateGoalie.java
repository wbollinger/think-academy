package soccer;

import lejos.nxt.Button;

public class StateGoalie extends State {

	private static StateGoalie instance = new StateGoalie();

	@Override
	public void enter(Robot r) {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Robot r) {
		int i = 0;
		double heading = r.compass.getDegrees();

		r.io.debugln("" + heading);
		while (Button.ENTER.isUp()) {

			if (r.USY.getDistance() > 20) {
				while (r.USY.getDistance() > 20) {
					r.nav.moveDir(270);
				}
				r.floatAll();
			} else if (r.USY.getDistance() < 15) {
				while (r.USY.getDistance() < 15) {
					r.nav.moveDir(90);
				}
				r.floatAll();
			}

			if (i > 5) {
				r.nav.pointToHeading((float) heading);
				i = 0;
			} else {
				i++;
			}

			if ((r.IR.getDirection() == 5) || (r.IR.getDirection() == 0)) {
				r.stopAll();
			} else if (r.IR.getDirection() < 5) { //(r.IR.getDirection() < 5) && !(r.USX.getDistance() > 121)
				// moves left, unless at edge of goal
				r.nav.moveDir(180);
			} else if (r.IR.getDirection() > 5) { //(r.IR.getDirection() > 5) && !(r.USX.getDistance() < 61)
				// moves right, unless at edge of goal
				r.nav.moveDir(0);
			}
		}
		r.changeState(StateCommand.getInstance());

	}

	public static StateGoalie getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot r) {
		// TODO Auto-generated method stub

	}

}
