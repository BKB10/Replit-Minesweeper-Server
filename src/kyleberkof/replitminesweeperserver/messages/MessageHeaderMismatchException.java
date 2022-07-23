package kyleberkof.replitminesweeperserver.messages;

public class MessageHeaderMismatchException extends RuntimeException {
    public MessageHeaderMismatchException(byte expectedHeader, byte bufferHeader) {
        super(expectedHeader + " header expected but got header " + bufferHeader);
    }
}
