package kyleberkof.replitminesweeperserver.minesweeperengine;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;
import java.awt.MouseInfo;

class PlayerEntity extends Entity {
  public static final byte TYPE_ID = 1;

  private Color color;

  private Point screenPos;

  public PlayerEntity(Vector2Float position, Color color) {
    super(position, new Vector2Float(0.75f, 0.75f), TYPE_ID);

    this.color = color;

    screenPos = new Point(0, 0);
  }

  public PlayerEntity(Vector2Float position, Vector2Float scale, Color color) {
    super(position, scale, TYPE_ID);

    this.color = color;

    screenPos = new Point(0, 0);
  }

  public void render(MinesweeperRenderer renderer, Graphics2D g) {
    //System.out.println("Rendering " + position);
    //Remember that the scale of entity positions is blocks
    Vector2Float tileScreenPos = renderer.getTilePositionFloat(screenPos);

    position.x = tileScreenPos.x;
    position.y = tileScreenPos.y;

    g.setColor(color);
    //g.fillOval((int) ((position.x - scale.x / 2) * tileSize.x), (int) ((position.y - scale.y / 2) * tileSize.y), (int) (scale.x * tileSize.x), (int) (scale.y * tileSize.y));
    //g.fillOval((int) (renderer.getCameraPosition().x * tileSize.x), (int) (renderer.getCameraPosition().y * tileSize.y), (int) (scale.x * tileSize.x), (int) (scale.y * tileSize.y));
    g.fillOval((int) ((tileScreenPos.x - renderer.getCameraPosition().x - scale.x / 2) * renderer.getTileWidth()), (int) ((tileScreenPos.y - renderer.getCameraPosition().y - scale.y / 2) * renderer.getTileHeight()), (int) (scale.x * renderer.getTileWidth()), (int) (scale.y * renderer.getTileHeight()));
  }

  public void tick(World world, float timeMultiplier) {
    screenPos = MouseInfo.getPointerInfo().getLocation();
  }
}