package pixbits.nanoblock.data;

public class PiecePart
{
  public final int x;
  public final int y;
  public final boolean lastSouth, lastEast;
  
  PiecePart(int x, int y, boolean lastSouth, boolean lastEast)
  {
    this.x = x;
    this.y = y;
    this.lastSouth = lastSouth;
    this.lastEast = lastEast;
  }
}
