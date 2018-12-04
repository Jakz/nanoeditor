package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.PieceOutline;

public class PieceHover
{
  public final int x, y;
  public final PieceOutline outline;
  
  public PieceHover(int x, int y, PieceOutline outline)
  {
    this.x = x;
    this.y = y;
    this.outline = outline;
  }
  
  public boolean equals(PieceHover other)
  {
    return x == other.x && y == other.y && outline == other.outline;
  }
}
