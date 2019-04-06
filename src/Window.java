import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Timer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;

import utils.FrameCounter;
import utils.Vector;

public class Window {

	private static FrameCounter framecounter = new FrameCounter();

	public static final int Width = 640, Height = 480, numparticles = 10000;

	private Vector3f Position = new Vector3f(0,0, -500);
	private final float zNear = 0.01f;
	private final float zFar = 1000f;
	private static long lastFrame;
	private boolean Running = true;

	private ArrayList<Nparticle> particles = new ArrayList<Nparticle>();
	
	public static int iterations = 0;

	public static int Delta = 16;
	private static long getTime() {
		return System.currentTimeMillis();
	}

	private static int getDelta() {
		long currentTime = getTime();
		int delta = (int) (currentTime - lastFrame);
		lastFrame = getTime();
		return delta;
	}

	public Window() {
		try {
			Display.setDisplayMode(new DisplayMode(Width, Height));
			Display.setTitle("N-Body simulation with OpenGL     fps: " + String.valueOf(framecounter.fps));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Width, 0, Height, zNear, zFar);
		glTranslatef(Position.x, Position.y, Position.z);
		glMatrixMode(GL_MODELVIEW);
		glEnableClientState(GL_VERTEX_ARRAY);

		createVBO();

		while (Running) {
			System.out.println(Delta);
			glLoadIdentity();
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			QuadTree tree = new QuadTree(0, 0, 640, 480, particles);
			QuadTree.DrawCenterofMass = false;
			
			int i=0;
			//glBegin(GL_POINTS);
			glColor3f(0,1,0);
			Vector mouse = new Vector(Mouse.getX(), Mouse.getY());
			for (Nparticle particle : particles) {
				Window.iterations++;
				//particle.Velocity.add(tree.calculateForce(particle));
				if (Mouse.isButtonDown(0)) {
					Vector lookat = particle.Position.lookAt(mouse);
					float d = particle.Position.distanceTo(mouse);
					lookat.scale((250/d));
					particle.Velocity.add(lookat);
				}
				particle.step();
				//particle.checkForWalls();
				positions.put(i, particle.Position.X);
				positions.put(i+1, particle.Position.Y);
				i+=3;
				//glVertex2f(particle.Position.X, particle.Position.Y);
			}
			//glEnd();
			
			glBufferData(GL_ARRAY_BUFFER, positions, GL_DYNAMIC_DRAW);
			glDrawArrays(GL_POINTS, 0, numparticles);
			
			glColor3f(0,1,0);
			tree.renderAll();

			if (Display.isCloseRequested()) {
				Running = false;
				Display.destroy();
				System.exit(0);
			}
			Display.update();
			Display.sync(60);
			framecounter.newframe();
			Display.setTitle("N-Body simulation with OpenGL     fps: " + String.valueOf(framecounter.fps) + ", " + iterations + " iterations");
			Delta = getDelta();
			iterations = 0;
		}
		cleanUpVBO();
		Display.destroy();
		System.exit(0);
	}

	int VBOID;
	FloatBuffer positions = BufferUtils.createFloatBuffer(numparticles*3);
	private void createVBO() {
		for (int i=0; i<numparticles; i++) {
			Nparticle newparticle = new Nparticle();
			particles.add(newparticle);
			positions.put(newparticle.Position.X).put(newparticle.Position.Y).put(0);
		}
		positions.flip();	

		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers(buffer);
		VBOID = buffer.get(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positions, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOID);
		glVertexPointer(3, GL_FLOAT, 0, 0);
	}

	public void cleanUpVBO() {
		
	}

	public static void main(String[] args) {
		Timer counter = new Timer();
		counter.schedule(framecounter, 0, 1000);
		new Window();
	}
}