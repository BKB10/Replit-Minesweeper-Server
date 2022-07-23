package kyleberkof.replitminesweeperserver.minesweeperengine;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;
import java.awt.MouseInfo;

public class PlayerEntity extends Entity {
  public static final byte TYPE_ID = 1;

  private Color color;

  private World world;

  public PlayerEntity(Vector2Float position, Color color, World world) {
    super(position, new Vector2Float(0.75f, 0.75f), TYPE_ID);

    this.color = color;
    this.world = world;
  }

  public PlayerEntity(Vector2Float position, Vector2Float scale, Color color, World world) {
    super(position, scale, TYPE_ID);

    this.color = color;
    this.world = world;
  }

  public void tick(World world, float timeMultiplier) {

  }

  public void shoot() {
    //world.addEntity(new BulletEntity());
  }
}