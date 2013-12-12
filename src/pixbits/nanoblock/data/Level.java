package pixbits.nanoblock.data;

import java.util.*;

import pixbits.nanoblock.Main;

public class Level
{
  Set<Piece> pieces;
  final int width;
  final int height;
  
  private final Level previous;
  private Level next;
  
  final int index;
  
  Level(int index, int width, int height, Level previous)
  {
    this.width = width;
    this.height = height;
    this.index = index;
    this.previous = previous;
    
    pieces = new TreeSet<Piece>(new PieceComparator());
  }
  
  void setNext(Level next)
  {
    this.next = next;
  }
  
  public Level previous() { return previous; }
  
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
      for (int i = piece.x; i < piece.x+piece.type.width; ++i)
        for (int j = piece.y; j < piece.y+piece.type.height; ++j)
        {
          Piece piece2 = next.pieceAt(i,j);
          if (piece2 != null && piece2.type == PieceType.CAP)
            next.removePiece(piece2);
        }
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
    System.out.println("Placing piece at "+piece.x+","+piece.y);
    
    
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
    for (int i = x; i < x+type.width; ++i)
      for (int j = y; j < y+type.height; ++j)
        if (!isFreeAt(i,j))
          return false;
    
    return true;
  }
  
  public Piece pieceAt(int x, int y)
  {
    Iterator<Piece> pieces = iterator();
    
    while (pieces.hasNext())
    {
      Piece piece = pieces.next();
      if (x >= piece.x && x < piece.x+piece.type.width && y >= piece.y && y < piece.y+piece.type.height)
      {
        System.out.println("found piece at "+x+","+y);
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
  
  private static class PieceComparator implements Comparator<Piece>
  {
    public int compare(Piece p1, Piece p2)
    {
      if (p1.equals(p2))
        return 0;
      else
      {
        if (p1.x + p1.type.width - 1 < p2.x || p1.y + p1.type.height - 1 < p2.y)
          return -1;
        else
          return 1;
      }
    }
  }
}
