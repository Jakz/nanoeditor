package pixbits.nanoblock.data;

import java.util.*;

public class Level
{
  Set<Piece> pieces;
  Piece[][] table;
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
    table = new Piece[width][height];
  }
  
  void setNext(Level next)
  {
    this.next = next;
  }
  
  void removePiece(Piece piece)
  {
    for (int i = piece.x; i < piece.x+piece.type.width; ++i)
      for (int j = piece.y; j < piece.y+piece.type.height; ++j)
      {
        if (table[i][j].equals(piece) || (table[i][j] != null && table[i][j].type == PieceType.CAP))
          table[i][j] = null;
      }
    
    pieces.remove(piece);
    
    /* add caps to current level */
    if (previous != null && piece.type != PieceType.CAP)
    {
      for (int i = piece.x; i < piece.x+piece.type.width; ++i)
        for (int j = piece.y; j < piece.y+piece.type.height; ++j)
          if (previous.table[i][j] != null && previous.table[i][j].type != PieceType.CAP)
            addPiece(new Piece(PieceType.CAP, i,j));
    }
    
    /* remove caps to next level */
    if (next != null && piece.type != PieceType.CAP)
    {
      for (int i = piece.x; i < piece.x+piece.type.width; ++i)
        for (int j = piece.y; j < piece.y+piece.type.height; ++j)
          if (next.table[i][j] != null && next.table[i][j].type == PieceType.CAP)
            next.removePiece(next.table[i][j]);
    }
  }
  
  void addPiece(Piece piece)
  {
    /* remove caps to current level */
    for (int i = piece.x; i < piece.x+piece.type.width; ++i)
      for (int j = piece.y; j < piece.y+piece.type.height; ++j)
        if (table[i][j] != null)
        {
          pieces.remove(table[i][j]);
        }
    
    pieces.add(piece);
    table[piece.x][piece.y] = piece;
    
    /* add caps to next level */
    if (next != null && piece.type != PieceType.CAP)
    {
      for (int i = piece.x; i < piece.x+piece.type.width; ++i)
        for (int j = piece.y; j < piece.y+piece.type.height; ++j)
          if (next.table[i][j] == null)
            next.addPiece(new Piece(PieceType.CAP, i,j));
    }
    
  }
  
  public Piece pieceAt(int x, int y)
  {
    return table[x][y];
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
      if (p1.x < p2.x)
        return -1;
      else if (p2.x < p1.x)
        return 1;
      else
      {
        if (p1.y < p2.y)
          return -1;
        else if (p2.y < p1.y)
          return 1;
        else
          return 0;
      }
      
      /*int M1x = p1.x + p1.type.width-1;
      int M1y = p1.y + p1.type.height-1;
      int m1x = p1.x, m1y = p1.y;
      
      int M2x = p2.x + p2.type.width-1;
      int M2y = p2.y + p2.type.height-1;
      int m2y = p2.x;
      
      if (M1x < M2x && M1y < M2y)
        return -1;
      else if (M2x > M1x && M2y > M2x)
        return 1;
      else
      {
        return 0;
      }*/
    }
  }
}
