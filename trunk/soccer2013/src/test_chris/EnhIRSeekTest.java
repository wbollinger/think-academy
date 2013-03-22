package test_chris;

import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import soccer.EnhIRSeekerV2;

public class EnhIRSeekTest {

	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		EnhIRSeekerV2 ir = new EnhIRSeekerV2(SensorPort.S1);

		LCD.drawString("Demo EnhIRSeekerV2", 0, 1);

		while (true) {
			ir.update();

			LCD.drawString("Edir:  ", 0, 3);
			LCD.drawInt(ir.getDir(), 6 * 5, 3);

			LCD.drawString("Estr:     ", 6 * 7, 3);
			LCD.drawInt(ir.getStrength(), 6 * 12, 3);

			sleep(50);
		}
	}

}
