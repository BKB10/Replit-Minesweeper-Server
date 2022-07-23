package kyleberkof.replitminesweeperserver.minesweeperengine;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

public abstract class GameMode {
  public static enum State { PLAY, WIN, LOSE };

  protected State state;
  protected Timer timer;

  protected World world;

  protected MinesweeperRenderer renderer;

  GameMode(World world, MinesweeperRenderer renderer) {
    this.world = world;
    this.renderer = renderer;

    state = State.PLAY;
    timer = new Timer();
  }

  public abstract void onMouseButtonPress(MouseEvent e);

  public abstract void onMouseButtonRelease(MouseEvent e);

  public abstract void onKeyboardPress(KeyEvent e);

  public abstract void onKeyboardRelease(KeyEvent e);

  public abstract void tick(float timeMultiplier);

  public World getWorld() {
    return world;
  }

  public State getState() {
    return state;
  }

  public double getTime() {
    return timer.getCurrentTime();
  }
}