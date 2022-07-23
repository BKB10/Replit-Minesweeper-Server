package kyleberkof.replitminesweeperserver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // takes input from the client socket
        try {
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.

            String line = "";

            while (!line.equals("end"))
            {
                try {
                    line = inputStream.readUTF();
                    System.out.println(line);
                }
                catch(IOException i) {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection for " + socket.getInetAddress().getHostAddress() + ".");

            // close connection
            socket.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
