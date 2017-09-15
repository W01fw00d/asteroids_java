package classic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Enemy extends Ship{
	
	//Size can be 1 or 2
	private int size;
	
	private final int numPoints = 12;
	
	private int[][] shape = {
			{ 0, 4, 11, 15, 0, 4, 11, 15, 11, 10, 5, 4 },
			{ 7, 10, 10, 7, 7, 4, 4, 7, 4, 1, 1, 4 }
	};
	
	int cooldown = 0;
	
	Player player;

	protected int getSize() {
		return size;
	}

	protected int getNumPoints() {
		return numPoints;
	}

	public Enemy(Game game, double x, double y, int size, int shotDelay, Player player) {
		
		this.game = game;
		this.x = x;
		this.y = y;
		this.size = size;
		this.shotDelay = shotDelay;
		this.player = player;
		
		xPts = new int[numPoints];
		yPts = new int[numPoints];
		
		//Resize of the drawing points
		for (int i = 0; i < numPoints; i++) {
			
			shape[0][i] *= size;
			shape[1][i] *= size;
		}
		
		xSpeed = 1;
		ySpeed = 0;
		
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

	private void move() {
		
		//The shot delay reduces with every move
		if (getShotDelayLeft() > 0){ 
			setShotDelayLeft(getShotDelayLeft() - 1); 
		}
		
		x += xSpeed; 
		
		int diagonal_move = new Random().nextInt(3);
		
		if ( (diagonal_move == 0) && (ySpeed == 0) && (cooldown <= 0) ){
			ySpeed = 1;
			cooldown = 100;
			
		}else if ( (diagonal_move == 1) && (ySpeed == 0) && (cooldown <= 0) ){
			ySpeed = -1;
			cooldown = 100;
			
		}else if ( (diagonal_move == 2) && (cooldown <= 0) ){
			ySpeed = 0;
			cooldown = 100;
		}
		
		y += ySpeed;
		cooldown--;
		
		//disapear at screen horizontal limits
		if ( (x < 0 - (size * 2)) || (x > screenWidth + (size * 1)) ){
			die();
		}
		
		if (y < 0){
			y += screenHeight;
		}else if (y > screenHeight){
			y = 0;
		}
	}
	
	/**
	 * Depending on the size of the enemy ship, shoots randomly or aims to the player
	 * @return
	 */
	protected Shot aim(){
		
		Shot aux_shot = null;
		
		setShotDelayLeft(shotDelay);
		
		//Small ship: Accurate shot
		if (size == 1){
			aux_shot = accurateShot();
		
		//Large ship: Random shot
		}else if (size == 2){
			angle = new Random().nextInt( (int) Math.PI * 2 );
			aux_shot = shoot();
		}

		return aux_shot;
		
	}
	
	/**
	 * Shoots to the player ship, calculating the angle between the enemy position and the player position
	 */
	protected Shot accurateShot(){
		
		//Set delay to control the fire rate
		shotDelayLeft=shotDelay; 
		
		double delta_x = player.getX() - x;
		double delta_y = y - player.getY();
		double theta_radians = Math.atan2(delta_y, delta_x);
		
		if (theta_radians < 0){
			theta_radians = Math.abs(theta_radians);
		 
		}else{
		   theta_radians = 2*Math.PI - theta_radians;
		}
		
		return new Shot(game, "normal", Color.white, xPts[0], yPts[0], theta_radians, 5, xSpeed, ySpeed, 50);
	}
	
	/**
	 * Normal shot
	 * @return
	 */
	protected Shot shoot(){
		
		//Set delay to control the fire rate
		shotDelayLeft=shotDelay;
		
		return new Shot(game, "normal", Color.white, xPts[0], yPts[0], angle, 5, xSpeed, ySpeed, 50);
	}	

	public synchronized void draw(Graphics g) {
		
		g.setColor(Color.white); 
		
		for (int i = 0; i < numPoints; i++) {
			
			xPts[i] = (int) (getX() + shape[0][i]);
			yPts[i] = (int) (getY() + shape[1][i]);
		}
		
		g.drawPolygon(getXPts(), getYPts(), numPoints);
	}

}
