import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

import utils.Vector;


public class QuadTree {
	private static final Vector minsize = new Vector(5,5);
	private static final float theta = 10f;
	
	public Vector Position, Size, CenterofMass;
	private ArrayList<Nparticle> Contains = new ArrayList<Nparticle>();
	public float Mass = 0;
	private QuadTree[] Children;
	boolean ContainsAny = false;
	
	public static boolean DrawOutline = true, DrawEmptyCells = true, DrawCenterofMass = false;
	
	public QuadTree(float x, float y, float width, float height, ArrayList<Nparticle> possibleparticles) {
		Position = new Vector(x, y);
		Size = new Vector(width, height);
		for (Nparticle particle : possibleparticles) {
			if (containsParticle(particle.Position.X, particle.Position.Y)) Contains.add(particle);
			Window.iterations++;
		}
		
		if (Contains.size() == 1) {
			CenterofMass = new Vector(0,0);
			CenterofMass = Contains.get(0).Position;
			Mass = 1;
			ContainsAny = true;
			Window.iterations++;
		} else if (Contains.size() == 0) {
			
		} else {
			ContainsAny = true;
			CenterofMass = new Vector(0,0);
			if (Size.X > minsize.X && Size.Y > minsize.Y) {
				Children = new QuadTree[4];
				Vector halfsize = Size.Clone().scale(0.5f);
				Children[0] = new QuadTree(Position.X, 				Position.Y, 			halfsize.X, halfsize.Y, Contains);
				Children[1] = new QuadTree(Position.X+halfsize.X,	Position.Y, 			halfsize.X, halfsize.Y, Contains);
				Children[2] = new QuadTree(Position.X, 				Position.Y+halfsize.Y, 	halfsize.X, halfsize.Y, Contains);
				Children[3] = new QuadTree(Position.X+halfsize.X,	Position.Y+halfsize.Y, 	halfsize.X, halfsize.Y, Contains);
				int childscontains = 0;
				if (Children[0].ContainsAny) {CenterofMass.add(Children[0].CenterofMass); Mass+=Children[0].Mass; childscontains++;}
				if (Children[1].ContainsAny) {CenterofMass.add(Children[1].CenterofMass); Mass+=Children[0].Mass; childscontains++;}
				if (Children[2].ContainsAny) {CenterofMass.add(Children[2].CenterofMass); Mass+=Children[0].Mass; childscontains++;}
				if (Children[3].ContainsAny) {CenterofMass.add(Children[3].CenterofMass); Mass+=Children[0].Mass; childscontains++;}
				CenterofMass.scale(1f/childscontains);
			} else {
				for (Nparticle particle : Contains) {
					CenterofMass.add(particle.Position);
					Mass++;
					Window.iterations++;
				}
				CenterofMass.scale(1f/Contains.size());
			}
		}
	}
	
	public Vector calculateForce(Nparticle particle) {
		Vector force = new Vector(0,0);
		if (!ContainsAny) return force;
		if (Size.X < minsize.X || Size.Y < minsize.Y) return force; 
		if (Contains.size() == 1) {
			force = particle.Position.lookAt(CenterofMass);
			float d = force.getMagnitude();
			force.scale((500f/d)/500f);
			Window.iterations++;
		} else {
			float 	r = particle.Position.distanceTo(CenterofMass)/500,
					d = Size.Y;
			if (d/r < theta) {
				force = particle.Position.lookAt(CenterofMass);
				//force.scale(500f-r);
				//force.scale(Mass);
				Window.iterations++;
			} else {
				byte affected = 0;
				if (Children[0].ContainsAny) {force.add(Children[0].calculateForce(particle)); affected++;}
				if (Children[1].ContainsAny) {force.add(Children[1].calculateForce(particle)); affected++;}
				if (Children[2].ContainsAny) {force.add(Children[2].calculateForce(particle)); affected++;}
				if (Children[3].ContainsAny) {force.add(Children[3].calculateForce(particle)); affected++;}
				force.scale(1f/affected);
			}
		}
		return force;
	}

	private boolean containsParticle(float x, float y) {
		if (x < Position.X) return false; 
		if (x > Position.X+Size.X) return false;
		if (y < Position.Y) return false; 
		if (y > Position.Y+Size.Y) return false;
		return true;
	}
	
	public void render() {
		glColor3f(0, 1, 0);
		if (DrawOutline) {
			if ((!DrawEmptyCells && Contains.size() == 0) || DrawEmptyCells) {  
				glPushMatrix();
					glBegin(GL_LINE_LOOP);
						glVertex2f(Position.X, Position.Y);
						glVertex2f(Position.X+Size.X, Position.Y);
						glVertex2f(Position.X+Size.X, Position.Y+Size.Y);
						glVertex2f(Position.X, Position.Y+Size.Y);
					glEnd();
				glPopMatrix();
			}
		}
		if (DrawCenterofMass) {
			if (CenterofMass == null)
				return;
			glColor3f(1, 0, 0);
			glPushMatrix();
				glBegin(GL_LINE_LOOP);
					glVertex2f(CenterofMass.X-5, CenterofMass.Y-5);
					glVertex2f(CenterofMass.X+5, CenterofMass.Y-5);
					glVertex2f(CenterofMass.X+5, CenterofMass.Y+5);
					glVertex2f(CenterofMass.X-5, CenterofMass.Y+5);
				glEnd();
			glPopMatrix();
			glColor3f(1, 1, 1);
		}
	}
	
	public void renderAll() {
		render();
		if (Contains.size() <= 1) return;
		if (Size.X < minsize.X) return;
		if (Size.Y < minsize.Y) return;
		Children[0].renderAll();
		Children[1].renderAll();
		Children[2].renderAll();
		Children[3].renderAll();
	}
}