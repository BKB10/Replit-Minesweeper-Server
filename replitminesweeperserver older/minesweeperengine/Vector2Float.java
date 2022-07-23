package kyleberkof.replitminesweeperserver.minesweeperengine;

public class Vector2Float {
  public float x;
  public float y;

  public Vector2Float(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vector2Float multiply(Vector2Float factor) {
    return new Vector2Float(x * factor.x, y * factor.y);
  }

  public Vector2Float add(Vector2Float addend) {
    return new Vector2Float(addend.x + x, addend.y + y);
  }

  public Vector2Float subtract(Vector2Float subtrahend) { //What a fun name
    return new Vector2Float(x - subtrahend.x, y - subtrahend.y);
  }

  public boolean equals(Object vec) {
    return x == ((Vector2Float) vec).x && y == ((Vector2Float) vec).y;
  }

  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}