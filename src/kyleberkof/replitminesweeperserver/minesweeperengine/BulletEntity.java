package kyleberkof.replitminesweeperserver.minesweeperengine;

import java.awt.Graphics2D;
import java.awt.Color;

public class BulletEntity extends Entity {
  public static final byte TYPE_ID = 2;

  private Color color;

  private Vector2Float velocity;

  public BulletEntity(Vector2Float position, Vector2Float velocity) {
    super(position, new Vector2Float(0.25f, 0.25f), TYPE_ID);

    this.velocity = velocity;

    color = new Color(255, 255, 255);
  }

  public BulletEntity(Vector2Float position, Vector2Float scale, Vector2Float velocity) {
    super(position, scale, TYPE_ID);

    this.velocity = velocity;

    color = new Color(255, 255, 255);
  }

  /*
  public void render(MinesweeperRenderer renderer, Graphics2D g) {
    g.setColor(color);
    g.fillOval((int) ((position.x - renderer.getCameraPosition().x - scale.x / 2) * renderer.getTileWidth()), (int) ((position.y - renderer.getCameraPosition().y - scale.y / 2) * renderer.getTileHeight()), (int) (scale.x * renderer.getTileWidth()), (int) (scale.y * renderer.getTileHeight()));
  }
   */

  public void tick(World world, float timeMultiplier) {
    position.x += velocity.x;
    position.y += velocity.y;
  }
}