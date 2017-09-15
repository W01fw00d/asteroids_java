package classic;

import java.awt.*;

public class Shot extends Mobile{
	
	public Shot(Game game, String type, Color color, double x, double y, double angle, 
			double shotSpeed, double shipXVel, double shipYVel, int lifeLeft) {
		
		this.game = game;
		this.x = x;
		this.y = y;
		
		//Calculations of the shot direction and speed, depending on the angle of shot
		xSpeed = shotSpeed * Math.cos(angle) + shipXVel;
		ySpeed = shotSpeed * Math.sin(angle) + shipYVel;
		this.lifeLeft = lifeLeft;
		
		start();
	}
	
	@Override
	public void run() {
		
		while (alive){
			
			if (!game.isPaused()){
				move();
			}
				
			try {
		
				sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}

	private void move() {
		
		//When life left reaches 0, the shot disappears
		lifeLeft--; 
		
		x += xSpeed; 
		y += ySpeed;
		
		//The shot warps to the other side of the screen when it reaches its limit
		if (x < 0) 
			x += screenWidth; 
		else if (x > screenWidth)
			x -= screenWidth;
		if (y < 0)
			y += screenHeight;
		else if (y > screenHeight)
			y -= screenHeight;
	}

	public synchronized void draw(Graphics g) {
		g.setColor(Color.WHITE); 
		
		g.fillOval((int) (x - .5), (int) (y - .5), 2, 2); //3, 3
		
	}

}
