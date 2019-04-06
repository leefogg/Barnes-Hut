package utils;

public class Vector {
	public float X, Y;
	
	public static final Vector
	Zero 	= new Vector(0,0),
	North 	= new Vector(0,-1),
	East 	= new Vector(1,0),
	South 	= new Vector(0,1),
	West 	= new Vector(-1,0);
	
	public Vector(float xpos, float ypos) {
		X = xpos;
		Y = ypos;
	}
	
	public Vector(float angle) {
		this(angle, 1f, 1f);
	}
	
	public Vector(float angle, float width, float height) {
		float rad = (float)Math.toRadians(angle-90);
		X = (float)(width*Math.cos(rad));
		Y = (float)(height*Math.sin(rad));
	}
	
	public float getMagnitude() {
		return (float)Math.sqrt(X * X + Y * Y);
	}
	
	public void setMagnitude(float length) {
		double current_angle = getAngle();
		X = (float)(length * Math.cos(current_angle));
		Y = (float)(length * Math.sin(current_angle));
	}
	
	public Vector scale(float scale) {
		X *= scale;
		Y *= scale;
		return this;
	}
	
	public Vector Normalize() {
		double length = getMagnitude();
		return new Vector((float)(X  / length), (float)(Y / length));
	}
	
	public Vector lookAt(Vector other) {
		float x = other.X - X;
		float y = other.Y - Y;
		double Magnitude = other.getMagnitude();
		return new Vector((float)(x / Magnitude), (float)(y / Magnitude));
	}
	
	public float distanceTo(Vector to) {
		float 	x = to.X - X,
				y = to.Y - Y;
		return (float)Math.sqrt(x*x+y*y);
	}
	
	public double getAngle() {
		return Math.atan2(Y, X);
	}
	
	public float getAngleInDegrees() {
		double angle = (getAngle() * (180/Math.PI)) + 90;
		if (angle < 0) angle = 360 - Math.abs(angle);
		return (float)angle;
	}
	
	public float getAngleInDegrees(Vector angleto) {
		double angle = Math.toDegrees(Math.atan2(X - angleto.X, Y - angleto.Y));
		return (float) ((360 - angle) % 360);
	}
	
	public void rotate(float radians) {
		rotate(new Vector(0,0), radians);
	}
	
	public Vector rotate(Vector pos, float radians) {
		float s = (float)Math.sin(radians);
		float c = (float)Math.cos(radians);
		
		// translate point back to origin:
		X -= pos.X;
		Y -= pos.Y;
		
		// rotate point
		float xnew = X * c - Y * s;
		float ynew = X * s + Y * c;
		
		// translate point back:
		X = xnew + pos.X;
		Y = ynew + pos.Y;
		
		return this;
	}
	
	public Vector getLeftNormal() {
		return new Vector(Y, -X);
	}
	
	public Vector getRightNormal() {
		return new Vector(-Y, X);
	}
	
	public float dotProduct(Vector other) {
		return (X * other.X) + (Y * other.Y);
	}
	
	public float perpendicularProduct(Vector other) {
		return (Y * other.X) + (X * - other.Y);
	}
	
	public float crossProduct(Vector other) {
		return (X * other.Y) - (Y * other.X);
	}
	
	
	public Vector add(Vector other) {
		X += other.X;
		Y += other.Y;
		return this;
	}
	
	public Vector subtract(Vector other) {
		X -= other.X;
		Y -= other.Y;
		return this;
	}
	
	public Vector multiply(Vector other) {
		X *= other.X;
		Y *= other.Y;
		return this;
	}
	
	public Vector negate() {
		X = -X;
		Y = -Y;
		return this;
	}

	public Vector Clone() { return new Vector(X, Y); }

	@Override
	public String toString() {
		return "X: " + X + "\tY: " + Y;
	}
}