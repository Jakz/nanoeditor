package pixbits.nanoblock.data;

import java.util.Objects;

import pixbits.nanoblock.gui.Tileset.PieceSpec;

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
  
  public boolean overlaps(Piece piece)
  {
    for (int lx = x; lx < x + type.width*2; lx += 2)
      for (int ly = y; ly < y + type.height*2; ly += 2)
      {
        for (int ox = piece.x; ox < piece.x + piece.type.width*2; ox += 2)
          for (int oy = piece.y; oy < piece.y + piece.type.height*2; oy += 2)
            if (overlapCoords(lx,ly,ox,oy))
            {
              //System.out.println(this+" is in front of "+piece+" at "+x+","+y+" on part "+lx+","+ly+" to "+ox+","+oy);
              
              return true;
            }
      }
    
    return false;
  }
  
  public static boolean  overlapCoords(int x1, int y1, int x2, int y2)
  {
    if (x2 == x1 - 2 && y2 >= y1-3 && y2 <= y1+1) { /*System.out.println("overlaps case 1");*/ return true; }
    else if (x2 == x1 - 3 && y2 >= y1-3 && y2 <= y1) { /*System.out.println("overlaps case 2");*/ return true; }
    else if (y2 == y1 - 2 && x2 >= x1 - 3 && x2 <= x1 + 1) { /*System.out.println("overlaps case 3");*/ return true; }
    else if (y2 == y1 - 3 && x2 >= x1 && x2 <= x1 - 3) { /*System.out.println("overlaps case 3");*/ return true; }
    
    return false;
  }
  
  public String toString() { return "{"+type+", "+color+" at "+x+","+y+"}"; };
}
