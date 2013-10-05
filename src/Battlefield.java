import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;

import javax.swing.*;

@SuppressWarnings("serial")
public class Battlefield extends JComponent {
	private Alien ufo;
	private Bullet[] ufoBullets =
		{new Bullet(Color.RED), new Bullet(Color.RED), new Bullet(Color.RED)};
	private ArrayDeque<ArrayDeque<Alien>> enemies;
	private Spaceship spaceship;
	private Bullet spaceshipBullet;
	private Bullet alienBullet;
	private Shield[] shields;
	private Scoreboard scoreboard;
	private int score;
	private int level;
	private boolean gameOver = false;

	private int interval = 35; // Milliseconds between updates.
	private Timer timer;       // Each time timer fires we animate one step.

	// Dimension and gameplay fields
	private static final int FIELDWIDTH  = 400;              // Playing field width in pixels
	private static final int FIELDHEIGHT = 500;              // Playing field height in pixels
	private static final int ALIENROWS = 6;                  // How many rows of aliens?
	private static final int ALIENCOLS = 8;			         // How many columns?
    private static final int ALIEN_HORIZ_SEPARATION = 13;    // Separation between aliens
	private static final int ALIEN_VERT_SEPARATION = 4;      // Separation between aliens
	
	private static final int SPACESHIP_VEL = 8;              // How fast the spaceship moves
	private static final int INITIAL_ALIEN_VELOCITY = 1;     // How fast aliens move at the start
	private int alienVelocity;                               // How fast aliens move
	private static final int BULLET_SPEED = 20;              // How fast bullets move
	private static final int UFO_VELOCITY = 5;               // UFO Velocity
	
	private static final double ALIEN_FIRE_FREQ = 0.005;     // Alien rate of fire
	private static final double UFO_FIRE_FREQ = 0.006;       // UFO rate of fire
	private static final double UFO_APPEARANCE_RATE = 0.002; // UFO prevalence rate
	
	private static final int NUM_OF_SHIELDS = 4;             // Number of shields to start with
	private static final int SHIELD_HEALTH = 10;             // Initial shield health
	private static final int SHIELD_RESTORE_PER_LEVEL = 3;   // Shield healing per level

	private static final int MAX_LIVES = 6;                  // Number of initial lives
	private int numOfLives;                                  // Number of lives.




	public Battlefield() {
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setFocusable(true);

		timer = new Timer(interval, new ActionListener() {
			public void actionPerformed(ActionEvent e) { tick(); }});
		timer.start(); 

		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					spaceship.setVelocity(-SPACESHIP_VEL, 0);
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					spaceship.setVelocity(SPACESHIP_VEL, 0);
				else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (!spaceshipBullet.isActive()) {
						spaceshipBullet.fire(
								spaceship.x + (spaceship.width / 2),
								spaceship.y + Bullet.HEIGHT,
								BULLET_SPEED);
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_R)
					reset();
				else if (e.getKeyCode() == KeyEvent.VK_Q)
					System.exit(0);
				else if (e.getKeyCode() == KeyEvent.VK_H)
					help();
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					return;
				}
				spaceship.setVelocity(0, 0);
			}
		});
		// After a Battlefield object is built and installed in a container
		// hierarchy, somebody should invoke reset() to get things started... 
	}

	/** Set the state of the state of the game to its initial value and 
	    prepare the game for keyboard input. */
	public void reset() {
		timer.start();
		gameOver = false;
		numOfLives = MAX_LIVES;
		alienVelocity = INITIAL_ALIEN_VELOCITY;
		enemies = createEnemies(ALIENCOLS, ALIENROWS);
		spaceship = new Spaceship(FIELDWIDTH, FIELDHEIGHT);
		spaceshipBullet = new Bullet(Color.GREEN);
		alienBullet = new Bullet(Color.WHITE);
		shields = createShields(NUM_OF_SHIELDS, SHIELD_HEALTH);
		ufo = new Alien(0, 0, 0, 0, FIELDWIDTH, AlienType.UFO);
		ufo.setDestroyed(true);
		scoreboard = new Scoreboard(FIELDWIDTH, numOfLives);
		score = 0;
		level = 1;
		requestFocusInWindow();
	}

	/**
	 * Set the state of the game to the initial one except for shield states.
	 */
	public void resetExceptShields() {
		if (enemies.isEmpty()) {
			enemies = createEnemies(ALIENCOLS, ALIENROWS);
		}
		spaceship = new Spaceship(FIELDWIDTH, FIELDHEIGHT);
		spaceshipBullet = new Bullet(Color.GREEN);
		alienBullet = new Bullet(Color.WHITE);
		ufo = new Alien(0, 0, 0, 0, FIELDWIDTH, AlienType.UFO);
		ufo.setDestroyed(true);
		requestFocusInWindow();
	}

	/** Update the game one timestep by moving the bullets, aliens, etc.*/
	void tick() {

		spaceship.setBounds(getWidth(), getHeight());
		spaceship.move();

		alienMovement();
		if (enemies.isEmpty()) {
			levelWon();
		}

		for (Shield s : shields) {
			score += s.bulletContact(spaceshipBullet, alienBullet);
			score += s.bulletContact(ufoBullets);
		}

		spaceshipBullet.setBounds(getWidth(), getHeight());
		spaceshipBullet.move();

		alienBullet.setBounds(getWidth(), getHeight());
		alienBullet.move();

		spaceship.bulletContact(alienBullet);
		spaceship.bulletContact(ufoBullets);

		if (!ufo.isDestroyed()) {
			ufoMove();
		} else if (Math.random() <= UFO_APPEARANCE_RATE) {
			ufoAppear();
		}

		for (Bullet b : ufoBullets) {
			b.setBounds(getWidth(), getHeight());
			b.move();
		}

		if (spaceship.isDestroyed()) {
			levelLost();
		}

		scoreboard.setScore(score);
		
		repaint(); // Repaint indirectly calls paintComponent.
	}

	private void levelLost() {
		numOfLives--;
		scoreboard.setLives(numOfLives);
		resetExceptShields();
		if (numOfLives <= 0) {
			timer.stop();
			gameOver = true;
			this.repaint();
		}

	}

	private void levelWon() {
		alienVelocity++;
		scoreboard.setLevel(++level);
		for (Shield s : shields) {
			s.heal(SHIELD_RESTORE_PER_LEVEL);
		}
		numOfLives++;
		resetExceptShields();

	}

	private void ufoAppear() {
		ufo.x = ufo.rightBound;
		ufo.y = 5;
		ufo.setVelocity(-UFO_VELOCITY, 0);
		ufo.setDestroyed(false);
	}
	
	/**
	 * Moves the UFO, checks if it's been hit, and fires its bullets.
	 */
	private void ufoMove() {
		ufo.setBounds(getWidth(), getHeight());
		ufo.move();
		
		score += ufo.bulletContact(spaceshipBullet);
		
		if (ufo.hasReachedEdge()) {
			ufo.setDestroyed(true);
			ufo.setVelocity(0, 0);
			return;
		}
		
		for (Bullet b : ufoBullets) {
			if (!b.isActive()) {
				double rand = Math.random();
				if (rand <= UFO_FIRE_FREQ) {
					b.fire(ufo.x, ufo.y + ufo.height, -BULLET_SPEED);
				}
			}
		}
	}

	/**
	 * Moves each Alien, and removes them from play if they had contact with a Bullet.
	 * It may also decide to fire a bullet towards the Spaceship.
	 * If one of them has reached the edge, then it reverses direction and lowers all of them.
	 */
	private void alienMovement() {
		boolean hasReachedEdge = false;

		if (enemies.getFirst().getFirst().hasReachedEdge() ||
				enemies.getLast().getLast().hasReachedEdge()) {
			hasReachedEdge = true;
		}

		ArrayDeque<ArrayDeque<Alien>> columnsToRemove = new ArrayDeque<ArrayDeque<Alien>>();

		for (ArrayDeque<Alien> al : enemies) {
			ArrayDeque<Alien> aliensToRemove = new ArrayDeque<Alien>();
			for (Alien alien : al) {
				if (hasReachedEdge) {
					alien.moveDown();
				}

				if (!alien.isDestroyed()) {
					alien.setBounds(getWidth(), getHeight());
					alien.move();
					if (alien.y >= shields[0].y) {
						timer.stop();
						gameOver = true;
						this.repaint();
					}
					score += alien.bulletContact(spaceshipBullet);
					if (alien.isDestroyed()) {
						aliensToRemove.add(alien);
					}
					if (al.getLast().equals(alien)) {
						double rand = Math.random();
						if (rand <= ALIEN_FIRE_FREQ && !alienBullet.isActive()) {
							alienBullet.fire(
									alien.x + alien.width / 2,
									alien.y + alien.height,
									-BULLET_SPEED);
						}
					}
				}
			}
			al.removeAll(aliensToRemove);
			if (al.isEmpty()) {
				columnsToRemove.add(al);
			}
		}	

		enemies.removeAll(columnsToRemove);
	}

	/*
	 * GameObject creation methods
	 */

	/**
	 * Creates a Deque of Deques of Aliens. Each Deque is a column, and each Alien
	 * within the same Deque is in a different row. 
	 * @param rows The number of rows of aliens
	 * @param columns The number of columns of aliens
	 * @return A Deque of Deques containing Aliens.
	 */
	private ArrayDeque<ArrayDeque<Alien>> createEnemies(int rows, int columns) {
		ArrayDeque<ArrayDeque<Alien>> list = new ArrayDeque<ArrayDeque<Alien>>();
		
		for (int i = 0; i < rows; i++) {
			ArrayDeque<Alien> al = new ArrayDeque<Alien>();
			for (int j = 0; j < columns; j++) {
				AlienType type =
						j <= 1 ? AlienType.TYPE3 :
						j <= 3 ? AlienType.TYPE2 :
						 		 AlienType.TYPE1;
				
				al.add(new Alien (
						i * (22 + ALIEN_HORIZ_SEPARATION),
						j * (16 + ALIEN_VERT_SEPARATION) + 25,
						alienVelocity,
						0,
						FIELDWIDTH,
						type));
			}
			list.add(al);
		}
		return list;
	}

	/**
	 * Creates an array of Shields evenly spaced throughout the Battlefield.
	 * @param num The number of shields to be created
	 * @param health The health of each field
	 * @return An array of Shields.
	 */
	private Shield[] createShields(int num, int health) {
		int height = 15;
		int width = FIELDWIDTH / (2 * num + 1);
		int yLoc = FIELDHEIGHT - 60;
		Shield[] shields = new Shield[num];

		for (int i = 0; i < num ; i++) {
			shields[i] = new Shield((2 * i + 1) * width, yLoc, width, height, health);
		}

		return shields;
	}

	/**
	 * Displays a help screen. For simplicity (and because JLabels were devised by the enemy),
	 * it displays an image, help.png. It stops the timer and resumes it when ready. Note that
	 * the only way to close the pop-up is to click on the button.
	 */
	public void help() {
		timer.stop();
		final JFrame frame = new JFrame("Help");
		frame.setLocation(400, 400);
		frame.setSize(456, 531);
		frame.setResizable(false);
		final JComponent picture = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Picture.draw(g, "help.png", 0, 0);
			}
		};
		
		final JButton exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				requestFocusInWindow();
				timer.start();
			}
		});
		
		final JPanel exitpanel = new JPanel();
		exitpanel.add(exit);
		frame.add(exitpanel, BorderLayout.SOUTH);
		frame.add(picture, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
	}
	
	/*
	 * PaintComponent inherited methods
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Paint background, border
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, FIELDWIDTH, FIELDHEIGHT);

		if (!gameOver) {
			for (ArrayDeque<Alien> al : enemies) {
				for (Alien alien : al) {
					alien.draw(g);
				}
			}

			for (Shield s : shields) {
				s.draw(g);
			}

			ufo.draw(g);
			
			for (Bullet b : ufoBullets) {
				b.draw(g);
			}
			
			spaceship.draw(g);
			spaceshipBullet.draw(g);
			alienBullet.draw(g);
			scoreboard.draw(g);
		} else {
			g.setColor(Color.GREEN);
			g.setFont(new Font("Courier", Font.BOLD, 14));
			g.drawString("You have lost!", 50, 100);
			g.drawString("You got " + score + " points", 50, 120);
			g.drawString("Press R to restart or Q to quit", 50, 140);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(FIELDWIDTH, FIELDHEIGHT);
	}
}
