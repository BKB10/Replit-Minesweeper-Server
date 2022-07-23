package kyleberkof.replitminesweeperserver.minesweeperengine;

import java.util.ArrayList;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.*;

public class TestingMode extends GameMode {
  private InputManager inputManager;

  //private World world;

  /*
  private void makeTiles() {
    //Randomly make array of positions to have bombs
    ArrayList<Vector2> bombPositions = new ArrayList<>();
    for(int i = 0; i < bombCount; i ++) {
      Vector2 pos = new Vector2(Utils.random.nextInt(tiles.length), Utils.random.nextInt(tiles[0].length));
      while(bombPositions.contains(pos)) {
        pos = new Vector2(Utils.random.nextInt(tiles.length), Utils.random.nextInt(tiles[0].length));
      }

      bombPositions.add(pos);
    }

    //Makes tiles at positions that were chosen to have bombs
    for(Vector2 pos : bombPositions) {
      tiles[pos.x][pos.y] = new Tile(true, timer);
    }

    //Put in tiles that were not made to have bombs
    for(int x = 0; x < tiles.length; x ++) {
      for(int y = 0; y < tiles[0].length; y ++) {
        if(tiles[x][y] == null) {
          tiles[x][y] = new Tile(false, timer);
        }
      }
    }

    //Initialize all the tiles (find neighbors)
    for(int x = 0; x < tiles.length; x ++) {
      for(int y = 0; y < tiles[0].length; y ++) {
        tiles[x][y].initialize(tiles, x, y);
      }
    }
  }
  */

  public TestingMode(MinesweeperRenderer renderer, InputManager inputManager) {
    super(new World(true), renderer);

    this.inputManager = inputManager;

    world.addEntity(new PlayerEntity(new Vector2Float(0, 0), new Color(0, 255, 0, 150)));

    //tiles = new Tile[width][height];
    //makeTiles();

    //world = new World(true);

    //world.getChunk(new Vector2Int(0, 0));
    //world.getChunk(new Vector2Int(1, 0));
  }

  public void clearTile(Vector2Int position) {
    if(!timer.isRunning()) {
      timer.start();
    }

    Tile tile = world.getTileAtPosition(new Vector2Int(position.x, position.y));

    //System.out.println("testing: " + position);
    if(tile != null) {
      tile.clear();
      //tiles[position.x][position.y].clear();

      if(tile.hasBomb() && tile.isCleared()) {
        state = State.LOSE;
        timer.stop();
      }
    }

    /*
    for(Tile[] yTiles : tiles) {
      for(Tile tile : yTiles) {
        if(tile.hasBomb() && tile.isCleared()) {
          state = State.LOSE;
          timer.stop();
          return;
        }
      }
    }

    for(Tile[] yTiles : tiles) {
      for(Tile tile : yTiles) {
        if(!tile.hasBomb() && !tile.isCleared()) {
          return;
        }
      }
    }
    timer.stop();
    state = State.WIN;
    */
  }

  public void setFlag(Vector2Int position) {
    if(!timer.isRunning()) {
      timer.start();
    }

    world.getTileAtPosition(position).toggleFlag();

    //tiles[position.x][position.y].toggleFlag();
  }

  public void reset() {
    //tiles = new Tile[tiles.length][tiles[0].length];
    //makeTiles();
    //world = new World(false);
    //world.getChunk(new Vector2Int(0, 0));
    //generateTiles();

    state = State.PLAY;
  }

  public void tick(float timeMultiplier) {
    if(inputManager.upPressed) {
      renderer.setCameraPosition(renderer.getCameraPosition().add(new Vector2Float(0, -0.125f * timeMultiplier)));
    }

    if(inputManager.downPressed) {
      renderer.setCameraPosition(renderer.getCameraPosition().add(new Vector2Float(0, 0.125f * timeMultiplier)));
    }

    if(inputManager.leftPressed) {
      renderer.setCameraPosition(renderer.getCameraPosition().add(new Vector2Float(-0.125f * timeMultiplier, 0)));
    }

    if(inputManager.rightPressed) {
      renderer.setCameraPosition(renderer.getCameraPosition().add(new Vector2Float(0.125f * timeMultiplier, 0)));
    }

    if(inputManager.zoomInPressed) {
      renderer.setCameraScale(renderer.getCameraScale().multiply(new Vector2Float(1.025f, 1.025f)));
    }

    if(inputManager.zoomOutPressed) {
      renderer.setCameraScale(renderer.getCameraScale().multiply(new Vector2Float(0.975f, 0.975f)));
    }

    world.tick(timeMultiplier);
  }

  public void onMouseButtonPress(MouseEvent e) {
    Point mousePosition = MouseInfo.getPointerInfo().getLocation();

    if(e.getButton() == MouseEvent.BUTTON1) { //left
      if(state == State.PLAY) {
        clearTile(renderer.getTilePosition(mousePosition));
      } else {
        reset();
      }
      //render();
    } else if(e.getButton() == MouseEvent.BUTTON3) { //right
      if(state == State.PLAY) {
        setFlag(renderer.getTilePosition(mousePosition));
      }
      //render();
    }
  }

  public void onMouseButtonRelease(MouseEvent e) {
    
  }

  public void onKeyboardPress(KeyEvent e) {
    
  }

  public void onKeyboardRelease(KeyEvent e) {

  }
}