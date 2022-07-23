package kyleberkof.replitminesweeperserver.minesweeperengine;

import java.awt.Graphics2D;

public abstract class Entity {
  //On the scale of tile sizes, global positions rather than relative to chunks
  private byte typeId;
  private int id;
  public Vector2Float position;
  public Vector2Float scale;

  Entity(Vector2Float position, Vector2Float scale, byte typeId) {
    this.position = position;
    this.scale = scale;
    this.typeId = typeId;
  }

  public void setId(int id) {
    this.id = id;
  }

  //public abstract void render(MinesweeperRenderer renderer, Graphics2D g);

  //No ticking will happen on the client in multiplayer (and neither will it in singleplayer because there are no entities there)
  public abstract void tick(World world, float timeMultiplier);

  public int getId() {
    return id;
  }

  public byte getTypeId() {
    return typeId;
  }
}