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
}
