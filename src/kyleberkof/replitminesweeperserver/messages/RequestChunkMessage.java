package kyleberkof.replitminesweeperserver.messages;

import kyleberkof.replitminesweeperserver.minesweeperengine.Vector2Int;

import java.nio.ByteBuffer;

public class RequestChunkMessage extends Message {
    public static final byte HEADER = 1;

    private Vector2Int chunkPosition;

    public RequestChunkMessage(Vector2Int chunkPosition) {
        super(HEADER);

        this.chunkPosition = chunkPosition;

        byteBuffer = ByteBuffer.allocate(13);
        byteBuffer.mark();
        byteBuffer.putInt(9);
        byteBuffer.put(header);

        byteBuffer.putInt(chunkPosition.x);
        byteBuffer.putInt(chunkPosition.y);
    }

    public RequestChunkMessage(byte[] bytes) {
        super(bytes, HEADER);

        decode();
        byteBuffer.reset();
    }

    public void decode() {
      byteBuffer.getInt();
      byteBuffer.get();

      chunkPosition = new Vector2Int(byteBuffer.getInt(), byteBuffer.getInt());
    }

    public Vector2Int getChunkPosition() {
      return chunkPosition;
    }
}
