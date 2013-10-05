import java.awt.*;

public class Alien extends GameObject {
	private boolean isDestroyed;
	private int leftLimit;
	private int rightLimit;
	private AlienType type;

	public Alien(int x, int y, int velocityX, int leftLimit, int rightLimit, AlienType type) {
		super(x, y, velocityX, 0, 0, 0);
		width = type == AlienType.UFO ? 32 : 22;
		height = type == AlienType.UFO ? 14 : 16;
		isDestroyed = false;
		this.leftLimit = leftLimit;
		this.rightLimit = rightLimit - width;
		this.type = type;
	}

	public boolean isDestroyed() {
		return isDestroyed;
	}

	public void setDestroyed(boolean b) {
		this.isDestroyed = b;
	}

	public void moveDown() {
		velocityX =  -velocityX;
		y += height / 2;
	}

	public void accelerate() {
		// the check for reaching the edge is handled by the tick() method in
		// the Battlefield class.
		if (y < 0)
			velocityY =  Math.abs(velocityY);
		else if (y > bottomBound)
			velocityY = -Math.abs(velocityY);
	}

	/**
	 * Calculates how many points are awarded to the player for killing this Alien.
	 * returns 10 for TYPE1, 20 for TYPE2, 40 for TYPE3 and 100 for UFO.
	 * @return the points to be awarded
	 */
	public int pointYield() {
		switch (type) {
		case TYPE1 : return 10;
		case TYPE2 : return 20;
		case TYPE3 : return 40;
		case UFO   : return 100;
		default    : return 0;
		}
	}

	public int bulletContact(Bullet... bullets) {
		for (Bullet b : bullets) {
			if (this.intersects(b) != Intersection.NONE && b.isActive()) {
				b.setInactive();
				this.setDestroyed(true);
				this.velocityX = 0;
				this.velocityY = 0;
				return this.pointYield();
			}
		}
		
		return 0;
	}

	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		if (!isDestroyed) {
			String filepath = "alien1.png";
			switch (type) {
			case TYPE1 : filepath = "alien1.png"; break;
			case TYPE2 : filepath = "alien2.png"; break;
			case TYPE3 : filepath = "alien3.png"; break;
			case UFO : filepath = "ufo.png"; break;
			}
			Picture.draw(g, filepath, x, y);
		}
	}

	public boolean hasReachedEdge() {
		return (x <= leftLimit || x >= rightLimit);
	}
}
