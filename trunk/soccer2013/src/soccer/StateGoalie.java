package soccer;

public class StateGoalie extends State {

	@Override
	public void enter(Robot r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute(Robot r) {
			while (true) {

				if (r.IR.getDirection() > 0 && r.IR.getDirection() < 5) {
					//turns left?
						r.nav.moveDir(240);
				}
				if (r.IR.getDirection() == 5) {
					r.stopAll();
				}
				if (r.IR.getDirection() > 5) {
					//turns right?
					r.nav.moveDir(60);
				}

			}
		
		

		
	}

	@Override
	public void exit(Robot r) {
		// TODO Auto-generated method stub
		
	}

}
