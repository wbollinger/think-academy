package soccer;

public class Vector2D {
	private double x;
	private double y;
	private double angle;
	private double magnitude;
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
		angle = calculateAngle(x, y);
		magnitude = calculateMagnitude(x, y);
		
	}
	
	public Vector2D(double angle) {
		this.angle = angle;
		this.magnitude = 1.0;
		this.x = calculateX();
		this.y = calculateY();
	}
	
	public double calculateX() {
		return getMagnitude()*Math.cos(getAngle());
	}
	
	public static double calculateX(Vector2D a) {
		return a.getMagnitude()*Math.cos(a.getAngle());
	}
	
	public static double calculateX(double angle, double magnitude) {
		return magnitude*Math.cos(angle);
	}
	
	public double calculateY() {
		return getMagnitude()*Math.sin(getAngle());
	}
	
	public static double calculateY(Vector2D a) {
		return a.getMagnitude()*Math.sin(a.getAngle());
	}
	
	public static double calculateY(double angle, double magnitude) {
		return magnitude*Math.sin(angle);
	}
	
	public static double calculateAngle(Vector2D a) {
		return Math.atan2(a.getY(), a.getX());
	}
	
	public static double calculateAngle(double x, double y) {
		return Math.atan2(y, x);
	}
	
	public static double calculateMagnitude(Vector2D a) {
		return Math.sqrt(Math.pow(a.getX(), 2)+Math.pow(a.getY(), 2));
	}
	
	public static double calculateMagnitude(double x, double y) {
		return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
	}
	
	public static double dotProduct(Vector2D a, Vector2D b) {
		return a.getX()*b.getX()+a.getY()*b.getY();
	}
	
	public double dot(Vector2D b) {
		return this.getX()*b.getX()+this.getY()*b.getY();
	}
	
	public Vector2D times(double b) {
		return new Vector2D(this.getX()*b, this.getY()*b);
	}
	
	public static double toRadian(double angle) {
		return Math.PI*angle/180.0;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public double getMagnitude() {
		return magnitude;
	}
}
