package pixbits.nanoblock.data;

public enum Direction
{
  NORTH ( 0, -1, 2),
  SOUTH ( 0,  1, 8),
  EAST  ( 1,  0, 4),
  WEST  (-1,  0, 1)
 ;
  
  public final int x;
  public final int y;
  public final int mask;
  
  Direction(int x, int y, int mask)
  {
    this.x = x;
    this.y = y;
    this.mask = mask;
  }
  
  public Direction flipped()
  {
    switch (this)
    {
      case NORTH: return SOUTH;
      case SOUTH: return NORTH;
      case EAST: return WEST;
      case WEST: return EAST;
      default: return null;
    }
  }
}
