package soccer;

public class StateGoalie extends State {

	private static StateGoalie instance = new StateGoalie();
	
	
	@Override
	public void enter(Robot r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Robot r) {
		double heading = r.compass.getDegrees();
		r.io.debugln("" + heading);
			while (true) {
				
				if ((r.IR.getDirection() == 5)||(r.IR.getDirection() == 0)) {
					r.stopAll();
				} else if (r.IR.getDirection() < 5) {
					//turns left?
						r.nav.moveDir(180);
				} else if (r.IR.getDirection() > 5) {
					//turns right?
					r.nav.moveDir(0);
				}
			//	if(r.compass.getDegrees() < heading + 5
			//			&& r.compass.getDegrees() > heading - 5){
			//		r.nav.rotateTo((float)heading);
					
			//	}
				//if(r.USX.getDistance() < 100){
					//r.moveForward();
					//if(r.USX.getDistance() > 120){
						//r.stopAll();
					//}
				//}

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
