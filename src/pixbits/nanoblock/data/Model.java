package pixbits.nanoblock.data;

import java.util.*;

public class Model implements Iterable<Level>
{
  final private List<Level> levels;
  public final int width, height;
  
  public Model(int width, int height)
  {
    levels = new ArrayList<Level>();
    
    this.width = width;
    this.height = height;
  }
  
  public Level levelAt(int index)
  {
    return levels.get(index);
  }
  
  public int levelCount()
  {
    return levels.size();
  }
  
  public void addPiece(Level l, PieceType type, PieceColor color, int x, int y)
  {
    Piece piece = new Piece(type, color, x, y);
    l.addPiece(piece);
  }

  public void allocateLevels(int count)
  {
    Level previous = null;
    for (int i = 0; i < count+1; ++i)
    {
      Level level = new Level(i, width*2,height*2,previous);
      
      if (previous != null)
        previous.setNext(level);
      
      levels.add(level);
      
      previous = level;
    }
  }
  
  public boolean canShift(Direction dir)
  {
    int minX = width*2;
    int maxX = 0;
    int minY = height*2;
    int maxY = 0;
    
    for (Level l : levels)
    {
      for (Piece p : l.pieces)
      {
        minX = Math.min(minX, p.x);
        minY = Math.min(minY, p.y);
        maxX = Math.max(maxX, p.x+p.type.width*2);
        maxY = Math.max(maxY, p.y+p.type.height*2);
      }
    }
    
    switch (dir)
    {
      case NORTH: return minY >= 2;
      case SOUTH: return maxY <= height*2 - 2;
      
      case WEST: return maxX <= width*2 - 2;
      case EAST: return minX >= 2;
      
      default: return false;
    }
  }
  
  public void shift(Direction dir)
  {
    for (Level l : levels)
      for (Piece p : l.pieces)
      {
        p.x = p.x + dir.x*2;
        p.y = p.y + dir.y*2;
      }
  }
  
  public void removePiece(Level l, Piece piece)
  {
    l.removePiece(piece);
  }

  public void clear()
  {
    for (Level l : levels)
      l.clear();
  }
  
  public Iterator<Level> iterator() { return levels.iterator(); }
}
