package kyleberkof.replitminesweeperserver.messages;

import kyleberkof.replitminesweeperserver.minesweeperengine.*;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class EntityUpdateMessage extends Message {
  public static final byte HEADER = 5;

  private List<Entity> entities;

  private World world;

  public EntityUpdateMessage(List<Entity> entities) {
    super(HEADER);

    this.entities = entities;

    byteBuffer = ByteBuffer.allocate(21 * entities.size() + 5);
    byteBuffer.mark();
    byteBuffer.putInt(21 * entities.size() + 1); // The length of the chunk
    byteBuffer.put(header); //Every message starts with the header

    for(int i = 0; i < entities.size(); i ++) {
      Entity ent = entities.get(i);

      byteBuffer.put(ent.getTypeId());
      byteBuffer.putInt(ent.getId());
      byteBuffer.putFloat(ent.position.x);
      byteBuffer.putFloat(ent.position.y);
      byteBuffer.putFloat(ent.scale.x);
      byteBuffer.putFloat(ent.scale.y);
    }

    byteBuffer.reset();
  }

  public EntityUpdateMessage(byte[] bytes, World world) {
    super(bytes, HEADER);

    this.world = world;

    decode();
    byteBuffer.reset();
  }

  public void decode() {
    //Get the length and header out of the way
    byteBuffer.getInt();
    byteBuffer.get();

    entities = new ArrayList<>();
    while(byteBuffer.hasRemaining()) {
      byte typeId = byteBuffer.get();

      int id = byteBuffer.getInt();

      Vector2Float position = new Vector2Float(byteBuffer.getFloat(), byteBuffer.getFloat());
      Vector2Float scale = new Vector2Float(byteBuffer.getFloat(), byteBuffer.getFloat());

      final byte PLAYER = 1;
      final byte BULLET = 2;
      Entity entity;
      switch(typeId) {
        case PLAYER:
          entity = new PlayerEntity(position, scale, new Color(255, 0, 0), world);
          entity.setId(id);
          entities.add(entity); //Make a way to transmit entity specific data (like for example the color of an entity). This would probably be a String.
          break;
        case BULLET:
          entity = new BulletEntity(position, scale, new Vector2Float(0, 0));
          entity.setId(id);
          entities.add(entity);
          break;
        default:
          break;
      }
    }
  }

  public List<Entity> getEntities() {
    return entities;
  }
}