package kyleberkof.replitminesweeperserver.minesweeperengine;

import kyleberkof.replitminesweeperserver.ConnectionHandler;
import kyleberkof.replitminesweeperserver.MinesweeperServer;

import java.util.*;

public class World {
  //The chunks can be dynamically allocated for multiplayer loading chunks from server
  private ArrayList<ArrayList<Chunk>> chunks;

  private ArrayList<Integer> chunkArrayOriginsY;

  private List<Entity> entitiesToUpdate;

  private List<Vector2Int> keys;
  private Map<Vector2Int, List<Vector2Int>> tilesToUpdatePositions;
  private Map<Vector2Int, List<Tile>> tilesToUpdate;

  private int currentEntityId;

  private int chunkArrayOriginX;

  private MinesweeperServer server;

  private boolean autoGenerate;

  public World(boolean autoGenerate) {
    chunks = new ArrayList<ArrayList<Chunk>>();
    chunkArrayOriginX = 0;
    chunkArrayOriginsY = new ArrayList<Integer>();

    this.autoGenerate = autoGenerate;

    entitiesToUpdate = Collections.synchronizedList(new ArrayList<Entity>());
    keys = Collections.synchronizedList(new ArrayList<Vector2Int>());
    tilesToUpdatePositions = Collections.synchronizedMap(new HashMap<Vector2Int, List<Vector2Int>>());
    tilesToUpdate = Collections.synchronizedMap(new HashMap<Vector2Int, List<Tile>>());
  }

  public void setServer(MinesweeperServer server) {
    this.server = server;
  }

  private Vector2Int getEquivilantKey(Vector2Int chunkPos) {
    for(int i = 0; i < keys.size(); i ++) {
      if(keys.get(i).equals(chunkPos)) {
        return keys.get(i);
      }
    }

    return null;
  }

  private Vector2Int updateMaps(Vector2Int chunkPos) {
    Vector2Int key = getEquivilantKey(chunkPos);

    if(key == null) {
      tilesToUpdate.put(chunkPos, Collections.synchronizedList(new ArrayList<Tile>()));
      tilesToUpdatePositions.put(chunkPos, Collections.synchronizedList(new ArrayList<Vector2Int>()));

      keys.add(chunkPos);

      return chunkPos;
    }

    return key;
  }

  public void addEntityToUpdate(Entity entity) {
    if(server != null) {
      entitiesToUpdate.add(entity);
    }
  }

  public void addTileToUpdate(Vector2Int chunkPos, Vector2Int tilePos, Tile tile) {
    if(server != null) {
      Vector2Int key = updateMaps(chunkPos);
      tilesToUpdatePositions.get(key).add(tilePos);
      tilesToUpdate.get(key).add(tile);
    }
  }

  public void sendUpdates() {
    if(server != null) {
      //client.sendChunk(); or client.sendTiles() / client.sendEntities();
      server.sendEntities(entitiesToUpdate);

      for(int i = 0; i < keys.size(); i ++) {
        Vector2Int chunkPos = keys.get(i);

        List<Tile> tiles = tilesToUpdate.get(chunkPos);
        List<Vector2Int> positions = tilesToUpdatePositions.get(chunkPos);
        if(tiles.size() <= 51) { //It would take more bytes to send 52 individual tiles then to send a whole chunk of tiles
          server.sendTiles(chunkPos, positions, tiles);
        } else {
          server.sendFullTiles(chunkPos, getChunk(chunkPos).getTiles());
        }
      }

      tilesToUpdate.clear();
      tilesToUpdatePositions.clear();
      keys.clear();
    }
  }

  //Chunk position input is real position of chunks, not position relative to array origin
  public boolean chunkPositionExists(Vector2Int chunkPosition) {
    //System.out.println("chunk exists " + chunkPosition);
    if(chunkPosition.x - chunkArrayOriginX >= chunkArrayOriginsY.size() || chunkPosition.x - chunkArrayOriginX < 0) {
      return false;
    }
    Vector2Int arrayPos = chunkPosition.subtract(new Vector2Int(chunkArrayOriginX, chunkArrayOriginsY.get(chunkPosition.x - chunkArrayOriginX)));

    //System.out.println("testin: " + chunkArrayOriginsY.get(chunkPosition.x - chunkArrayOriginX));

    //System.out.println("looking into position " + arrayPos);
    //System.out.println("chunks size: " + chunks.size() + "; x origin: " + chunkArrayOriginX + "; y origins size: " + chunkArrayOriginsY.size());
    //System.out.println("chunk position exists " + chunkPosition);
    return arrayPos.x >= 0 && arrayPos.y >= 0 && arrayPos.x < chunks.size() && arrayPos.y < chunks.get(arrayPos.x).size() && chunks.get(arrayPos.x).get(arrayPos.y) != null;

    //return chunkPosition.x >= chunkArrayOriginX && chunkPosition.x - chunkArrayOriginX < chunks.size() && chunkPosition.y >= chunkArrayOriginsY.get(chunkPosition.x - chunkArrayOriginX) && chunkPosition.y - chunkArrayOriginsY.get(chunkPosition.x - chunkArrayOriginX) < chunks.get(chunkPosition.x - chunkArrayOriginX).size() && chunks.get(chunkPosition.x - chunkArrayOriginX).get(chunkPosition.y - chunkArrayOriginsY.get(chunkPosition.x - chunkArrayOriginX)) != null;
  }

  public void tick(float timeMultiplier) {
    for(int cx = 0; cx < chunks.size(); cx ++) {
      for(int cy = 0; cy < chunks.get(cx).size(); cy ++) {
        Chunk chunk = chunks.get(cx).get(cy);

        if(chunk != null) {
          chunk.tick(new Vector2Int(cx + chunkArrayOriginX, cy + chunkArrayOriginsY.get(cx)), this, timeMultiplier);
        }
      }
    }

    sendUpdates();
  }

  private Vector2Int getChunkArrayPosition(Vector2Int chunkPosition) {
    Vector2Int arrayPos = chunkPosition.subtract(new Vector2Int(chunkArrayOriginX, 0));

    //IF THERE ARE ANY WEIRD ERRORS WITH THIS, IT MIGHT BE WITH THE FACT THAT THE ARRAYS FOR Y COORDINATES ARE NOT ALL INCREASED IN SIZE

    //Either add stuff onto the end or push stuff up then add stuff at the beginning
    if(arrayPos.x >= 0) {
      while(chunks.size() <= arrayPos.x) {
        chunks.add(new ArrayList<Chunk>());
        chunkArrayOriginsY.add(0);
        //while(chunks.get(chunkPosition.x).size() <= chunkPosition.y) {
        //  chunks.get(chunks.size() - 1).add(null);
        //}
      }
    } else {
      //System.out.println("Doing x origin stuff");
      int xDifference = -arrayPos.x;

      for(int i = 0; i < xDifference; i ++) {
        chunks.add(null);
        chunkArrayOriginsY.add(0);
      }

      //Move stuff in array up to make room for chunks at start
      for(int i = chunks.size() - xDifference - 1; i >= 0; i --) {
        chunks.set(i + xDifference, chunks.get(i));
        chunkArrayOriginsY.set(i + xDifference, chunkArrayOriginsY.get(i));
      }

      for(int i = 0; i < xDifference; i ++) {
        chunks.set(i, new ArrayList<Chunk>());
        chunkArrayOriginsY.set(i, 0);
      }

      chunkArrayOriginX = chunkPosition.x;
    }

    arrayPos = chunkPosition.subtract(new Vector2Int(chunkArrayOriginX, chunkArrayOriginsY.get(chunkPosition.x - chunkArrayOriginX)));

    if(arrayPos.y >= 0) {
      while(chunks.get(arrayPos.x).size() <= arrayPos.y) {
        chunks.get(arrayPos.x).add(null);
        //while(chunks.get(chunkPosition.x).size() <= chunkPosition.y) {
        //  chunks.get(chunks.size() - 1).add(null);
        //}
      }
    } else {
      //System.out.println("Dooing y origin stuff");
      int yDifference = -arrayPos.y;

      //System.out.println("Array will go from size " + chunks.get(arrayPos.x).size() + " to size " + (chunks.get(arrayPos.x).size() + yDifference));
      for(int i = 0; i < yDifference; i ++) {
        chunks.get(arrayPos.x).add(null);
      }

      //Move stuff in array up to make room for chunks at start
      for(int i = chunks.get(arrayPos.x).size() - yDifference - 1; i >= 0; i --) {
        //System.out.println("moving index " + i + " to " + (i + yDifference));
        chunks.get(arrayPos.x).set(i + yDifference, chunks.get(arrayPos.x).get(i));
      }

      for(int i = 0; i < yDifference; i ++) {
        //System.out.println("index " + i + " is now blank");
        chunks.get(arrayPos.x).set(i, null);
      }

      chunkArrayOriginsY.set(arrayPos.x, chunkPosition.y);

      //System.out.println("array origin is now " + chunkArrayOrigin);
      //System.out.println("new array of y things: " + chunks.get(arrayPos.x));
    }
    arrayPos = chunkPosition.subtract(new Vector2Int(chunkArrayOriginX, chunkArrayOriginsY.get(chunkPosition.x - chunkArrayOriginX)));

    return arrayPos;
  }

  public void addEntity(Entity entity) {
    Chunk chunk = getChunk(World.getBlockChunkPosition(entity.position));
    entity.setId(currentEntityId++);
    chunk.addEntity(entity);
  }

  public void removeEntity(Entity entity) {
    Chunk chunk = getChunk(World.getBlockChunkPosition(entity.position));
    chunk.removeEntity(entity);
  }

  public void setChunk(Vector2Int position, Chunk chunk) {
    Vector2Int arrayPos = getChunkArrayPosition(position);

    chunks.get(arrayPos.x).set(arrayPos.y, chunk);
  }

  public Chunk getChunk(Vector2Int chunkPosition) {
    //System.out.println("get chunk " + chunkPosition);
    //Position in the array converted from position in the world
    //System.out.println("x size: " + chunks.size() + ", y origins size: " + chunkArrayOriginsY.size() + ";");

    //allocates space for chunk and gets position of it in array
    Vector2Int arrayPos = getChunkArrayPosition(chunkPosition);
    
    if(chunks.get(arrayPos.x).get(arrayPos.y) == null) {
      Chunk chunk = new Chunk();
      chunks.get(arrayPos.x).set(arrayPos.y, chunk);

      if(autoGenerate) {
        chunk.generate(this, chunkPosition);

        //Update bomb neighbor counts
        for(int x = -1; x <= 1; x ++) {
          for(int y = -1; y <= 1; y ++) {
            Vector2Int direction = new Vector2Int(x, y);
            Vector2Int position = chunkPosition.add(direction);
            //System.out.println("cp: " + chunkPosition + "; newcp: " + position + ";");
            Vector2Int arrPos = getChunkArrayPosition(position);
            //Vector2Int arrPos = arrayPos.add(direction);
            if(chunkPositionExists(position)) { //null pointer exception on chunks.get(arrpos....). maybe it's a problem with chunkPositionExists not being accurate? just take a clear headed look through what the code does and see if any problems can be found first
              //System.out.println("cxs: " + chunks.size() + "; cx: " + arrPos.x);
              //System.out.println("cys: " + chunks.get(arrPos.x).size() + "; cy: " + arrPos.y);
              //System.out.println("xo: " + chunkArrayOriginX + "; yo: " + chunkArrayOriginsY.get(chunkPosition.x - chunkArrayOriginX) + "; ot: " + chunkArrayOriginsY.get(position.x - chunkArrayOriginX) + ";");
              //System.out.println("newap: " + arrPos + "; ap: " + arrayPos + ";");
              chunks.get(arrPos.x).get(arrPos.y).updateNeighbors(position);
            }
          }
        }
      }

      return chunk;
    } else {
      return chunks.get(arrayPos.x).get(arrayPos.y);
    }
  }

  public Tile getTileAtPosition(Vector2Int position) {
    int chunkX = (int) Math.floor((double) position.x / Chunk.CHUNK_SIZE); //NOTE: this is still unfinished
    int chunkY = (int) Math.floor((double) position.y / Chunk.CHUNK_SIZE);
    //System.out.println("Test: " + chunkX + "; " + chunkY + "; " + (chunkX - chunkArrayOrigin.x) + "; " + (chunkY - chunkArrayOrigin.y) + "; " + chunkArrayOrigin + ";");
    return chunkPositionExists(new Vector2Int(chunkX, chunkY)) ? chunks.get(chunkX - chunkArrayOriginX).get(chunkY - chunkArrayOriginsY.get(chunkX - chunkArrayOriginX)).getTiles()[position.x - chunkX * Chunk.CHUNK_SIZE][position.y - chunkY * Chunk.CHUNK_SIZE] : null; //return null if out of range of chunks
  }

  public ArrayList<ArrayList<Chunk>> getChunks() {
    return chunks;
  }

  public static Vector2Int getBlockChunkPosition(Vector2Float blockPos) {
    return new Vector2Int((int) Math.floor(blockPos.x / Chunk.CHUNK_SIZE), (int) Math.floor(blockPos.y / Chunk.CHUNK_SIZE));
  }
}