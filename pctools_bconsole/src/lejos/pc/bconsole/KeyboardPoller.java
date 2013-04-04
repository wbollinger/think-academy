package lejos.pc.bconsole;

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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.centralnexus.input.*;

public class KeyboardPoller extends Thread implements KeyListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3295186028202390147L;

	DataOutputStream out;
	
	ArrayList<Character> keysPressed;
	
	private int interval = 200;

	KeyboardPoller(DataOutputStream dataOut)
			throws IOException {

		out = dataOut;
		keysPressed = new ArrayList<Character>();
	}
	
	public void startPolling() {
		this.setName("KeyboardPoller");
		this.start();
	}
	
	public void run() {
		String msg = "";
		String temp;
		while(true) {
			temp = "blurpig";
			for(int i = 0; i < keysPressed.size(); i++) {
				temp += keysPressed.get(i).toString();
			}
			
			msg = "keydata "+ temp;
			System.out.println(msg);

			try {
				out.writeUTF(msg + '\n');
				out.flush();
			} catch (IOException e) {
				System.err.println("ERROR WRITING KEYBOARD TO OUTPUT:" + e);
				break;
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
	}

	public static void main(String args[]) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(!keysPressed.contains(e.getKeyChar())) {
			keysPressed.add(e.getKeyChar());
			System.out.println("I SEE A LITTLE KEY BEING PRESSED BY YOU");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysPressed.remove(keysPressed.indexOf(e.getKeyChar()));
		System.out.println("I SEE A LITTLE KEY BEING RELEASED BY YOU");

	}
}
