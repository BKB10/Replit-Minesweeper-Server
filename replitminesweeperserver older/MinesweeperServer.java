package kyleberkof.replitminesweeperserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MinesweeperServer {
    private Socket socket;
    private ServerSocket server;

    private ConnectionHandler handler;

    public MinesweeperServer() {

    }

    public void start() {
        try {
            server = new ServerSocket(7436);
            System.out.println("Server started.");

            System.out.println("Waiting for a client...");

            while(true) {
                socket = server.accept();
                System.out.println("Client " + socket.getInetAddress().getHostAddress() + " accepted.");

                handler = new ConnectionHandler(socket);
                new Thread(handler).start();
            }
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }
}
