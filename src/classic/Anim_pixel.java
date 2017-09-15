package classic;

import java.awt.Color;
import java.awt.Graphics;

public class Anim_pixel extends Animation {

	// Mobile pixels for dead animation
	public Anim_pixel(Game game, Color color, double x, double y, double angle, double pixelSpeed, int lifeLeft) {

		this.game = game;
		this.color = color;
		this.x = x;
		this.y = y;
		this.angle = angle;

		xSpeed = pixelSpeed * Math.cos(angle);
		ySpeed = pixelSpeed * Math.sin(angle);
		this.lifeLeft = lifeLeft;

		start();
	}

	@Override
	public void run() {

		while (alive) {

			if (!game.isPaused()) {
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
		lifeLeft--;
		x += xSpeed;
		y += ySpeed;
	}

	public synchronized void draw(Graphics g) {
		g.setColor(color);

		g.fillOval((int) (x - .5), (int) (y - .5), 1, 1);

	}
}
