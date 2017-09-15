package classic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 * Father of all the elements that move in the app
 * @author Gabriel
 *
 */
public abstract class Mobile extends Thread{
	
	protected Game game;

	protected double x, y, xSpeed, ySpeed;
	
	protected int screenWidth = 550;
	protected int screenHeight = 390;
	
	protected int lifeLeft;
	protected boolean alive = true;
	
	protected Game getGame() {
		return game;
	}
	
	protected double getX() {
		return x;
	}

	protected double getY() {
		return y;
	}

	protected double getxSpeed() {
		return xSpeed;
	}

	protected double getySpeed() {
		return ySpeed;
	}

	protected int getLifeLeft() {
		return lifeLeft;
	}

	protected void setX(double x) {
		this.x = x;
	}

	protected void setY(double y) {
		this.y = y;
	}

	protected void setxSpeed(double xSpeed) {
		this.xSpeed = xSpeed;
	}

	protected void setySpeed(double ySpeed) {
		this.ySpeed = ySpeed;
	}

	protected int getScreenWidth() {
		return screenWidth;
	}

	protected int getScreenHeight() {
		return screenHeight;
	}
	
	//Interface
	private synchronized void move(){
		
	}
	
	//Shared methods
	protected synchronized void die(){
		
		alive = false;
	}
	
	/**
	 * General dead animation: pixels floating for a while in space
	 * @param n_pixels
	 * @return
	 */
	protected ArrayList<Animation> deadAnim(int n_pixels){
			
		int random_angle = 0;
		
		ArrayList<Animation> pixels = new ArrayList<Animation>();
		
		for (int i = 0; i < n_pixels; i++) {
			
			random_angle = new Random().nextInt( (int) Math.PI * 2 );
			pixels.add( new Anim_pixel(game, Color.white, x, y, random_angle, .5, 35) );
		}
		
		return pixels;
	}
}
