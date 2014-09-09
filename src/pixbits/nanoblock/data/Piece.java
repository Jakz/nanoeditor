package pixbits.nanoblock.data;

public class Piece
{
  public final PieceType type;
  public final PieceColor color;
  public int x, y;
  
  public Piece(PieceType type, PieceColor color, int x, int y)
  {
    this.type = type;
    this.color = color;
    this.x = x;
    this.y = y;
  }
  
  public boolean equals(Object o)
  {
    if (o != null && o instanceof Piece)
    {
      Piece p = (Piece)o;
      return p.x == x && p.y == y && p.type == type && p.color == color;
    }
    return false;
  }
  
  public String toString() { return "{"+type+", "+color+" at "+x+","+y+"}"; };
}
