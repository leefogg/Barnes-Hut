import utils.Vector;

public class Nparticle {
	public Vector Velocity = new Vector(0,0);
	public Vector Position;
	
	public Nparticle() {
		Position = new Vector((float)Math.random()*Window.Width, (float)Math.random()*Window.Height);
	}
	public Nparticle(float x, float y) {Position = new Vector(x,y);}
	
	public void checkForWalls() {
		if (Position.X >= Window.Width)  	{Velocity.X = (short)-Velocity.X; Position.X = Window.Width-1;}
		if (Position.X <= 0)  				{Velocity.X = (short)-Velocity.X; Position.X = 1;}
		if (Position.Y >= Window.Height) 	{Velocity.Y = (short)-Velocity.Y; Position.Y = Window.Height-1;}
		if (Position.Y <= 0)  				{Velocity.Y = (short)-Velocity.Y; Position.Y = 1;}
	}
	
	
	public void step() {
		Position.add(Velocity);
		Velocity.scale(0.99f);
	}
	
	public void push(Vector p) {		
		float side1 = Position.X - p.X;
		float side2 = Position.Y - p.Y;
		double dist = Window.Width-Math.sqrt((side1*side1)+(side2*side2));
		dist *= 0.0005*(Window.Delta>>4);
		Vector dir = p.Normalize();
		
		Velocity.X += dir.X*dist;
		Velocity.Y += dir.Y*dist;
	}
}
