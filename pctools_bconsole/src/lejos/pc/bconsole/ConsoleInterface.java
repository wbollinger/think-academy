package lejos.pc.bconsole;

import java.awt.Color;
import java.io.*;

/**
 * The text and GUI capabilities provided by the console.
 */
public interface ConsoleInterface {

	public void print(Object o);

	public void println(Object o);

	public Reader getIn();

	public PrintStream getOut();

	public PrintStream getErr();

	public void error(Object o);
	
	/**
	 * Additional capabilities of an interactive console.
	 */
	public void print(Object o, Color color);

	// public void setNameCompletion( NameCompletion nc );

	/** e.g. the wait cursor */
	//public void setWaitFeedback(boolean on);
}
