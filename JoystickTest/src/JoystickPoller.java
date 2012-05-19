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

import java.io.DataOutputStream;
import java.io.IOException;

import com.centralnexus.input.*;

public class JoystickPoller extends Thread {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3295186028202390147L;

	Joystick joy;

	/** polling interval for this joystick */
	private int interval = 50;

	
	
	DataOutputStream out;

	JoystickPoller() throws IOException {

		joy = Joystick.createInstance();
		
	}
	
	JoystickPoller(Joystick joystick, DataOutputStream dataOut) throws IOException {
		
		joy = joystick;
		out = dataOut;

		setPollInterval(50);

		startPolling();
		
		
	}

	JoystickPoller(int joystickID) throws IOException {

		joy = Joystick.createInstance(joystickID);
		
	}

	/**
	 * This is used by the internal thread. It creates a lot of String objects,
	 * so it uses the garbage collector a lot. Since this is for testing only,
	 * this is not a problem for speed.
	 */
	public void run() {
		String msg = "";
		for (;;) {
			joy.poll();
			msg = "("+joy.getX()+","+joy.getY()+")";
			System.err.println(msg);
			
			try {
				out.writeUTF(msg);
			} catch (IOException e) {
				System.out.println("ERROR WRITING JOYSTICK TO OUTPUT:"+e);
			}
			
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
	}


	public void startPolling() {
		this.setName("JoystickPoller");
		this.start();
	}

	public void setDeadZone(double deadZone) {
		joy.setDeadZone(deadZone);
	}

	public static void main(String args[]) {
		
	}
}
