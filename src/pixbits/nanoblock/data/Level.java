package pixbits.nanoblock.data;

import java.util.*;

import pixbits.nanoblock.Main;

public class Level implements Iterable<Piece>
{
  Set<Piece> pieces;
  final int width;
  final int height;
  
  private final Level previous;
  private Level next;
  
  //final int index;
  
  Level(int index, int width, int height, Level previous)
  {
    this.width = width;
    this.height = height;
    //this.index = index;
    this.previous = previous;
    
    pieces = new TreeSet<Piece>(new PieceComparator());
  }
  
  void setNext(Level next)
  {
    this.next = next;
  }
  
  public Level previous() { return previous; }
  public Level next() { return next; }
  
  void removePiece(Piece piece)
  {    
    pieces.remove(piece);
    
    /* add caps to current level */
    if (Main.drawCaps && previous != null && piece.type != PieceType.CAP)
    {
      for (int i = piece.x; i < piece.x+piece.type.width; ++i)
        for (int j = piece.y; j < piece.y+piece.type.height; ++j)
        {
          Piece piece2 = previous.pieceAt(i, j);
          if (piece2 != null && piece2.type != PieceType.CAP)
            addPiece(new Piece(PieceType.CAP, piece2.color, i,j));
        }
    }
    
    /* remove caps to next level */
    if (Main.drawCaps && next != null && piece.type != PieceType.CAP)
    {
      next.removeCaps(piece.x, piece.y, piece.type.width, piece.type.height);
    }
  }
    
  void addPiece(Piece piece)
  {
    /* remove caps to current level */
    if (Main.drawCaps)
    {   
      Iterator<Piece> pieces = iterator();
      while (pieces.hasNext())
      {
        Piece piece2 = pieces.next();
        if (piece2.type == PieceType.CAP)
          if (piece2.x >= piece.x && piece2.x < piece.x+piece.type.width && piece2.y >= piece.y && piece2.y < piece.y+piece.type.height)
            pieces.remove();
         
      }
    }
    
    pieces.add(piece);    
    
    /* add caps to next level */
    if (Main.drawCaps && next != null && piece.type != PieceType.CAP)
    {
      for (int i = piece.x; i < piece.x+piece.type.width; ++i)
        for (int j = piece.y; j < piece.y+piece.type.height; ++j)
          if (next.isFreeAt(i,j))
            next.addPiece(new Piece(PieceType.CAP, piece.color, i,j));
    }
    
  }
  
  public boolean isFreeAt(int x, int y)
  {
    Piece piece = pieceAt(x,y);
    
    return piece == null || piece.type == PieceType.CAP;
  }
  
  public boolean canPlace(PieceType type, int x, int y)
  {
    if (x+type.width-1 >= width || y+type.height-1 >= height)
      return false;
    
    for (int i = x; i < x+type.width; ++i)
      for (int j = y; j < y+type.height; ++j)
        if (!isFreeAt(i,j))
          return false;
    
    return true;
  }
  
  public void removeCaps(int x, int y, int w, int h)
  {
    Iterator<Piece> pieces = iterator();
    while (pieces.hasNext())
    {
      Piece piece = pieces.next();
      if (piece.type == PieceType.CAP && piece.x >= x && piece.x < x+w && piece.y >= y && piece.y < y+h)
      {
        pieces.remove();
      }
    }
  }
  
  public Piece pieceAt(int x, int y)
  {
    Iterator<Piece> pieces = iterator();
    
    while (pieces.hasNext())
    {
      Piece piece = pieces.next();

      if (x >= piece.x && x < piece.x+piece.type.width && y >= piece.y && y < piece.y+piece.type.height)
      {
        return piece;
      }
    }

    return null;
  }
  
  public int count()
  {
    return pieces.size();
  }
  
  public Iterator<Piece> iterator()
  {
    return pieces.iterator();
  }
  
  public void clear()
  {
    pieces.clear();
  }
  
  private static class PieceComparator implements Comparator<Piece>
  {
    public int compare(Piece p1, Piece p2)
    {
      if (p1.equals(p2))
        return 0;
      else
      {
        int sum1x = p1.x + p1.type.width - 1;
        int sum2x = p2.x + p2.type.width - 1;
        int sum1y = p1.y + p1.type.height - 1;
        int sum2y = p2.y + p2.type.height - 1;
        
        if ((sum1x < p2.x || sum1y < p2.y) && (sum2x < p1.x || sum2y < p1.y))
        {
          int sum1 = sum1x+sum1y;
          int sum2 = sum2x+sum2y;
          
          if (sum1 < sum2) return -1;
          else if (sum1 > sum2) return 1;
          else
          {
            if (p1.x < p2.x) return -1;
            else if (p1.x > p2.x) return 1;
            else
            {
              if (p1.y < p2.y) return -1;
              else if (p1.y > p2.y) return 1;
              else return 0;
            }
          }
        }
        else if (sum1x < p2.x || sum1y < p2.y)
          return -1;
        else if (sum2x < p1.x || sum2y < p1.y)
          return 1;
        else return 0;
      }
    }
  }
}
