package classic;

import java.awt.*;
import java.util.Random;

public class Asteroid extends Mobile{
	
	//Size can be 3 , 2 or 1
	private int size;

	private int numSplit;
	
	private final int numPoints = 12;
	
	//It stores de different random shapes that a asteroid can have
	private int[] [][] shapes = {
			
	//Shape0.1
	{
		{ 0, 1, 4, 5, 7, 10, 9, 8, 5, 3, 1, 0},
		{ 5, 3, 1, 0, 4, 5, 7, 9, 10, 7, 6, 5}
	},
	//Shape0.2
	{
		{ 5, 3, 1, 0, 4, 5, 7, 9, 10, 7, 6, 5},
		{ 0, 1, 4, 5, 7, 10, 9, 8, 5, 3, 1, 0}	
	},
	//Shape1.1
	{
		{ 0, 1, 2, 4, 7, 10, 9, 8, 5, 3, 1, 0},
		{ 5, 6, 10, 10, 9, 7, 5, 4, 4, 1, 2, 5}
	},
	
	//Shape1.2
	{
		{ 5, 6, 10, 10, 9, 7, 5, 4, 4, 1, 2, 5},
		{ 0, 1, 2, 4, 7, 10, 9, 8, 5, 3, 1, 0}	
	},
	//Shape2.1
	{
		{ 0, 1, 3, 5, 8, 9, 8, 7, 4, 2, 1, 0},
		{ 5, 2, 1, 2, 4, 5, 7, 9, 10, 7, 6, 5}
	},
	//Shape2.2
	{
		{ 5, 2, 1, 2, 4, 5, 7, 9, 10, 7, 6, 5},
		{ 0, 1, 3, 5, 8, 9, 8, 7, 4, 2, 1, 0}	
	},
	//Shape3.1
	{
		{ 3, 5, 6, 7, 8, 9, 10, 8, 5, 3, 1, 3},
		{ 0, 2, 1, 4, 4, 5, 7, 9, 10, 7, 6, 0}
	},
	//Shape3.2
	{
		{ 0, 2, 1, 4, 4, 5, 7, 9, 10, 7, 6, 0},
		{ 3, 5, 6, 7, 8, 9, 10, 8, 5, 3, 1, 3}
	}	
	};
	
	//The drawing points, relatives to the center of the ship
	private int[] origXPts, origYPts;
	
	//The normal drawing points
	private int[] xPts = new int[numPoints]; 
	private int[] yPts = new int[numPoints];

	protected int getNumSplit() {
		return numSplit;
	}

	protected int getSize() {
		return size;
	}

	protected int getNumPoints() {
		return numPoints;
	}

	protected int[] getXPts() {
		return xPts;
	}

	protected int[] getYPts() {
		return yPts;
	}

	public Asteroid(Game game, double x, double y, int size, 
			double minSpeed, double maxSpeed, int lifeLeft, int numSplit) {
		
		this.game = game;
		this.x = x;
		this.y = y;
		this.size = size;
		this.lifeLeft = lifeLeft; 
		this.numSplit = numSplit; 
		
		//On creation, the asteroid speed is randomized
		double speed = minSpeed + Math.random() * (maxSpeed - minSpeed), dir = 2 * Math.PI * Math.random(); 
																													
		xSpeed = speed * Math.cos(dir);
		ySpeed = speed * Math.sin(dir);
		
		//Random shape
		int random = new Random().nextInt(shapes.length);
		origXPts = shapes[random][0];
		origYPts = shapes[random][1];
		
		//Resize of the drawing points
		for (int i = 0; i < numPoints; i++) {
			origXPts[i] *= size;
			origYPts[i] *= size;
		}
		
		start();
	}
	
	@Override
	public void run() {
		
		while (alive){
		
			if (!getGame().isPaused()){
				move();
			}
			
			try {

				sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates a new asteroid with half of the parent asteroid size
	 * @param minSpeed
	 * @param maxSpeed
	 * @return
	 */
	protected Asteroid createSplitAsteroid(double minSpeed, double maxSpeed) {
		
		return new Asteroid(game, x, y, getSize() / 2, minSpeed, maxSpeed, lifeLeft - 1, numSplit);
	}

	private void move() {
		x += xSpeed; 
		y += ySpeed;
		
		//warp at the screen limits
		if (x < 0 - (size * 8)){
			x += screenWidth + (size * 4);
		}else if (x > screenWidth + (size * 4)){
			x = - (size * 8);
		}if (y < 0 - (size * 8)){
			y += screenHeight + (size * 4);
		}else if (y > screenHeight + (size * 4)){
			y = - (size * 8);
		}
		
	}

	public synchronized void draw(Graphics g) {
		
		g.setColor(Color.white);
		
		for (int i = 0; i < numPoints; i++) {
			
			xPts[i] = (int) (getX() + origXPts[i]);
			yPts[i] = (int) (getY() + origYPts[i]);
		}
		
		g.drawPolygon(xPts, yPts, numPoints);
	}
}
