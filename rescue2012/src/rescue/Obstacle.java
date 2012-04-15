package rescue;
public class Obstacle {
	private double xLength;
	private double yLength;

	public Obstacle(double xLength, double yLength) {

		this.xLength = xLength;
		this.yLength = yLength;

	}

	public double getxLength() {
		return xLength;
	}

	public void setxLength(double xLength) {
		this.xLength = xLength;
	}

	public double getyLength() {
		return yLength;
	}

	public void setyLength(double yLength) {
		this.yLength = yLength;
	}

}
