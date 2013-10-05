import java.awt.*;

public class Spaceship extends GameObject {
	final static int HEIGHT = 15;
	final static int WIDTH = 40;
	public boolean isDestroyed;

	public Spaceship(int fieldwidth, int fieldheight) {
		super((fieldwidth - WIDTH) / 2, fieldheight - HEIGHT - 20, 0, 0, WIDTH, HEIGHT);
		isDestroyed = false;
	}

	public void accelerate() {
		if (x < 0 || x > rightBound)
			velocityX = 0;
		if (y < 0 || y > bottomBound)
			velocityY = 0;
	}

	public int bulletContact (Bullet...bullets) {
		for (Bullet b : bullets) {
			if (this.intersects(b) != Intersection.NONE && b.isActive()) {
				isDestroyed = true;
				return 0;
			}
		}
		return 0;
	}
	
	public void draw(Graphics g) {
		Picture.draw(g, "spaceship.png", x, y);
	}

	public boolean isDestroyed() {
		return isDestroyed;
	}
}
