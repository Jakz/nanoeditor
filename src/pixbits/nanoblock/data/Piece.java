package pixbits.nanoblock.data;

import java.util.Objects;

public class Piece
{
  public PieceType type;
  public PieceColor color;
  public int x, y;
  
  public Piece(PieceType type, PieceColor color, int x, int y)
  {
    this.type = type;
    this.color = color;
    this.x = x;
    this.y = y;
  }

  public Piece dupe()
  {
    return new Piece(type, color, x, y);
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (o != null && o instanceof Piece)
    {
      Piece p = (Piece)o;
      return p.x == x && p.y == y && p.type == type && p.color == color;
    }
    return false;
  }
  
  @Override
  public int hashCode() { return Objects.hash(type, color, x, y); }
  
  public String toString() { return "{"+type+", "+color+" at "+x+","+y+"}"; };
}
