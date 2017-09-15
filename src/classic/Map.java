package classic;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Map extends Canvas implements Runnable {

	// A flag if repaint in progress (needed if our computation are to long)
	private boolean repaintInProgress = false;

	private int x, y, width, height;

	private long startTime;
	private long endTime;
	private long framePeriod = 10;

	private Game game;
	private Player player;

	protected Map(Game game, Player player, int x, int y, int width, int height) {

		this.game = game;
		this.player = player;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		// I ignore the auto-repaint, my programm will handle it
		setIgnoreRepaint(true);

		// Set the key listener for playing
		addKeyListener(game);

		// Focus on this canvas for listen to the keys
		setFocusable(true);
	}

	@Override
	public void run() {

		while (true) {

			while (game.isSplash_on()) {

				splashRepaint();
			}

			// repaints respecting the frame period chosen and the time it takes
			// to repaint every time
			while (game.isGame_on()) {

				startTime = System.currentTimeMillis();

				gameRepaint();

				try {
					endTime = System.currentTimeMillis();
					if (framePeriod - (endTime - startTime) > 0) {
						Thread.sleep(framePeriod - (endTime - startTime));
					}

				} catch (InterruptedException e) {
				}
			}

			while (!game.isSplash_on()) {

				gameOverRepaint();
			}
		}
	}

	// Repaints the splash screen, for showing asteroids moving
	private void splashRepaint() {

		// just in case
		if (repaintInProgress) {
			return;
		}

		repaintInProgress = true;

		Dimension size = getSize();

		// Painting on the hidden image (doble buffer)
		BufferStrategy strategy = getBufferStrategy();
		Graphics graphics = strategy.getDrawGraphics();

		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Drawing the background
		graphics2d.setColor(Color.BLACK);
		graphics2d.fillRect(0, 0, size.width, size.height);

		graphics2d.setColor(Color.WHITE);

		// Drawing the asteroids
		ArrayList<Asteroid> asteroids = game.getAsteroids();

		for (int i = 0; i < asteroids.size(); i++) {

			if (asteroids.get(i) != null) {
				asteroids.get(i).draw(graphics2d);
			}
		}

		Font currentFont = graphics2d.getFont();
		Font newFont = currentFont.deriveFont(currentFont.getSize() * 1F);
		graphics2d.setFont(newFont);

		graphics2d.drawString("'Press enter to play'", 220, 230);

		newFont = currentFont.deriveFont(currentFont.getSize() * 4F);
		graphics2d.setFont(newFont);

		graphics2d.drawString("ASTEROIDS", 130, 170);

		if (graphics2d != null)
			graphics2d.dispose();

		// Show next buffer
		strategy.show();

		Toolkit.getDefaultToolkit().sync();

		repaintInProgress = false;
	}

	// Repaints the game screen, with the mobiles and the scores
	private void gameRepaint() {

		if (repaintInProgress) {
			return;
		}

		repaintInProgress = true;

		Dimension size = getSize();

		BufferStrategy strategy = getBufferStrategy();
		Graphics graphics = strategy.getDrawGraphics();

		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		graphics2d.setColor(Color.BLACK);
		graphics2d.fillRect(0, 0, size.width, size.height);

		// Drawing the player's ship
		player = game.getPlayer();

		if (player.isAlive()) {
			player.draw(graphics2d);
		}

		// Drawing the player's shots
		ArrayList<Shot> shots = game.getShots();

		for (int i = 0; i < shots.size(); i++) {

			if (shots.get(i) != null) {
				shots.get(i).draw(graphics2d);
			}
		}

		// Drawing the enemy shots
		ArrayList<Shot> enemyShots = game.getEnemyShots();

		for (int i = 0; i < enemyShots.size(); i++) {

			if (enemyShots.get(i) != null) {
				enemyShots.get(i).draw(graphics2d);
			}
		}

		// Drawing the animations 
		ArrayList<Animation> animations = game.getAnimations();

		for (int i = 0; i < animations.size(); i++) {

			animations.get(i).draw(graphics2d);

		}

		// Drawing the asteroids
		ArrayList<Asteroid> asteroids = game.getAsteroids();

		for (int i = 0; i < asteroids.size(); i++) {

			if (asteroids.get(i) != null) {
				asteroids.get(i).draw(graphics2d);
			}
		}

		// Drawing the enemies
		ArrayList<Enemy> enemies = game.getEnemies();

		for (int i = 0; i < enemies.size(); i++) {

			if (enemies.get(i) != null) {
				enemies.get(i).draw(graphics2d);
			}
		}

		Font currentFont = graphics2d.getFont();
		Font newFont = currentFont.deriveFont(currentFont.getSize() * 2F);
		graphics2d.setFont(newFont);

		// Score and lives at the up-left corner
		graphics2d.drawString(Integer.toString(game.getScore()), 20, 30);

		int livesToDraw = game.getLives();
		int[] liveXPts;
		int[] liveYPts;

		int liveXPos = 25;
		int liveYPos = 45;

		for (int i = 1; i <= livesToDraw; i++) {

			liveXPts = new int[] { 0 + liveXPos, 4 + liveXPos, 3 + liveXPos, -3 + liveXPos, -4 + liveXPos };
			liveYPts = new int[] { -6 + liveYPos, 6 + liveYPos, 3 + liveYPos, 3 + liveYPos, 6 + liveYPos };

			liveXPos += 20;

			graphics2d.drawPolygon(liveXPts, liveYPts, 5);
		}

		if (graphics2d != null)
			graphics2d.dispose();

		// Show next buffer
		strategy.show();

		Toolkit.getDefaultToolkit().sync();

		repaintInProgress = false;
	}

	// Repaints the gameOver
	private void gameOverRepaint() {

		if (repaintInProgress) {
			return;
		}

		repaintInProgress = true;

		Dimension size = getSize();

		BufferStrategy strategy = getBufferStrategy();
		Graphics graphics = strategy.getDrawGraphics();

		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		graphics2d.setColor(Color.BLACK);
		graphics2d.fillRect(0, 0, size.width, size.height);

		graphics2d.setColor(Color.WHITE);

		ArrayList<Asteroid> asteroids = game.getAsteroids();

		for (int i = 0; i < asteroids.size(); i++) {

			if (asteroids.get(i) != null) {
				asteroids.get(i).draw(graphics2d);
			}
		}

		ArrayList<Animation> animations = game.getAnimations();

		for (int i = 0; i < animations.size(); i++) {

			animations.get(i).draw(graphics2d);

		}

		ArrayList<Enemy> enemies = game.getEnemies();

		for (int i = 0; i < enemies.size(); i++) {

			if (enemies.get(i) != null) {
				enemies.get(i).draw(graphics2d);
			}
		}

		Font currentFont = graphics2d.getFont();
		Font newFont = currentFont.deriveFont(currentFont.getSize() * 2F);
		graphics2d.setFont(newFont);

		graphics2d.drawString(Integer.toString(game.getScore()), 20, 30);

		newFont = currentFont.deriveFont(currentFont.getSize() * 4F);
		graphics2d.setFont(newFont);

		graphics2d.drawString("GAME OVER", 130, 170);

		if (graphics2d != null)
			graphics2d.dispose();

		strategy.show();

		Toolkit.getDefaultToolkit().sync();

		repaintInProgress = false;
	}
}
