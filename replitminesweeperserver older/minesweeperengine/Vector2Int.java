package kyleberkof.replitminesweeperserver.minesweeperengine;

public class Vector2Int {
  public int x;
  public int y;

  public Vector2Int(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Vector2Int multiply(Vector2Int factor) {
    return new Vector2Int(x * factor.x, y * factor.y);
  }

  public Vector2Int add(Vector2Int addend) {
    return new Vector2Int(addend.x + x, addend.y + y);
  }

  public Vector2Int subtract(Vector2Int subtrahend) { //What a fun name
    return new Vector2Int(x - subtrahend.x, y - subtrahend.y);
  }

  public boolean equals(Object vec) {
    return x == ((Vector2Int) vec).x && y == ((Vector2Int) vec).y;
  }

  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}