package kyleberkof.replitminesweeperserver;

import kyleberkof.replitminesweeperserver.messages.ChunkMessage;
import kyleberkof.replitminesweeperserver.messages.EntityUpdateMessage;
import kyleberkof.replitminesweeperserver.messages.FullTileUpdateMessage;
import kyleberkof.replitminesweeperserver.messages.TileUpdateMessage;
import kyleberkof.replitminesweeperserver.minesweeperengine.*;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MinesweeperServer {
    private Socket socket;
    private ServerSocket server;

    private World world;
    private Ticker ticker;

    private ArrayList<ConnectionHandler> handlers;

    public MinesweeperServer() {
      world = new World(true);

      handlers = new ArrayList<>();

      world.setServer(this);
    }

    private void clearHandlers() {
        for(int i = 0; i < handlers.size(); i ++) {
            ConnectionHandler handler = handlers.get(i);

            if(handler.getSocket().isClosed()) {
                handlers.remove(i);
                i--;
            }
        }
    }

    public void sendEntities(List<Entity> entities) {
        clearHandlers();
        for(int i = 0; i < handlers.size(); i ++) {
            ConnectionHandler handler = handlers.get(i);

            handler.sendEntities(entities);
        }
    }

    public void sendTiles(Vector2Int chunkPosition, List<Vector2Int> tilePositions, List<Tile> tiles) {
        clearHandlers();
        for(int i = 0; i < handlers.size(); i ++) {
            ConnectionHandler handler = handlers.get(i);

            if(handler.getLoadedChunks().contains(chunkPosition)) { //Send the tile updates if this chunk is loaded on the client
                handler.sendTiles(chunkPosition, tilePositions, tiles);
            }
        }
    }

    public void sendFullTiles(Vector2Int chunkPosition, Tile[][] tiles) {
        clearHandlers();
        for(int i = 0; i < handlers.size(); i ++) {
            ConnectionHandler handler = handlers.get(i);

            if(handler.getLoadedChunks().contains(chunkPosition)) { //Send the tile updates if this chunk is loaded on the client
                handler.sendFullTiles(chunkPosition, tiles);
            }
        }
    }

    public void start() {
        ticker = new Ticker(world, 30);
        new Thread(ticker).start(); //Start ticking thread

        try {
            server = new ServerSocket(7436);
            System.out.println("Server started.");

            System.out.println("Waiting for a client...");

            while(true) {
                socket = server.accept();
                System.out.println("Client " + socket.getInetAddress().getHostAddress() + " accepted.");

                PlayerEntity player = new PlayerEntity(new Vector2Float(5, 5), new Color(255, 0, 0, 150), world);
                world.addEntity(player);
                handlers.add(new ConnectionHandler(this, socket, player, world));
                new Thread(handlers.get(handlers.size() - 1)).start();
            }
        }
        catch(IOException i) {
            System.out.println(i);
        }
    }

    public ArrayList<ConnectionHandler> getConnectionHandlers() {
        return handlers;
    }
}
