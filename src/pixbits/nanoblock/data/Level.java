package pixbits.nanoblock.data;

import java.util.*;

public class Level
{
  Set<Piece> pieces;
  Piece[][] table;
  final int width;
  final int height;
  
  Level(int width, int height)
  {
    this.width = width;
    this.height = height;
    
    pieces = new TreeSet<Piece>(new PieceComparator());
    table = new Piece[width][height];
  }
  
  public boolean removePiece(Piece piece)
  {
    boolean found = false;

    for (int i = 0; i < width; ++i)
      for (int j = 0; j < height; ++j)
      {
        if (table[i][j] != null && table[i][j].equals(piece))
        {
          found = true;
          table[i][j] = null;
        }
      }
    
    pieces.remove(piece);
    
    return found;
  }
  
  public void addPiece(Piece piece)
  {
    pieces.add(piece);
    table[piece.x][piece.y] = piece;
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
