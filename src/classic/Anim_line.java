package classic;

import java.awt.Color;
import java.awt.Graphics;

public class Anim_line extends Animation {

	private Player player;

	private double x1, y1, x2, y2;

	protected Player getPlayer() {
		return player;
	}

	// Player ship lines for dead animation
	public Anim_line(Game game, Color color, double x1, double y1, double x2, double y2, Player player,
			double pixelSpeed, int lifeLeft) {

		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.game = game;
		this.color = color;
		this.player = player;
		this.lifeLeft = lifeLeft;

		this.angle = calculateAngle((int) x1, (int) y1);
		xSpeed = pixelSpeed * Math.cos(angle);
		ySpeed = pixelSpeed * Math.sin(angle);

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
		x1 += xSpeed;
		y1 += ySpeed;
		x2 += xSpeed;
		y2 += ySpeed;
	}

	/**
	 * Calculates the angle between the center of the player ship and one of the
	 * points of its drawing
	 * 
	 * @param xPt
	 * @param yPt
	 * @return
	 */
	protected double calculateAngle(int xPt, int yPt) {

		double delta_x = player.getX() - xPt;
		double delta_y = yPt - player.getY();
		double theta_radians = Math.atan2(delta_y, delta_x);

		if (theta_radians < 0) {
			theta_radians = Math.abs(theta_radians);

		} else {
			theta_radians = 2 * Math.PI - theta_radians;
		}
		return theta_radians;
	}

	public synchronized void draw(Graphics g) {

		g.setColor(color);
		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}
}
