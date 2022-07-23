package kyleberkof.replitminesweeperserver.messages;

import kyleberkof.replitminesweeperserver.minesweeperengine.Vector2Int;

import java.nio.ByteBuffer;

public class ChunkUnloadMessage extends Message {
    public static final byte HEADER = 6;

    private Vector2Int chunkPosition;

    public ChunkUnloadMessage(Vector2Int chunkPosition) {
        super(HEADER);

        this.chunkPosition = chunkPosition;

        byteBuffer = ByteBuffer.allocate(13);
        byteBuffer.mark();
        byteBuffer.putInt(9);
        byteBuffer.put(header);

        byteBuffer.putInt(chunkPosition.x);
        byteBuffer.putInt(chunkPosition.y);
    }

    public ChunkUnloadMessage(byte[] bytes) {
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