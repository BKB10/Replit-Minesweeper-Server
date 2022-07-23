package kyleberkof.replitminesweeperserver.messages;

import java.nio.ByteBuffer;

public abstract class Message {
    protected ByteBuffer byteBuffer;

    protected byte header;

    public Message(byte header) {
        this.header = header;
    }

    public Message(byte[] message, byte header) {
        if(message[0] != header) {
            throw(new MessageHeaderMismatchException(header, message[0]));
        }

        byte[] messageWithLength = new byte[message.length + 4];
        System.arraycopy(message, 0, messageWithLength, 1, message.length);

        byteBuffer = ByteBuffer.wrap(messageWithLength);
        byteBuffer.putInt(0, message.length);
        byteBuffer.reset(); //Put position back to 0
        decode();
    }

    public abstract void decode();

    public byte[] getByteArray() {
        return byteBuffer.array();
    }

    public byte getHeader() {
        return header;
    }
}
