package kyleberkof.replitminesweeperserver.minesweeperengine;

public class Timer {
  private long startTime;
  private long endTime;

  private boolean running;

  public Timer() {
    running = false;
  }

  public void start() {
    endTime = 0;
    running = true;
    startTime = System.currentTimeMillis();
  }

  public void stop() {
    running = false;
    endTime = System.currentTimeMillis();
  }

  public double getCurrentTime() {
    return ((endTime > 0 ? endTime : System.currentTimeMillis()) - startTime) / 1000.0;
  }

  public boolean isRunning() {
    return running;
  }
}