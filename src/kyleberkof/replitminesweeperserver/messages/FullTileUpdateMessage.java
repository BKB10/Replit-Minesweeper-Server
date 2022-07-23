package kyleberkof.replitminesweeperserver.messages;

import kyleberkof.replitminesweeperserver.minesweeperengine.*;

import java.nio.ByteBuffer;

public class FullTileUpdateMessage extends Message {
  public static final byte HEADER = 4;

  private Vector2Int chunkPosition;
  private Tile[][] tiles;

  private World world;

  public FullTileUpdateMessage(Vector2Int chunkPosition, Tile[][] tiles) {
    super(HEADER);

    this.chunkPosition = chunkPosition;
    this.tiles = tiles;

    byteBuffer = ByteBuffer.allocate(8 + tiles.length * tiles[0].length + 5);
    byteBuffer.mark();
    byteBuffer.putInt(8 + tiles.length * tiles[0].length + 1); // The length of the chunk
    byteBuffer.put(header); //Every message starts with the header

    byteBuffer.putInt(chunkPosition.x);
    byteBuffer.putInt(chunkPosition.y);

    for(int x = 0; x < tiles.length; x ++) {
      for(int y = 0; y < tiles[x].length; y ++) {
        Tile tile = tiles[x][y];

        byteBuffer.put((byte) ((tile.isFlagged() ? 0b00000001 : 0) + (tile.isCleared() ? 0b00000010 : 0) + (tile.hasBomb() ? 0b00000100 : 0)));
      }
    }
  }

  public FullTileUpdateMessage(byte[] bytes, World world) {
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

    tiles = new Tile[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];
    while(byteBuffer.hasRemaining()) {
      for(int x = 0; x < tiles.length; x ++) {
        for(int y = 0; y < tiles[x].length; y ++) {
          byte tileByte = byteBuffer.get();
          tiles[x][y] = new Tile((tileByte & 0b00000100) != 0, (tileByte & 0b00000001) != 0, (tileByte & 0b00000010) != 0, world);
        }
      }
    }
  }

  public Vector2Int getChunkPosition() {
    return chunkPosition;
  }

  public Tile[][] getTiles() {
    return tiles;
  }
}