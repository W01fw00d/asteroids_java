package classic;

import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class Sound extends Thread {

	String name;
	int cooldown;
	Player player;
	Enemy enemy;
	Game game;

	// Normal sound constructor
	Sound(String name) {

		play_sound(name);
	}

	// Flame sound constructor
	Sound(String name, int cooldown, Player player) {

		this.name = name;
		this.cooldown = cooldown;
		this.player = player;

		this.start();
	}

	// Enemy ship sound constructor
	Sound(int size, int cooldown, Enemy enemy) {

		if (size == 1) {
			this.name = "saucerSmall";
		} else if (size == 2) {
			this.name = "saucerBig";
		}

		this.cooldown = cooldown;
		this.enemy = enemy;

		this.start();
	}

	// Game background sound constructor
	Sound(int cooldown, Game game) {

		this.cooldown = cooldown;
		this.game = game;

		this.start();
	}

	// Asteroids sound constructor
	Sound(int size) {

		switch (size) {

		case 8:
			play_sound("bangLarge");
			break;
		case 4:
			play_sound("bangMedium");
			break;
		case 2:
			play_sound("bangSmall");
			break;
		}
	}

	public void run() {

		if (player != null) {
			try {

				while (player.isAlive() && player.isAccelerating()) {

					Thread.sleep(cooldown);
					play_sound(name);
				}

			} catch (InterruptedException v) {
				System.out.println(v);
			}

		} else if (enemy != null) {

			try {

				while (enemy.isAlive()) {

					Thread.sleep(cooldown);
					play_sound(name);
				}

			} catch (InterruptedException v) {
				System.out.println(v);
			}

		} else if (game != null) {

			try {

				while (game.isGame_on()) {

					play_sound("beat1");
					Thread.sleep(cooldown);
					play_sound("beat2");
					Thread.sleep(cooldown);
				}

			} catch (InterruptedException v) {
				System.out.println(v);
			}
		}
	}

	/**
	 * Plays the chosen sound
	 * @param sound
	 */
	protected void play_sound(String sound) {

		try {
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;
			Clip clip;
			stream = AudioSystem.getAudioInputStream(new File("src/classic/sounds/" + sound + ".wav")); 
			// for .jar compiling: new File("sounds/" + sound + ".wav")
			format = stream.getFormat();
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();
			
		} catch (Exception e) {
		}
	}
}
