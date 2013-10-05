import java.awt.Color;
import java.awt.Graphics;


public class Bullet extends GameObject {

	private boolean isActive;

	final static int WIDTH = 2;
	final static int HEIGHT = 10;
	private Color color;

	public Bullet (Color c) {
		super (0, 0, 0, 0, WIDTH, HEIGHT);
		color = c;
	}

	public boolean isActive() {
		return isActive;
	}
	
	public void setInactive () {
		this.isActive = false;;
		velocityX = 0;
		velocityY = 0;
	}

	@Override
	public void accelerate() {
		if (y < 0 || y > bottomBound) {
			isActive = false;
			this.velocityY = 0;
		}

	}


	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		if (isActive) {
			g.fillRect(x, y, WIDTH, HEIGHT);
		}
	}

	/**
	 * Fires this bullet, with initial x and y position and given y-velocity
	 * (Bullets don't move left or right)
	 * @param x Starting x-coordinate
	 * @param y Starting y-coordinate
	 * @param vel Bullet velocity
	 */
	public void fire(int x, int y, int vel) {
		this.x = x;
		this.y = y;
		this.velocityY = -vel;
		this.isActive = true;
	}

	@Override
	public int bulletContact(Bullet... bullets) {
		throw new UnsupportedOperationException("Bullet-on-Bullet contact");		
	}

}
