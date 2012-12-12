package soccer;
/**
 * abstract base class to define an interface for a state
 */
public abstract class State {

	/**
	 * this will execute when the state is entered
	 * 
	 * @param r
	 */
	public abstract void enter(Robot r);

	/**
	 * this is the state's normal update function
	 * 
	 * @param r
	 */
	public abstract void execute(Robot r);

	/**
	 * this will execute when the state is exited.
	 * 
	 * @param r
	 */
	public abstract void exit(Robot r);
	
	
	public void debug(String msg) {
		Robot.getRobot().io.debug(msg);
	}

	public void debugln(String msg) {
		debug(msg + "\n");
	}

	
}
