package kyleberkof.replitminesweeperserver.messages;

import kyleberkof.replitminesweeperserver.minesweeperengine.Tile;
import kyleberkof.replitminesweeperserver.minesweeperengine.Vector2Int;
import kyleberkof.replitminesweeperserver.minesweeperengine.World;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TileUpdateMessage extends Message {
  public static final byte HEADER = 3;

  private Vector2Int chunkPosition;
  private List<Vector2Int> tilePositions;
  private List<Tile> tiles;

  private World world;

  public TileUpdateMessage(Vector2Int chunkPosition, List<Vector2Int> tilePositions, List<Tile> tiles) {
    super(HEADER);

    this.chunkPosition = chunkPosition;
    this.tiles = tiles;

    byteBuffer = ByteBuffer.allocate(8 + 9 * tiles.size() + 5);
    byteBuffer.mark();
    byteBuffer.putInt(8 + 9 * tiles.size() + 1); // The length of the chunk
    byteBuffer.put(header); //Every message starts with the header

    byteBuffer.putInt(chunkPosition.x);
    byteBuffer.putInt(chunkPosition.y);

    for(int i = 0; i < tiles.size(); i ++) {
      Tile tile = tiles.get(i);

      byteBuffer.putInt(tilePositions.get(i).x);
      byteBuffer.putInt(tilePositions.get(i).y);

      byteBuffer.put((byte) ((tile.isFlagged() ? 0b00000001 : 0) + (tile.isCleared() ? 0b00000010 : 0) + (tile.hasBomb() ? 0b00000100 : 0)));
    }
  }

  public TileUpdateMessage(byte[] bytes, World world) {
    super(bytes, HEADER);

    this.world = world;

    decode();
    byteBuffer.reset();
  }

  public void decode() {
    //Get the length and header out of the way
    byteBuffer.getInt();
    byteBuffer.get();

    chunkPosition = new Vector2Int(byteBuffer.getInt(), byteBuffer.getInt());

    tilePositions = new ArrayList<>();
    tiles = new ArrayList<>();
    while(byteBuffer.hasRemaining()) {
      tilePositions.add(new Vector2Int(byteBuffer.getInt(), byteBuffer.getInt()));

      byte tileByte = byteBuffer.get();
      tiles.add(new Tile((tileByte & 0b00000100) != 0, (tileByte & 0b00000001) != 0, (tileByte & 0b00000010) != 0, world));
    }
  }

  public Vector2Int getChunkPosition() {
    return chunkPosition;
  }

  public List<Vector2Int> getTilePositions() {
    return tilePositions;
  }

  public List<Tile> getTiles() {
    return tiles;
  }
}