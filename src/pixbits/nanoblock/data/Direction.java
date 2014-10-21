package pixbits.nanoblock.data;

public enum Direction
{
  NORTH(0,-1),
  SOUTH(0,1),
  EAST(1,0),
  WEST(-1,0)
 ;
  
  public final int x;
  public final int y;
  
  Direction(int x, int y)
  {
    this.x = x;
    this.y = y;
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
