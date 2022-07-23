package kyleberkof.replitminesweeperserver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello");

        MinesweeperServer server = new MinesweeperServer();
        server.start();
    }
}