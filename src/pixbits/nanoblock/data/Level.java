package pixbits.nanoblock.data;

import java.util.*;

import pixbits.nanoblock.Main;

public class Level implements Iterable<Piece>
{
  Set<Piece> pieces;
  final int width;
  final int height;
  
  private Level previous;
  private Level next;
  
  //final int index;
  
  Level(int width, int height, Level previous)
  {
    this(width, height);
    
    //this.index = index;
    this.previous = previous;
    
    pieces = new TreeSet<Piece>(new PieceComparator());
  }
  
  Level(int width, int height)
  {
    this.width = width;
    this.height = height;
    pieces = new TreeSet<Piece>(new PieceComparator());
  }
  
  void setNext(Level next)
  {
    this.next = next;
  }
  
  void setPrevious(Level previous)
  {
    this.previous = previous;
  }
  
  public Level previous() { return previous; }
  public Level next() { return next; }
  
  
  
  void removePiece(Piece piece)
  {    
    pieces.remove(piece);
    
    /* add caps to current level */
    if (Main.drawCaps && previous != null && piece.type != PieceType.CAP)
    {
      for (int i = piece.x-1; i <= piece.x+piece.type.width*2; ++i)
        for (int j = piece.y-1; j <= piece.y+piece.type.height*2; ++j)
        {
          if (i >= 0 && j >= 0)
          {
            Piece piece2 = previous.pieceAt(i, j);
            
            if (piece2 != null)
            {              
              if (!piece2.type.monocap && i%2 == piece2.x%2 && j%2 == piece2.y%2)
                addPiece(new Piece(PieceType.CAP, piece2.color, i, j));
              else if (piece2.type.monocap && ((i == piece2.x+1 && j == piece2.y && piece2.type.width > piece2.type.height) || (i == piece2.x && j == piece2.y+1 && piece2.type.width < piece2.type.height)))
                addPiece(new Piece(PieceType.CAP, piece2.color, i, j));
            }
          }
        }
    }
    
    /* remove caps to next level */
    if (Main.drawCaps && next != null && piece.type != PieceType.CAP)
    {
      next.removeCaps(piece.x, piece.y, piece.type.width*2, piece.type.height*2);
    }
  }
    
  void addPiece(Piece piece)
  {
    System.out.println("Place at "+piece.x+","+piece.y);
    
    /* remove caps to current level */
    if (Main.drawCaps)
    {   
      Iterator<Piece> pieces = iterator();
      while (pieces.hasNext())
      {
        Piece piece2 = pieces.next();
        if (piece2.type == PieceType.CAP)
          if (piece2.x >= piece.x-1 && piece2.x < piece.x+piece.type.width*2 && piece2.y >= piece.y-1 && piece2.y < piece.y+piece.type.height*2)
            pieces.remove();
         
      }
    }
    
    //System.out.println("Add "+piece);
    pieces.add(piece);    
    
    /* add caps to next level */
    if (Main.drawCaps && next != null && piece.type != PieceType.CAP)
    {
      if (!piece.type.monocap)
      {
        System.out.println("Check for caps to add");
        
        for (int i = 0; i < piece.type.width*2; i += 2)
          for (int j = 0; j < piece.type.height*2; j += 2)
          {
            if (next.isFreeAt(piece.x+i,piece.y+j))
              next.addPiece(new Piece(PieceType.CAP, piece.color, piece.x+i, piece.y+j));
          }
      }
      else
      {
        int i = piece.type.width/2 + piece.x;
        int j = piece.type.height/2 + piece.y;
        if (next.isFreeAt(i,j))
          next.addPiece(new Piece(PieceType.CAP, piece.color, i, j));
      }
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
    
    for (int i = x; i < x+type.width*2; ++i)
      for (int j = y; j < y+type.height*2; ++j)
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

      if (x >= piece.x && x < piece.x+piece.type.width*2 && y >= piece.y && y < piece.y+piece.type.height*2)
      {
        return piece;
      }
    }

    return null;
  }
  
  public Set<Piece> findAllCaps()
  {
    Set<Piece> caps = new HashSet<Piece>();
    
    for (Piece p : pieces)
      if (p.type == PieceType.CAP)
        caps.add(p);
    
    return caps;
  }
  
  public void removePieces(Set<Piece> pieces)
  {
    for (Piece p : pieces)
      removePiece(p);
  }
  
  public void addPieces(Set<Piece> pieces)
  {
    for (Piece p : pieces)
      addPiece(p);
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
  
  public int indexOfPiece(Piece piece)
  {
    int i = 0;
    for (Piece p : pieces)
    {
      if (p.equals(piece)) break;
      ++i;
    }
   
    return i;
  }
  
  private static class PieceComparator implements Comparator<Piece>
  {
    public int compare(Piece p1, Piece p2)
    {
      if (p1.equals(p2))
        return 0;
      else
      {
        int sum1x = p1.x + (p1.type.width - 1)*2;
        int sum2x = p2.x + (p2.type.width - 1)*2;
        int sum1y = p1.y + (p1.type.height - 1)*2;
        int sum2y = p2.y + (p2.type.height - 1)*2;
        
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
