package pixbits.nanoblock.data;

import java.util.*;

public class Model
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
  
  public void allocateLevels(int count)
  {
    for (int i = 0; i < count; ++i)
      levels.add(new Level(width,height));
  }
  
  public void removePiece(Piece piece)
  {
    for (Level l : levels)
      l.removePiece(piece);
  }
}
