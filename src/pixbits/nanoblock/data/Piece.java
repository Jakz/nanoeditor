package pixbits.nanoblock.data;

public class Piece
{
  public final PieceType type;
  public final int x, y;
  
  public Piece(PieceType type, int x, int y)
  {
    this.type = type;
    this.x = x;
    this.y = y;
  }
  
  public boolean equals(Object o)
  {
    if (o != null && o instanceof Piece)
    {
      Piece p = (Piece)o;
      return p.x == x && p.y == y && p.type == type;
    }
    return false;
  }
}
