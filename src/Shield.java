import java.awt.Color;
import java.awt.Graphics;


public class Shield extends GameObject {

	private int health;
	public boolean isDestroyed;

	public Shield(int x, int y, int width, int height, int maxHealth) {
		super (x, y, 0, 0, width, height);
		this.health = maxHealth;
		isDestroyed = false;
	}

	public int bulletContact (Bullet...bullets) {
		for (Bullet b : bullets) {
			if (this.intersects(b) != Intersection.NONE && b.isActive() && !isDestroyed) {
				b.setInactive();
				this.health--;
				if (this.health <= 0) {
					isDestroyed = true;
					return -5;
				}
			}
		}
		return 0;
	}

	public void heal (int i) {
		health += i;
		if (health >= 1) {
			isDestroyed = false;
		}
	}
	
	@Override
	public void accelerate() {

	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.GREEN);
		for (int i = 0 ; i < health; i++) {
			int xmid = width / 2;
			int xleft = i * xmid / health;
			int xright = width - xleft;
			g.drawLine(x + xleft, y + height, x + xmid, y);
			g.drawLine(x + xright, y + height, x + xmid, y);
		}
	}

}
