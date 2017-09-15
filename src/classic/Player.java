package classic;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Player extends Ship{

	private final double[] 
			origXPts = { 12, -12, -7, -7, -12 }, 
			origYPts = { 0, -8, -6, 6, 8 }, 
			origFlameXPts = { -7, -15, -7 },
			origFlameYPts = { -3, 0, 3 };
	
	protected final int numPoints = 5;
	 
	private double acceleration, SpeedDecay, rotationalSpeed;
	
	private boolean turningLeft, turningRight, accelerating, active;
	
	// Points of flame drawing
	private int[] flameXPts, flameYPts; 
	
	// A counter for making the flame flicker
	private int flameCount = 10;

	protected void setAccelerating(boolean accelerating) {
		this.accelerating = accelerating; 
	}

	protected void setTurningLeft(boolean turningLeft) {
		this.turningLeft = turningLeft;
	}

	protected void setTurningRight(boolean turningRight) {
		this.turningRight = turningRight;
	}

	// Used when the game is paused or unpaused
	protected void setActive(boolean active) {
		this.active = active; 
	}

	protected boolean isActive() {
		return active;
	}
	
	protected boolean isAccelerating() {
		return accelerating;
	}

	protected int getFlameCount() {
		return flameCount;
	}

	protected void setFlameCount(int flameCount) {
		this.flameCount = flameCount;
	}

	protected int getNumPoints() {
		return numPoints;
	}

	public Player(Game game, double x, double y, double angle, double acceleration, 
			double SpeedDecay, double rotationalSpeed, int shotDelay) {
		
		//For Player special behaviour, it's created dead. The Thread is started by Game.
		alive = false;
		this.game = game;
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.acceleration = acceleration;
		this.SpeedDecay = SpeedDecay;
		this.rotationalSpeed = rotationalSpeed;
		this.shotDelay = shotDelay; 
		
		xSpeed = 0; 
		ySpeed = 0;
		turningLeft = false; 
		turningRight = false;
		accelerating = false; 
		active = false; 
		
		xPts = new int[numPoints]; 
		yPts = new int[numPoints];
		flameXPts = new int[3];
		flameYPts = new int[3];
		
		shotDelayLeft = 0; 
	}

	@Override
	public void run() {
		
		alive = true;
		
		while (alive){
			
			if (!getGame().isPaused()){
				move();
			}
			
			try {
				sleep(25);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void move() {
		
		//When shotDelayLeft reaches 0, it can shoot again
		if (getShotDelayLeft() > 0){ 
			setShotDelayLeft(getShotDelayLeft() - 1); 
		}
		
		if (turningLeft){ 
			setAngle(getAngle() - rotationalSpeed); 
		}
		
		if (turningRight){ 
			setAngle(getAngle() + rotationalSpeed); 
		}
		
		// Keep angle within bounds of 0 to 2*PI
		if (getAngle() > (2 * Math.PI)){ 
			setAngle(getAngle() - (2 * Math.PI));
			
		}else if (getAngle() < 0){
			setAngle(getAngle() + (2 * Math.PI));
		}
		
		if (accelerating) { // 
			xSpeed += acceleration * Math.cos(angle);
			ySpeed += acceleration * Math.sin(angle);
		}
		
		x += xSpeed; 
		y += ySpeed;
		// Slows ship down when is not accelerating (speedDecay should be a decimal between 0 and 1)
		xSpeed *= SpeedDecay; 
		ySpeed *= SpeedDecay; 
		
		//Teleports the player at the screen limits
		if (x < 0 - 6){ 
			x += screenWidth + 6; 
		}else if (x > screenWidth + 6){
			x = -6;
		}

		if (y < 0 - 6){
			y += screenHeight; 
		}else if (y > screenHeight + 6){
			y = 0;
		}
	}
	
	/**
	 * Special feature: enter the hyperspace. It warps the player ship to a random location.
	 */
	protected void hyper(){
		
		x = new Random().nextInt(getScreenWidth());
		y = new Random().nextInt(getScreenHeight());
		angle = new Random().nextInt((int) Math.PI * 2);
	}
	
	

	public synchronized void draw(Graphics g) {
		
		// Draw flame if accelerating only in some occasions, to make the effect of a moving flame
		if ( accelerating && active && (flameCount <= 0) ) { 
			
			for (int i = 0; i < 3; i++) {
				flameXPts[i] = (int) (origFlameXPts[i] * Math.cos(angle) - origFlameYPts[i] * Math.sin(angle) + x + .5);
				flameYPts[i] = (int) (origFlameXPts[i] * Math.sin(angle) + origFlameYPts[i] * Math.cos(angle) + y + .5);
			}
			
			g.setColor(Color.white); 
			g.drawPolygon(flameXPts, flameYPts, 3); 
			
			flameCount = 2;
			
		}else{
			
			flameCount = flameCount - 1;
		}
		
		// Calculate the polygon for the ship, then draw it
		for (int i = 0; i < numPoints; i++) {
			
			xPts[i] = (int) (origXPts[i] * Math.cos(angle) - 
					origYPts[i] * Math.sin(angle) + x + .5); 
			
			yPts[i] = (int) (origXPts[i] * Math.sin(angle) + 
					origYPts[i] * Math.cos(angle) + y + .5); 
		}
		
		g.setColor(Color.white);
		g.drawPolygon(xPts, yPts, numPoints); 
	}
	
	/**
	 * Normal shot
	 * @return
	 */
	protected Shot shoot(){
		
		shotDelayLeft=shotDelay; 
		
		return new Shot(game, "normal", Color.white, xPts[0], yPts[0], angle, 5, xSpeed, ySpeed, 50); 
	}	
	
	/**
	 * A shot that delays its movement a bit and travels farer
	 * @return
	 */
	protected Shot missileShot(){
		
		shotDelayLeft=shotDelay;
		
		return new Shot(game, "normal", Color.red, xPts[0], yPts[0], angle, 5, xSpeed, ySpeed, 100); 
	}
	
	/**
	 * A shot that doesn't move but explodes making other shots
	 * @return
	 */
	protected Shot fireworkShot(){
		
		shotDelayLeft=shotDelay;
		
		return new Shot(game, "red", Color.red, xPts[0], yPts[0], angle + Math.PI, 5, xSpeed, ySpeed, 50);
	}
	
	/**
	 * Special animation for the player, adding three lines from its drawing that floate in space
	 */
	protected ArrayList<Animation> deadAnim(int n_pixels){
				
		int random_angle = 0;
			
		ArrayList<Animation> pixels = new ArrayList<Animation>();
			
		for (int i = 0; i < n_pixels; i++) {
				
			random_angle = new Random().nextInt( (int) Math.PI * 2 );
			pixels.add( new Anim_pixel(game, Color.white, x, y, random_angle, .5, 35) ); 
		}
		
		//Lines that floates and spread after the ship explodes
		pixels.add( new Anim_line(game, Color.white, xPts[0], yPts[0], xPts[1], yPts[1], this, .5, 35) );
		pixels.add( new Anim_line(game, Color.white, xPts[2], yPts[2], xPts[3], yPts[3], this, .5, 35) );
		pixels.add( new Anim_line(game, Color.white, xPts[4], yPts[4], xPts[0], yPts[0], this, .5, 35) );
		
		return pixels;
	}
}
