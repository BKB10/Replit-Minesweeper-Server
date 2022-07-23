package kyleberkof.replitminesweeperserver.minesweeperengine;

public class Tile {
  public static final int LEVEL_LIMIT = 128;

  private Tile[] neighbors;
  private boolean hasBomb;
  private boolean flagged = false;
  private boolean isCleared = false;
  private int bombCount;

  private Vector2Int position;

  private World world;

  public Tile(boolean hasBomb, World world) {
    this.hasBomb = hasBomb;
    this.world = world;
  }

  public Tile(boolean hasBomb, boolean isFlagged, boolean isCleared, World world) {
    this.hasBomb = hasBomb;
    this.flagged = isFlagged;
    this.isCleared = isCleared;
    this.world = world;
  }

  public void initialize(Vector2Int position) {
    this.position = position;

    neighbors = new Tile[8];
    //Put corners first then non-corners second (this seems to have not actually been needed)
    /*
    neighbors[0] = (x - 1 >= 0 && y - 1 >= 0) ? tiles[x - 1][y - 1] : null;
    neighbors[4] = y - 1 >= 0 ? tiles[x][y - 1] : null;
    neighbors[1] = (x + 1 < tiles.length && y - 1 >= 0) ? tiles[x + 1][y - 1] : null;
    neighbors[5] = x - 1 >= 0 ? tiles[x - 1][y] : null;
    neighbors[6] = x + 1 < tiles.length ? tiles[x + 1][y] : null;
    neighbors[2] = (x - 1 >= 0 && y + 1 < tiles[0].length) ? tiles[x - 1][y + 1] : null;
    neighbors[7] = y + 1 < tiles[0].length ? tiles[x][y + 1] : null;
    neighbors[3] = (x + 1 < tiles.length && y + 1 < tiles[0].length) ? tiles[x + 1][y + 1] : null;
    */

    neighbors[0] = world.getTileAtPosition(position.add(new Vector2Int(-1, -1)));
    neighbors[4] = world.getTileAtPosition(position.add(new Vector2Int(0, -1)));
    neighbors[1] = world.getTileAtPosition(position.add(new Vector2Int(1, -1)));
    neighbors[5] = world.getTileAtPosition(position.add(new Vector2Int(-1, 0)));
    neighbors[6] = world.getTileAtPosition(position.add(new Vector2Int(1, 0)));
    neighbors[2] = world.getTileAtPosition(position.add(new Vector2Int(-1, 1)));
    neighbors[7] = world.getTileAtPosition(position.add(new Vector2Int(0, 1)));
    neighbors[3] = world.getTileAtPosition(position.add(new Vector2Int(1, 1)));

    bombCount = 0;
    for(Tile tile : neighbors) {
      bombCount += (tile != null && tile.hasBomb()) ? 1 : 0;
    }
    bombCount += hasBomb() ? 1 : 0;
  }

  public void clear() {
    recursiveClear(bombCount > 0, 0);
  }

  private void recursiveClear(boolean breakClear, int level) {
    if(!isCleared) {
      isCleared = true;

      Vector2Int chunkPos = World.getBlockChunkPosition(new Vector2Float(position.x, position.y));
      world.addTileToUpdate(chunkPos, position.subtract(chunkPos), this); //Using the divide method should round down because its an int

      /*
      double difference = timer.getTimeDifferenceMilliseconds();
      if(difference < 3000.0 / (Main.game.getWidth() * Main.game.getHeight())) {
        try {
          Thread.sleep((int) Math.max(3000.0 / (Main.game.getWidth() * Main.game.getHeight()) - difference, 1.0)); //We want to take at most 2 seconds to clear
        } catch(InterruptedException e) {
          e.printStackTrace();
        }
      } else {
        timer.resetDifference();
      }
      */

      if(bombCount == 0) {
        for(int i = 0; i < neighbors.length; i ++) {
          Tile neighbor = neighbors[i];
          if(neighbor != null && !breakClear && level < LEVEL_LIMIT) {
            neighbor.recursiveClear(neighbor.getBombCount() > 0, level + 1);
          }
        }
      }
    }
  }

  public int getBombCount() {
    return bombCount;
  }

  public void toggleFlag() {
    flagged = !flagged;
  }

  public boolean isFlagged() {
    return flagged;
  }

  public boolean hasBomb() {
    return hasBomb;
  }

  public boolean isCleared() {
    return isCleared;
  }
}