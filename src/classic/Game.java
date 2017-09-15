package classic;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class Game implements KeyListener, Runnable {

	private boolean splash_on = true;
	private boolean game_on = false;
	
	private boolean paused;

	private int x, y, width, height;

	// Is the player shooting?
	private boolean shooting;
	// Number of shots that can be at the map at the same time
	int shotsLimit;

	private ArrayList<Shot> shots = new ArrayList<Shot>();
	
	private ArrayList<Shot> enemyShots = new ArrayList<Shot>();
	
	//Animations for collisions
	private ArrayList<Animation> animations = new ArrayList<Animation>();

	private ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();

	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();

	// The min and max speed that the asteroids will have
	double astMinSpd, astMaxSpd;
	int astNumHits, astNumSplit;

	// The current level number. It determines the number of asteroids. 
	// It isn't directly showed on screen
	private int level;

	// Score of the player's game session
	private int score;
	private int scoreForLife;
	private int lives;

	private Player player;
	private Window window;

	protected int getLevel() {
		return level;
	}

	protected boolean isPaused() {
		return paused;
	}

	protected boolean isGame_on() {
		return game_on;
	}
	
	protected boolean isSplash_on() {
		return splash_on;
	}

	protected boolean isShooting() {
		return shooting;
	}

	protected Player getPlayer() {
		return player;
	}
	
	protected ArrayList<Shot> getShots() {
		return shots;
	}
	
	protected ArrayList<Shot> getEnemyShots() {
		return enemyShots;
	}
	
	protected ArrayList<Animation> getAnimations() {
		return animations;
	}

	protected ArrayList<Asteroid> getAsteroids() {
		return asteroids;
	}

	protected ArrayList<Enemy> getEnemies() {
		return enemies;
	}

	protected int getScore() {
		return score;
	}

	protected int getLives() {
		return lives;
	}

	public Game() {

		astMinSpd = .5;
		astMaxSpd = 1.5; // 5
		astNumHits = 3;
		astNumSplit = 2;

		// Limit of shots per screen
		shotsLimit = 4; 
		
		player_respawn();
		
		x = 100;
		y = 100;
		width = 550;
		height = 390;

		//Create the window of the graphical interface
		window = new Window(this, player, x, y, width, height);

		// player starts without shooting
		shooting = false;

		new Thread(this).start();
	}

	@Override
	public void run() {

		while (true) {

			splashScreen();

			// level will be incremented to 1 when first level is set up. We
			// start with 4 asteroids.
			level = 3;
			score = 0;
			
			//Score needed for getting an extra life
			scoreForLife = 10000;
			
			// Player starts with 3 lives
			lives = 3;

			player_respawn();
			
			//Game background sound
			new Sound (800, this);
			
			while (game_on) {
				
				if ( asteroids.isEmpty() && enemies.isEmpty() ) {
					setUpNextLevel();
				}

				if (!paused) {

					manageEnemies();

					// Removes shots when their LifeLeft is 0
					for (int i = 0; i < shots.size(); i++) {

						if (shots.get(i).getLifeLeft() == 0) {

							shots.get(i).die();
							shots.remove(i);
						}
					}
					
					// Removes enemy shots when their LifeLeft is 0
					for (int i = 0; i < enemyShots.size(); i++) {

						if (enemyShots.get(i).getLifeLeft() == 0) {
							enemyShots.get(i).die();
							
							enemyShots.remove(i);
						}
					}
					
					// Removes animations when its LifeLeft is over
					for (int i = 0; i < animations.size(); i++) {

						if (animations.get(i).getLifeLeft() == 0) {

							animations.get(i).die();
							animations.remove(i);

						}
					}

					checkAsteroidsCollisions();
					
					checkShotsCollisions();
					
					checkPtsAndLives();

					if (shooting && player.canShoot() && (shots.size() < shotsLimit)) {

						// add a shot on to the arrayList if the ship is shooting
						shots.add(player.shoot());
						
						//Shot sound
						new Sound("fire");
					}
				}

				try {
					Thread.sleep(1);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			gameOverScreen();
		}
	}

	/**
	 * Title screen. Press enter to play.
	 */
	private synchronized void splashScreen() {

		for (int i = 0; i < asteroids.size(); i++) {
			asteroids.get(i).die();
		}
		asteroids.removeAll(asteroids);
		
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).die();
		}
		enemies.removeAll(enemies);
		
		for (int i = 0; i < animations.size(); i++) {
			animations.get(i).die();
		}
		animations.removeAll(animations);
		
		try {
			
			for (int i = 0; i < 4; i++) {
				asteroids.add(new Asteroid(this, Math.random() * width, Math.random() * height, 8, astMinSpd, astMaxSpd,
						astNumHits, astNumSplit));
			}
			
			wait();
			
			for (int i = 0; i < asteroids.size(); i++) {
				asteroids.get(i).die();
			}
			asteroids.removeAll(asteroids);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * End of the game screen. Press enter to go to the title screen.
	 */
	private synchronized void gameOverScreen(){
		
		try {
			
			wait();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setUpNextLevel() {
		
		// Starts a new level with one more asteroid
		level++;

		// No shots on the screen at beginning of level
		for (int i = 0; i < shots.size(); i++) {
			shots.get(i).die();
		}
		shots.removeAll(shots);
		
		// No enemy shots on the screen at beginning of level
		for (int i = 0; i < enemyShots.size(); i++) {
			enemyShots.get(i).die();
		}
		enemyShots.removeAll(enemyShots);
		
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).die();
		}
		enemies.removeAll(enemies);
		
		paused = false;
		shooting = false;

		
		Asteroid new_ast = null;
		boolean correct = false;
		
		// Create asteroids in random spots on the screen, avoiding the center of it 
		// and checking that the player isn't there
		
		for (int i = 0; i < level; i++) {
			
			while (!correct){
				
				double ast_x = Math.random() * width;
				double ast_y = Math.random() * height;
				
				if (
					(ast_x < (width / 3) || ast_x > (width * 2 / 3))
					&& (ast_y < (height / 3) || ast_y > (height * 2 / 3))
					){
					
					correct = true;
				}
				
				if (correct){
					new_ast = new Asteroid(
							this, ast_x, ast_y, 8, astMinSpd, astMaxSpd, astNumHits, astNumSplit);
					
					correct = !playerAsteroidCollision(player, new_ast);
					
					if (!correct){
						
						new_ast.die();
					}
				}

			}
			
			asteroids.add(new_ast);
			correct = false;
		}
	}
	
	/**
	 * Manage the arraylist of enemy ships (we use an arraylist in case we want
	 * to have more than 1 ship at the same time, for classic mode the arraylist
	 * isn't really necessary)
	 */
	private void manageEnemies() {

		int appearChance;

		int enemies_size;

		// If there's not enemy ship at screen, there's a chance of one
		// appearing, increasing with less asteroids
		if (enemies.isEmpty()) {

			//Chance of 7777. Gets higher with higher levels
			appearChance = new Random().nextInt(asteroids.size() * 7777);

			if (appearChance < level / 2) { 

				int size = 0;
				
				if (score >= 10000){
					size = 1;
				}else{
					size = 2;
				}
				
				enemies.add(new Enemy(this, 0, Math.random() * height, size, 150, player));
				
				//Enemy ship constant sound
				new Sound(size, 155, enemies.get(0));
			}

		} else {

			enemies_size = enemies.size();

			for (int i = 0; i < enemies_size; i++) {

				//Make the enemies shoot
				if (enemies.get(i).canShoot()){
					
					enemyShots.add( enemies.get(i).aim() );
				}
				
				// Remove the dead enemies from the arraylist
				if (!enemies.get(i).isAlive()) {

					enemies.remove(i);
				}
			}
		}
	}

	/**
	 * Checks player-asteroid and shot-asteroid collisions
	 */
	private void checkAsteroidsCollisions() {

		//If a asteroid is shot, break the loop
		boolean dead_asteroid = false;
		
		for (int i = 0; i < asteroids.size(); i++) {

			if (playerAsteroidCollision(player, asteroids.get(i))) {

				score += ptsAsteroid(asteroids.get(i));

				new Sound(asteroids.get(i).getSize());

				// If the asteroid can be split, creates two new ones
				if (asteroids.get(i).getLifeLeft() > 1) {
					for (int k = 0; k < asteroids.get(i).getNumSplit(); k++) {
						asteroids.add(asteroids.get(i).createSplitAsteroid(astMinSpd, astMaxSpd));
					}
				}
				
				animations.addAll( asteroids.get(i).deadAnim(5) );
				asteroids.get(i).die();
				asteroids.remove(i);

				animations.addAll( player.deadAnim(5) );
				player.die();

				if (lives > 1) {
					
					player_respawn();
					lives--;
				} else {

					gameOver();
				}
				break;
			}

			if ((!enemies.isEmpty()) && (enemyshipAsteroidCollision(enemies.get(0), asteroids.get(i)))) {

				new Sound(asteroids.get(i).getSize());
				
				// If the asteroid can be split, creates two new ones
				if (asteroids.get(i).getLifeLeft() > 1) {
					for (int k = 0; k < asteroids.get(i).getNumSplit(); k++) {
						asteroids.add(asteroids.get(i).createSplitAsteroid(astMinSpd, astMaxSpd));
					}
				}
				animations.addAll( asteroids.get(i).deadAnim(5) );
				asteroids.get(i).die();
				asteroids.remove(i);

				animations.addAll( enemies.get(0).deadAnim(5) );
				enemies.get(0).die();
				enemies.remove(0);
				break;
			}

			for (int j = 0; j < shots.size(); j++) {

				if ((asteroids.size() > 0) && shotAsteroidCollision(shots.get(j), asteroids.get(i))) {
					shots.get(j).die();
					shots.remove(j);

					score += ptsAsteroid(asteroids.get(i));

					new Sound(asteroids.get(i).getSize());
					
					// If the asteroid can be split, creates two new ones
					if (asteroids.get(i).getLifeLeft() > 1) {

						for (int k = 0; k < asteroids.get(i).getNumSplit(); k++) {
							asteroids.add(asteroids.get(i).createSplitAsteroid(astMinSpd, astMaxSpd));
						}
					}

					animations.addAll( asteroids.get(i).deadAnim(5) );
					asteroids.get(i).die();
					asteroids.remove(i);

					dead_asteroid = true;
					
					// Break to avoid bugs if a lot of asteroids are destroyed
					// at the same time
					break;
				}
			}
			
			//If the current asteroid doesn't exists anymore, we can stop the collision checking
			if (dead_asteroid){
				break;
			}
			
			for (int j = 0; j < enemyShots.size(); j++) {

				if ((asteroids.size() > 0) && (enemyShots.size() > 0) 
						&& shotAsteroidCollision(enemyShots.get(j), asteroids.get(i))) {
					
					enemyShots.get(j).die();
					enemyShots.remove(j);

					new Sound(asteroids.get(i).getSize());
					
					// If the asteroid can be split, creates two new ones
					if (asteroids.get(i).getLifeLeft() > 1) {

						for (int k = 0; k < asteroids.get(i).getNumSplit(); k++) {
							asteroids.add(asteroids.get(i).createSplitAsteroid(astMinSpd, astMaxSpd));
						}
					}

					animations.addAll( asteroids.get(i).deadAnim(5) );
					asteroids.get(i).die();
					asteroids.remove(i);

					// Break to avoid bugs if a lot of asteroids are destroyed
					// at the same time
					break;
				}
			}
		}
	}

	/**
	 * Checks player-asteroid and shot-asteroid collisions
	 */
	private void checkShotsCollisions() {

		//Only check it there's any enemy ship
		if (!enemies.isEmpty()){
			
			if ( playerEnemyshipCollision(player, enemies.get(0)) ){
				
				new Sound("bangLarge");
				
				score += ptsEnemyship(enemies.get(0));

				animations.addAll( enemies.get(0).deadAnim(5) );
				enemies.get(0).die();
				enemies.remove(0);
				
				animations.addAll( player.deadAnim(5) );
				player.die();

				if (lives > 1) {
					
					player_respawn();
					lives--;
				} else {

					gameOver();
				}
			}
			
			for (int i = 0; i < shots.size(); i++) {

				if ( !enemies.isEmpty() && (shotEnemyshipCollision(shots.get(i), enemies.get(0))) ) {

					score += ptsEnemyship(enemies.get(0));
					
					new Sound("bangLarge");

					animations.addAll( enemies.get(0).deadAnim(5) );
					enemies.get(0).die();
					enemies.remove(0);
					
					shots.get(i).die();
					shots.remove(i);

					break;
				}
			}
			
			for (int i = 0; i < enemyShots.size(); i++) {

				if (!enemyShots.isEmpty() && playerEnemyshotCollision(player, enemyShots.get(i)) ){
					
					new Sound("bangLarge");

					enemyShots.get(i).die();
					enemyShots.remove(i);
					
					animations.addAll( player.deadAnim(5) );
					player.die();
					
					if (lives > 1) {
						
						player_respawn();
						lives--;
					} else {

						gameOver();
					}

					break;
				}		
			}
		}
	}

	// Collision checkers. They use normal intersects() and a custom one
	
	private boolean playerAsteroidCollision(Player player, Asteroid asteroid) {

		if (customIntersects(new Polygon(player.getXPts(), player.getYPts(), player.getNumPoints()),
				new Polygon(asteroid.getXPts(), asteroid.getYPts(), asteroid.getNumPoints()))) {
			return true;
		}
		return false;
	}
	
	private boolean playerEnemyshipCollision(Player player, Enemy enemy) {

		if (customIntersects(new Polygon(player.getXPts(), player.getYPts(), player.getNumPoints()),
				new Polygon(enemy.getXPts(), enemy.getYPts(), enemy.getNumPoints()))) {
			return true;
		}
		return false;
	}
	
	private boolean enemyshipAsteroidCollision(Enemy enemy, Asteroid asteroid) {

		if (customIntersects(new Polygon(enemy.getXPts(), enemy.getYPts(), enemy.getNumPoints()),
				new Polygon(asteroid.getXPts(), asteroid.getYPts(), asteroid.getNumPoints()))) {
			return true;
		}
		return false;
	}

	public boolean shotAsteroidCollision(Shot shot, Asteroid asteroid) {

		if (new Polygon(asteroid.getXPts(), asteroid.getYPts(), asteroid.getNumPoints())
				.intersects(new Rectangle((int) shot.getX(), (int) shot.getY(), 1, 1))) {
			return true;
		}
		return false;
	}

	public boolean shotEnemyshipCollision(Shot shot, Enemy enemy) {

		if (new Polygon(enemy.getXPts(), enemy.getYPts(), enemy.getNumPoints())
				.intersects(new Rectangle((int) shot.getX(), (int) shot.getY(), 1, 1))) {
			return true;
		}
		return false;
	}
	
	private boolean playerEnemyshotCollision(Player player, Shot enemyShot) {

		if (new Polygon(player.getXPts(), player.getYPts(), player.getNumPoints())
				.intersects(new Rectangle((int) enemyShot.getX(), (int) enemyShot.getY(), 1, 1))) {
			return true;
		}
		return false;
	}

	/**
	 * A custom intersect between polygons
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static boolean customIntersects(Polygon p1, Polygon p2) {

		Point p;
		for (int i = 0; i < p2.npoints; i++) {
			p = new Point(p2.xpoints[i], p2.ypoints[i]);
			if (p1.contains(p))
				return true;
		}
		for (int i = 0; i < p1.npoints; i++) {
			p = new Point(p1.xpoints[i], p1.ypoints[i]);
			if (p2.contains(p))
				return true;
		}
		return false;
	}

	/**
	 * Creates a new player on the middle of the screen
	 */
	protected void player_respawn(){
		
		boolean correct = false;
		
		while (!correct){
			
			player = new Player(this, 270, 170, -(Math.PI / 2), .20, .98, .1, 12);
			
			correct = true;
			
			for (int j = 0; j < asteroids.size(); j++) {
				
				if ( playerAsteroidCollision(player, asteroids.get(j)) ){
					
					correct = false;
					break;
				}
			}		
		}
		player.start();
		player.setActive(true);
	}
	
	/**
	 * Returns the score that every asteroid gives to the player on being destroyed
	 * @param asteroid
	 * @return
	 */
	public int ptsAsteroid(Asteroid asteroid) {

		int pts = 0;

		if (asteroid.getLifeLeft() == 3) {
			// + 20 pts big asteroid
			pts = 20;
		} else if (asteroid.getLifeLeft() == 2) {
			// + 50 pts medium asteroid
			pts = 50;
		} else if (asteroid.getLifeLeft() == 1) {
			// + 100 pts small asteroid
			pts = 100;
		}

		return pts;
	}
	
	/**
	 * Returns the points that every enemy ship gives to the player on being destroyed
	 * @param enemy
	 * @return pts
	 */
	public int ptsEnemyship(Enemy enemy) {

		int pts = 0;

		// +1000 small ship
		if (enemy.getSize() == 1){
			pts = 1000;
			
		// + 200 large ship
		}else if (enemy.getSize() == 2){
			pts = 200;
		}

		return pts;
	}
	
	/**
	 * Check if the player gets an extra life for gaining enough score points.
	 */
	protected void checkPtsAndLives(){
		
		if (score >= scoreForLife){
			
			scoreForLife += scoreForLife;
			
			if (lives < 3){
				lives++;
				new Sound("extraShip");
			}
		}
	}
	
	/**
	 * Finishes the current game.
	 */
	protected void gameOver(){
		
		int i = 0;
		
		for (i = 0; i < shots.size(); i++) {
			shots.get(i).die();
		}
		shots.removeAll(shots);
		
		for (i = 0; i < enemyShots.size(); i++) {
			enemyShots.get(i).die();
		}
		enemyShots.removeAll(enemyShots);
		
		game_on = false;
	}

	//Key listeners:
	
	@Override
	public synchronized void keyPressed(KeyEvent e) {

		if (splash_on) {

			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				splash_on = false;
				game_on = true;
				notifyAll();
			}

		}else if (!game_on) {

			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				splash_on = true;
				notifyAll();
			}

		} else if (e.getKeyCode() == KeyEvent.VK_P) {

			if (!player.isActive() && !paused) {
				player.setActive(true);

			} else {
				paused = !paused;
				if (paused) {
					player.setActive(false);
				} else {
					player.setActive(true);
				}
			}

			// Ff the game is paused or player is inactive, only let unpause
		} else if (paused || !player.isActive()) {
			return;

		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			if (!player.isAccelerating()){
				new Sound("thrust", 200, player);
			}
			player.setAccelerating(true);
			

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			player.setTurningLeft(true);

		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			player.setTurningRight(true);

		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			shooting = true;

		// Jump to HyperSpace, ramdomly teleports to another point in map
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			player.hyper();
			
		//Finishes the game as if the player had lost all 3 lives
		}else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			
			player.die();
			gameOver();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_UP) {
			player.setAccelerating(false);

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			player.setTurningLeft(false);

		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			player.setTurningRight(false);

		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			shooting = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// Not used
	}
}
