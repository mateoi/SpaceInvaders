import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;


public class Scoreboard extends GameObject {

	private int level;
	private int lives;
	private int score;

	public Scoreboard(int width, int lives) {
		super(0, 0, 0, 0, width, 20);
		this.level = 1;
		this.lives = lives;
		this.score = 0;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public void setScore(int score) {
		this.score = score;
	}



	@Override
	public void accelerate() {
		// This GameObject doesn't move.
	}

	@Override
	public void setVelocity(int i, int j) {
		throw new UnsupportedOperationException("Changing the Scoreboard velocity");
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		g.setFont(new Font("Courier", Font.PLAIN, 12));
		if (lives <= 5) {
			g.drawString("Lives: ", 5, 20);
			for (int i = 0; i < lives; i++) {
				Picture.draw(g, "spaceship_mini.png", 50 + i * 22, y + 12);
			}
		} else {
			g.drawString("Lives: " + lives, x + 5, y + 20);
		}
		
		g.drawString("Level: " + level, x + 200, y + 20);
		g.drawString("Score: " + score, x + 300, y + 20);

	}

	@Override
	public int bulletContact(Bullet... bullets) {
		throw new UnsupportedOperationException("Shooting at the Scoreboard");
	}

}
