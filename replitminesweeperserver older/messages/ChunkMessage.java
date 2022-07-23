package kyleberkof.replitminesweeperserver.messages;

import kyleberkof.replitminesweeperserver.minesweeperengine.*;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ChunkMessage extends Message {
    public static final byte HEADER = 2;

    private Vector2Int chunkPosition;
    private Chunk chunk;

    private World world;

    public ChunkMessage(Chunk chunk, Vector2Int chunkPosition) {
        super(HEADER);

        this.chunk = chunk;

        /*
        Chunk (72 + 17 * size bytes):
          - 4 bytes - X Position
          - 4 bytes - Y Position
          - 256 bytes - Tiles
          - 17 * size bytes - Entities
        */

        byteBuffer = ByteBuffer.allocate(72 + 17 * chunk.getEntities().size());
        byteBuffer.put(header); //Every message starts with the header

        //Position
        byteBuffer.putInt(chunkPosition.x);
        byteBuffer.putInt(chunkPosition.y);

        //Tiles
        Tile[][] tiles = chunk.getTiles();
        for(int x = 0; x < tiles.length; x ++) {
            for(int y = 0; y < tiles[x].length; y ++) {
                Tile tile = tiles[x][y];

                //Put the booleans into one byte
                byteBuffer.put((byte) ((tile.isFlagged() ? 0b00000001 : 0) + (tile.isCleared() ? 0b00000010 : 0)));
            }
        }

        //Entities
        ArrayList<Entity> entities = chunk.getEntities();
        for(int i = 0; i < entities.size(); i ++) {
            Entity entity = entities.get(i);

            byteBuffer.put(entity.getTypeId());
            byteBuffer.putFloat(entity.position.x);
            byteBuffer.putFloat(entity.position.y);
            byteBuffer.putFloat(entity.scale.x);
            byteBuffer.putFloat(entity.scale.y);
        }

        byteBuffer.reset(); //put position back to 0
    }

    public ChunkMessage(byte[] bytes, World world) {
        super(bytes, HEADER);

        this.world = world;
    }

    public void decode() {
        byteBuffer.get();

        chunkPosition = new Vector2Int(byteBuffer.getInt(), byteBuffer.getInt());

        Tile[][] tiles = new Tile[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
        for(int x = 0; x < tiles.length; x ++) {
            for(int y = 0; y < tiles[x].length; y ++) {
                byte tileByte = byteBuffer.get();

                tiles[x][y] = new Tile((tileByte & 0b00000001) != 0, (tileByte & 0b00000010) != 0, world);
            }
        }

        ArrayList<Entity> entities = new ArrayList<>();
        while(byteBuffer.hasRemaining()) {
            byte entityId = byteBuffer.get();

            Vector2Float position = new Vector2Float(byteBuffer.getFloat(), byteBuffer.getFloat());
            Vector2Float scale = new Vector2Float(byteBuffer.getFloat(), byteBuffer.getFloat());

            final byte PLAYER = 1;
            final byte BULLET = 2;
            switch(entityId) {
                case PLAYER:
                    entities.add(new PlayerEntity(position, scale, new Color(255, 0, 0))); //Make a way to transmit entity specific data (like for example the color of an entity). This would probably be a String.
                    break;
                case BULLET:
                    entities.add(new BulletEntity(position, scale, new Vector2Float(0, 0)));
                    break;
                default:
                    break;
            }
        }

        chunk = new Chunk();
    }

    public Vector2Int getChunkPosition() {
        return chunkPosition;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
