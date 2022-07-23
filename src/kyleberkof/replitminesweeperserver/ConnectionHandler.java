package kyleberkof.replitminesweeperserver;

import kyleberkof.replitminesweeperserver.messages.*;
import kyleberkof.replitminesweeperserver.messages.ChunkMessage;
import kyleberkof.replitminesweeperserver.messages.RequestChunkMessage;
import kyleberkof.replitminesweeperserver.minesweeperengine.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler implements Runnable {
    private static final int CHUNK_REQUEST = 1;
    private static final int CHUNK_RESPONSE = 2;
    private static final int TILE_UPDATE = 3;
    private static final int FULL_TILE_UPDATE = 4;
    private static final int ENTITY_UPDATE = 5;
    private static final int CHUNK_UNLOAD = 6;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private MinesweeperServer server;

    private World world;

    private PlayerEntity player;

    private ArrayList<Vector2Int> loadedChunks;

    private boolean disconnect = false;

    public ConnectionHandler(MinesweeperServer server, Socket socket, PlayerEntity player, World world) {
        this.server = server;
        this.socket = socket;
        this.player = player;
        this.world = world;

        loadedChunks = new ArrayList<>();
    }

    public void sendChunk(Vector2Int chunkPosition, Chunk chunk) {
        ChunkMessage message = new ChunkMessage(chunk, chunkPosition);

        try {
            outputStream.write(message.getByteArray(), 0, message.getByteArray().length);
        } catch(IOException e) {
            e.printStackTrace();
            disconnect = true;
        }
    }

    public void sendEntities(List<Entity> entities) {
        EntityUpdateMessage message = new EntityUpdateMessage(entities);

        try {
            outputStream.write(message.getByteArray(), 0, message.getByteArray().length);
        } catch(IOException e) {
            e.printStackTrace();
            disconnect = true;
        }
    }

    public void sendTiles(Vector2Int chunkPosition, List<Vector2Int> tilePositions, List<Tile> tiles) {
        TileUpdateMessage message = new TileUpdateMessage(chunkPosition, tilePositions, tiles);

        try {
            outputStream.write(message.getByteArray(), 0, message.getByteArray().length);
        } catch(IOException e) {
            e.printStackTrace();
            disconnect = true;
        }
    }

    public void sendFullTiles(Vector2Int chunkPosition, Tile[][] tiles) {
        FullTileUpdateMessage message = new FullTileUpdateMessage(chunkPosition, tiles);

        try {
            outputStream.write(message.getByteArray(), 0, message.getByteArray().length);
        } catch(IOException e) {
            e.printStackTrace();
            disconnect = true;
        }
    }

    @Override
    public void run() {
        // takes input from the client socket
        try {
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outputStream = new DataOutputStream(socket.getOutputStream());

            /*
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
             */

            while(!disconnect) {
                if(inputStream.available() > 0) {
                    int messageLength = inputStream.readInt();

                    byte[] messageBytes = inputStream.readNBytes(messageLength);

                    byte header = messageBytes[0]; //If the header is not the header then is it really the header? Maybe it should have been called a type or something.

                    switch(header) {
                        case CHUNK_REQUEST: {
                            //System.out.println("Chunk request");
                            RequestChunkMessage chunkRequest = new RequestChunkMessage(messageBytes);

                            Vector2Int chunkPosition = chunkRequest.getChunkPosition();

                            ChunkMessage response = new ChunkMessage(world.getChunk(chunkPosition), chunkPosition);
                            outputStream.write(response.getByteArray());

                            loadedChunks.add(chunkPosition);
                        }

                            break;

                        case CHUNK_RESPONSE:
                        { //Scopes are very nice to have
                            ChunkMessage message = new ChunkMessage(messageBytes, world);
                            //System.out.println("Chunk position: " + message.getChunkPosition());

                            world.setChunk(message.getChunkPosition(), message.getChunk());
                        }

                        break;

                        case TILE_UPDATE:
                        {
                            //System.out.println("Tile update");
                            TileUpdateMessage message = new TileUpdateMessage(messageBytes, world);

                            Chunk chunk = world.getChunk(message.getChunkPosition());
                            for(int i = 0; i < message.getTilePositions().size(); i ++) {
                                Vector2Int position = message.getTilePositions().get(i);

                                chunk.getTiles()[position.x][position.y] = message.getTiles().get(i);
                            }

                            for(int i = 0; i < server.getConnectionHandlers().size(); i ++) {
                                ConnectionHandler handler = server.getConnectionHandlers().get(i);

                                if(handler != this) {
                                    handler.getSocket().getOutputStream().write(message.getByteArray());
                                }
                            }
                        }

                        break;

                        case FULL_TILE_UPDATE:
                        {
                            //System.out.println("Full tile update");
                            FullTileUpdateMessage message = new FullTileUpdateMessage(messageBytes, world);

                            Chunk chunk = world.getChunk(message.getChunkPosition());
                            chunk.setTiles(message.getTiles());

                            for(int i = 0; i < server.getConnectionHandlers().size(); i ++) {
                                ConnectionHandler handler = server.getConnectionHandlers().get(i);

                                if(handler != this) {
                                    handler.getSocket().getOutputStream().write(message.getByteArray());
                                }
                            }
                        }
                        break;

                        case ENTITY_UPDATE:
                        {
                            EntityUpdateMessage message = new EntityUpdateMessage(messageBytes, world);

                            for(Entity entity : message.getEntities()) {
                                Entity oldEntity = null; //Go on a quest to find previous version of entity and delete it. This process seems like it is not very efficient.
                                baseLoop: for(ArrayList<Chunk> yChunks : world.getChunks()) {
                                    for(Chunk chunk : yChunks) {
                                        if(chunk != null) {
                                            for(int i = 0; i < chunk.getEntities().size(); i ++) {
                                                Entity ent = chunk.getEntities().get(i);

                                                if(ent.getId() == entity.getId()) {
                                                    oldEntity = ent;
                                                    break;
                                                }
                                            }

                                            if(oldEntity != null) {
                                                chunk.removeEntity(oldEntity);
                                                break baseLoop;
                                            }
                                        }
                                    }
                                }

                                world.getChunk(World.getBlockChunkPosition(entity.position)).addEntity(entity);
                            }

                            for(int i = 0; i < server.getConnectionHandlers().size(); i ++) {
                                ConnectionHandler handler = server.getConnectionHandlers().get(i);

                                if(handler != this) {
                                    handler.getSocket().getOutputStream().write(message.getByteArray());
                                }
                            }
                        }
                        break;

                        case CHUNK_UNLOAD:
                        {
                            ChunkUnloadMessage message = new ChunkUnloadMessage(messageBytes);
                            loadedChunks.remove(message.getChunkPosition());
                        }
                        break;

                        default:
                            System.out.println("Unknown header " + header);
                            outputStream.writeInt(0);
                            break;
                            /*
                        case CHUNK_REQUEST:
                            //System.out.println("Chunk request");
                            RequestChunkMessage chunkRequest = new RequestChunkMessage(messageBytes);

                            Vector2Int chunkPosition = chunkRequest.getChunkPosition();

                            ChunkMessage response = new ChunkMessage(world.getChunk(chunkPosition), chunkPosition);
                            outputStream.write(response.getByteArray());

                            break;

                        case CHUNK_RESPONSE:
                            ChunkMessage message = new ChunkMessage(messageBytes, world);
                            world.setChunk(message.getChunkPosition(), message.getChunk());

                            break;

                        default:
                            outputStream.write(0); //0 byte means that there is no response for message
                            break;
                             */
                    }
                }
            }

            System.out.println("Closed connection with " + socket.getInetAddress());

            // close connection
            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public ArrayList<Vector2Int> getLoadedChunks() {
        return loadedChunks;
    }
}
