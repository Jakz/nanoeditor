package pixbits.nanoblock.data;

import java.util.*;

public class Model implements Iterable<Level>
{
  private ModelInfo info;
  
  final private List<Level> levels;
  
  public Model(ModelInfo info)
  {
    levels = new ArrayList<Level>();
    this.info = info;
  }
  
  public Model(int width, int height)
  {
    levels = new ArrayList<Level>();
    info = new ModelInfo();
    info.width = width;
    info.height = height;
  }
  
  public ModelInfo getInfo() { return info; }
  public void setInfo(ModelInfo info) { this.info = info; }
  
  public int getHeight() { return info.height; }
  public int getWidth() { return info.width; }
  
  public Level levelAt(int index)
  {
    return levels.get(index);
  }
  
  public int levelCount()
  {
    return levels.size();
  }
  
  public int indexOfLevel(Level level)
  {
    int i = 0;
    for (Level l : levels)
    {
      if (level == l) return i;
      ++i;
    }
    
    return 0;
  }
  
  public void addPiece(Level l, Piece piece)
  {
    l.addPiece(piece);
  }
  
  public void addPiece(Level l, PieceType type, PieceColor color, int x, int y)
  {
    Piece piece = new Piece(type, color, x, y);
     l.addPiece(piece);
  }

  public void allocateLevels(int count)
  {
    Level previous = null;
    for (int i = 0; i < count; ++i)
    {
      Level level = new Level(this, previous);
      
      if (previous != null)
        previous.setNext(level);
      
      levels.add(level);
      
      previous = level;
    }
    
    info.levels = count-1;
  }
    
  public boolean canShift(Direction dir)
  {
    int minX = info.width*2;
    int maxX = 0;
    int minY = info.height*2;
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
      case SOUTH: return maxY <= info.height*2 - 2;
      
      case EAST: return maxX <= info.width*2 - 2;
      case WEST: return minX >= 2;
      
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
  
  public void rotate(Direction dir)
  {
    if (dir == Direction.EAST)
    {
      for (Level l : levels)
      {
        for (Piece p : l.pieces)
        {
          System.out.println("Rotate "+p);
          System.out.println("> "+p.x+","+p.y);
          int oldY = p.y;
          p.y = p.x;
          p.x = getHeight()*2 - oldY- 1; // height pre swap
          System.out.println("> "+p.x+","+p.y);
          p.type = PieceType.getRotation(p.type);
          p.x -= p.type.width*2 - 1;
          System.out.println("> "+p.x+","+p.y);
        }
        
        l.resortPieces();
      }
    }
    else if (dir == Direction.WEST)
    {
      for (Level l : levels)
      {
        for (Piece p : l.pieces)
        {
          int oldX = p.x;
          p.x = p.y;
          p.y = getWidth()*2 - oldX - 1; // height pre swap
          p.type = PieceType.getRotation(p.type);
          p.y -= p.type.height*2 - 1;
        }
        
        l.resortPieces();
      }
    }
    
    //TODO: manage swapping model width/height
  }
  
  public void insertBelow(Level level)
  {
    for (int i = 0; i < levels.size(); ++i)
    {
      if (levels.get(i) == level)
      {
        Level newLevel = new Level(this);
        
        Level oldLevel = levels.get(i);
        
        Set<Piece> caps = oldLevel.findAllCaps();
        oldLevel.removePieces(caps);
        newLevel.addPieces(caps);
        
        if (oldLevel.previous() != null)
          oldLevel.previous().setNext(newLevel);
        
        newLevel.setNext(oldLevel);
        newLevel.setPrevious(oldLevel.previous());
        
        oldLevel.setPrevious(newLevel);
        
        levels.add(i, newLevel);
        
        //TODO: move caps

        ++info.levels;
        return;
      }
    }
    
  }
  
  public void insertAbove(Level level)
  {
    for (int i = 0; i < levels.size(); ++i)
    {
      if (levels.get(i) == level)
      {
        Level newLevel = new Level(this);
        
        Level oldLevel = levels.get(i);
                
        if (oldLevel.next() != null)
        {
          Set<Piece> caps = oldLevel.next().findAllCaps();
          oldLevel.next().removePieces(caps);
          newLevel.addPieces(caps);
          
          oldLevel.next().setPrevious(newLevel);
        }
        
        newLevel.setPrevious(oldLevel);
        newLevel.setNext(oldLevel.next());
        
        oldLevel.setNext(newLevel);
        
        //TODO: move caps
        ++info.levels;
        levels.add(i+1, newLevel);
      }
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
