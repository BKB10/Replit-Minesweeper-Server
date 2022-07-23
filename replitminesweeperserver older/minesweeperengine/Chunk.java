package kyleberkof.replitminesweeperserver.minesweeperengine;

import java.util.ArrayList;

public class Chunk {
  public static final int CHUNK_SIZE = 16;

  private Tile[][] tiles;

  private ArrayList<Entity> entities;

  public Chunk() {
    tiles = new Tile[CHUNK_SIZE][CHUNK_SIZE];
    entities = new ArrayList<Entity>();
  }

  public Chunk(Tile[][] tiles, ArrayList<Entity> entities) {
    this.tiles = tiles;
    this.entities = entities;
  }

  //MAKE BOMB GENERATION BASED ON SMOOTH DENSITY NOISE MAP

  public void generate(World world, Vector2Int chunkPosition) {
    int bombCount = 16;

    //Randomly make array of positions to have bombs
    ArrayList<Vector2Int> bombPositions = new ArrayList<>();
    for(int i = 0; i < bombCount; i ++) {
      Vector2Int pos = new Vector2Int(Utils.random.nextInt(tiles.length), Utils.random.nextInt(tiles[0].length));
      while(bombPositions.contains(pos)) {
        pos = new Vector2Int(Utils.random.nextInt(tiles.length), Utils.random.nextInt(tiles[0].length));
      }

      bombPositions.add(pos);
    }

    //Makes tiles at positions that were chosen to have bombs
    for(Vector2Int pos : bombPositions) {
      tiles[pos.x][pos.y] = new Tile(true, world);
    }

    //Put in tiles that were not made to have bombs
    for(int x = 0; x < tiles.length; x ++) {
      for(int y = 0; y < tiles[0].length; y ++) {
        if(tiles[x][y] == null) {
          tiles[x][y] = new Tile(false, world);
        }
      }
    }
  }

  //Neighbors need to be found after the chunks are generated
  public void updateNeighbors(Vector2Int chunkPosition) {
    //Initialize all the tiles (find neighbors)
    for(int x = 0; x < tiles.length; x ++) {
      for(int y = 0; y < tiles[0].length; y ++) {
        Tile tile = tiles[x][y];

        if(tile != null) {
          tile.initialize(chunkPosition.multiply(new Vector2Int(CHUNK_SIZE, CHUNK_SIZE)).add(new Vector2Int(x, y)));
        }
      }
    }
  }

  public void tick(Vector2Int chunkPosition, World world, float timeMultiplier) {
    for(int i = 0; i < entities.size(); i ++) {
      Entity entity = entities.get(i);

      entity.tick(world, timeMultiplier);
      updateEntityChunk(chunkPosition, world, entity);
    }
  }

  public void updateEntityChunk(Vector2Int chunkPosition, World world, Entity entity) {
    Chunk chunk = world.getChunk(World.getBlockChunkPosition(entity.position));
    //System.out.println("pos: " + entity.position + "; chunk pos: " + World.getBlockChunkPosition(entity.position) + ";");

    if(chunk != this) {
      System.out.println("Moved entity to chunk " + World.getBlockChunkPosition(entity.position));
      removeEntity(entity);
      chunk.addEntity(entity);
    }
  }

  public void addEntity(Entity entity) {
    entities.add(entity);
  }

  public void removeEntity(Entity entity) {
    entities.remove(entity);
  }

  public void getEntityInBounds(Vector2Float position, Vector2Float scale) {

  }

  public Tile[][] getTiles() {
    return tiles;
  }

  public ArrayList<Entity> getEntities() {
    return entities;
  }
}