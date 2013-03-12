package soccer;

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
		while (true) {

			if (r.USY.getDistance() > 15) {
				while (r.USY.getDistance() > 15) {
					r.nav.moveDir(270, 100);
				}
				r.floatAll();
			} else if (r.USY.getDistance() < 10) {
				while (r.USY.getDistance() < 10) {
					r.nav.moveDir(90, 100);
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
			} else if ((r.IR.getDirection() < 5) && !(r.USX.getDistance() > 121)) {
				// moves left, unless at edge of goal
				r.nav.moveDir(180, 100);
			} else if ((r.IR.getDirection() > 5) && !(r.USX.getDistance() < 61)) {
				// moves right, unless at edge of goal
				r.nav.moveDir(0, 100);
			}

			// }
			// if(r.USX.getDistance() < 100){
			// r.moveForward();
			// if(r.USX.getDistance() > 120){
			// r.stopAll();
			// }
			// }

		}

	}

	public static StateGoalie getInstance() {
		return instance;
	}

	@Override
	public void exit(Robot r) {
		// TODO Auto-generated method stub

	}

}
