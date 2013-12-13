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
      Level level = new Level(i, width,height,previous);
      
      if (previous != null)
        previous.setNext(level);
      
      levels.add(level);
      
      previous = level;
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
