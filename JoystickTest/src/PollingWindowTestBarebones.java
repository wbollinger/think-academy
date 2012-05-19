/*
THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. NEITHER RECIPIENT NOR
ANY CONTRIBUTORS SHALL HAVE ANY LIABILITY FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING WITHOUT
LIMITATION LOST PROFITS), HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THE PROGRAM
OR THE EXERCISE OF ANY RIGHTS GRANTED HEREUNDER, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGES.

The name of the Copyright Holder may not be used to endorse or promote
products derived from this software without specific prior written permission.

Copyright 2000 George Rhoten and others.

 */

//package com.centralnexus.test;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import com.centralnexus.input.*;

public class PollingWindowTestBarebones extends Frame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3295186028202390147L;

	Joystick joy;

	/** polling interval for this joystick */
	private int interval = 50;

	Thread thread = new Thread(this);

	Label buttonLabel = new Label(), deadZoneLabel = new Label(), xLabel = new Label(),
			yLabel = new Label(), zLabel = new Label(), rLabel = new Label(), uLabel = new Label(), vLabel = new Label(),
			povLabel = new Label();
	Label intervalLabel = new Label();

	PollingWindowTestBarebones() throws IOException {
		super();

		joy = Joystick.createInstance();


	}

	PollingWindowTestBarebones(int joystickID) throws IOException {
		super();

		joy = Joystick.createInstance(joystickID);
		
	}

	/**
	 * This is used by the internal thread. It creates a lot of String objects,
	 * so it uses the garbage collector a lot. Since this is for testing only,
	 * this is not a problem for speed.
	 */
	public void run() {
		for (;;) {
			joy.poll();
			
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	public void setPollInterval(int pollMillis) {
		interval = pollMillis;
		joy.setPollInterval(pollMillis);
		intervalLabel.setText(Integer.toString(interval));
	}

	public void startPolling() {
		thread.start();
	}

	public void setDeadZone(double deadZone) {
		joy.setDeadZone(deadZone);
		updateDeadZone();
	}

	public void updateDeadZone() {
		deadZoneLabel.setText("joy(" + joy.getDeadZone() + ")");
	}

	private static void help() {
		System.out.println("Help:");
		System.out.println(" -h This help screen info");
		System.out.println(" -v Verbose Joystick debug information");
		System.out.println(" -j:n Set the Joystick ID to test (n is an integer)");
		System.out.println(" -j2:n Set the second joystick ID to test (n is an integer)");
		System.out.println(" -d:n Set the dead zone size of the Joystick (n is a real number)");
	}

	public static void main(String args[]) {
		// This first and last one are never there, but this is for internal
		// testing.
		// They should ALWAYS be false.
		try {
			PollingWindowTestBarebones mainFrame;

			int joystickNum = -1;
			double deadZone = -1.0;
			int interval = 50;

			for (int idx = 0; idx < args.length; idx++) {
				if (args[idx].startsWith("-d:")) {
					deadZone = Double.valueOf(args[idx].substring(3, args[idx].length())).doubleValue();
				} else if (args[idx].startsWith("-i:")) {
					interval = Integer.valueOf(args[idx].substring(3, args[idx].length())).intValue();
				} else if (args[idx].startsWith("-j:")) {
					joystickNum = Integer.valueOf(args[idx].substring(3, args[idx].length())).intValue();
				} else if (args[idx].startsWith("-v")) {
					for (int id = -1; id <= Joystick.getNumDevices(); id++) {
						System.out.println("Joystick " + id + ": " + Joystick.isPluggedIn(id));
					}
				} else if (args[idx].startsWith("-h")) {
					help();
				} else {
					System.out.println("Unknown option: " + args[idx]);
					help();
				}
			}
			if (joystickNum >= 0) {
				mainFrame = new PollingWindowTestBarebones(joystickNum);
			} else {
				mainFrame = new PollingWindowTestBarebones();
			}
			if (deadZone >= 0.0) {
				mainFrame.setDeadZone(deadZone);
			}
			mainFrame.setPollInterval(interval);
			mainFrame.updateDeadZone();
			mainFrame.pack();
			mainFrame.setTitle("Polling Joystick");
			// mainFrame.show();
			mainFrame.setVisible(true);
			mainFrame.startPolling();

		} catch (IOException e) {
			System.err.println("");
			System.err.println(e.getMessage());
			System.err.println("Exiting!");
			System.exit(1);
		}
	}
}
