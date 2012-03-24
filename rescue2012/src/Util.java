/**
 * This corrects bugs in Lejos floor and round methods. Can remove this and
 * return to Math. versions when we move the next Lejos release after 0.9.1
 * 
 * @author chris
 * 
 */
public class Util {
	// dividing by 2 for some kind of safety margin
	private static final float ROUND_FLOAT_MAX = Integer.MAX_VALUE >> 1;
	private static final float ROUND_FLOAT_MIN = -ROUND_FLOAT_MAX;
	// dividing by 2 for some kind of safety margin
	private static final double ROUND_DOUBLE_MAX = Long.MAX_VALUE >> 1;
	private static final double ROUND_DOUBLE_MIN = -ROUND_DOUBLE_MAX;

	/* ========================= rounding functions ========================= */

	/**
	 * Returns the largest (closest to positive infinity) double value that is
	 * not greater than the argument and is equal to a mathematical integer.
	 */
	public static double floor(double a) {
		if (a > 0.0)
			return (a > ROUND_DOUBLE_MAX) ? a : (double) (long) a;

		if (a < ROUND_DOUBLE_MIN)
			return a;

		// if b==a, there were no decimal places (also handles negative zero)
		double b = (long) a;
		return b == a ? a : (b - 1.0);
	}

	/**
	 * Returns the closest int to the argument.
	 */
	public static int round(float a) {
		// check whether rounding required
		if (a < ROUND_FLOAT_MIN || a > ROUND_FLOAT_MAX)
			return (int) a;

		return (int) Util.floor(a + 0.5);
	}

	/**
	 * Returns the closest long to the argument.
	 */
	public static long round(double a) {
		// no rounding required
		if (a < ROUND_DOUBLE_MIN || a > ROUND_DOUBLE_MAX)
			return (long) a;

		return (long) Util.floor(a + 0.5);
	}
}
