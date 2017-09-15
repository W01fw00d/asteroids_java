package classic;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class Window extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private Map map;
	
	protected Map getMap() {
		return map;
	}

	protected Window(Game game, Player player, int x, int y, int width, int height){
		super("Asteroids - Gabriel Romay Machado");
		
		setBounds(x, y, width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		map = new Map(game, player, x, y, width, height);
		add(map, BorderLayout.CENTER);
		
		setVisible(true);
		
		map.createBufferStrategy(2);
		
		new Thread (map).start();
		
	}
}
