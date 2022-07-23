package kyleberkof.replitminesweeperserver.minesweeperengine;

public class Ticker implements Runnable {
  private GameMode game;
  private int tps;

  private boolean stop;

  public Ticker(GameMode game, int tps) {
    this.game = game;
    this.tps = tps;

    stop = false;
  }

  public void run() {
    startTicking();
  }

  public void startTicking() {
    float timeMultiplier = 0;

    long currentTime = System.currentTimeMillis();
    long lastTime = currentTime;

    float secondsPerTick = 1.0f / tps;
    float msPerTick = secondsPerTick * 1000;
    try {
      while(!stop) {
        currentTime = System.currentTimeMillis();
        timeMultiplier = (currentTime - lastTime) / msPerTick;
        tick(timeMultiplier);

        lastTime = currentTime;

        long tickingTime = (System.currentTimeMillis() - currentTime);
        if(msPerTick > tickingTime) {
          Thread.sleep((long) (msPerTick - tickingTime)); //Sleep for the extra time that we have after ticking but don't sleep if we are past the ideal ticking time
        }
      }
    } catch(InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    stop = true;
  }

  private void tick(float timeMultiplier) {
    game.tick(timeMultiplier);
  }
}