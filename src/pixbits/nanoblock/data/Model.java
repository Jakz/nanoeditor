package pixbits.nanoblock.data;

import java.awt.Rectangle;
import java.util.*;

import pixbits.nanoblock.files.*;
import pixbits.nanoblock.files.Log;

public class Model implements Iterable<Level>
{
  private LibraryModel lmodel; 
  
  final private List<Level> levels;
  
  public Model(LibraryModel lmodel)
  {
    levels = new ArrayList<Level>();
    this.lmodel = lmodel;
  }

  public int getHeight() { return lmodel.info.height; }
  public int getWidth() { return lmodel.info.width; }
  
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
    
    lmodel.info.levels = count-1;
  }
    
  public boolean canShift(Direction dir)
  {
    int minX = getWidth()*2;
    int maxX = 0;
    int minY = getHeight()*2;
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
      case SOUTH: return maxY <= getHeight()*2 - 2;
      
      case EAST: return maxX <= getWidth()*2 - 2;
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
      Log.i("Rotating model right");
      for (Level l : levels)
      {
        for (Piece p : l.pieces)
        {
          int oldY = p.y;
          p.y = p.x;
          p.x = getHeight()*2 - oldY- 1; // height pre swap
          p.type = PieceType.getRotation(p.type);
          p.x -= p.type.width*2 - 1;
        }
        
        l.resortPieces();
      }
    }
    else if (dir == Direction.WEST)
    {
      Log.i("Rotating model left");
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
  
  public void deleteLevel(Level level)
  {
    Level next = level.next();
    Level previous = level.previous();
    
    boolean hasNext = next != null;
    boolean hasPrev = previous != null;
    
    level.removeAllPieces();
    
    Set<Piece> previousPieces = null;
    
    if (hasPrev)
    {
      previousPieces = previous.findAllRealPieces();
      previous.removePieces(previousPieces);
      
      previous.setNext(next);
    }
    
    if (hasNext)
      next.setPrevious(previous);
    
    if (hasPrev)
      previous.addPieces(previousPieces);
    
    levels.remove(level);
  }
  
  public void insertBelow(Level oldLevel)
  {
    // TODO: cap bugged
    int i = indexOfLevel(oldLevel);
    
    Level newLevel = new Level(this);
    Level previousLevel = oldLevel.previous();

    Set<Piece> previousPieces = null;
    
    if (previousLevel != null)
    {
      previousPieces = previousLevel.findAllRealPieces();
      previousLevel.removePieces(previousPieces);
      
      previousLevel.setNext(newLevel);
    }

    newLevel.setNext(oldLevel);
    newLevel.setPrevious(previousLevel);
    oldLevel.setPrevious(newLevel);
    
    if (previousLevel != null)
      previousLevel.addPieces(previousPieces);
    
    levels.add(i, newLevel);
    ++lmodel.info.levels;

  }
  
  public void insertAbove(Level oldLevel)
  {
    int i = indexOfLevel(oldLevel);

    Level newLevel = new Level(this);

    Set<Piece> oldPieces = oldLevel.findAllRealPieces();
    oldLevel.removePieces(oldPieces);
    
    Level nextLevel = oldLevel.next();
    
    
    newLevel.setPrevious(oldLevel);
    newLevel.setNext(nextLevel);
    
    if (nextLevel != null)
      nextLevel.setPrevious(newLevel);
    
    oldLevel.setNext(newLevel);
    
    oldLevel.addPieces(oldPieces);
    
    ++lmodel.info.levels;
    levels.add(i+1, newLevel);
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
  
  public Rectangle computeBound()
  {
    int minX = Integer.MAX_VALUE, maxX = 0;
    int minY = Integer.MAX_VALUE, maxY = 0;
    
    for (Level l : levels)
    {
      for (Piece p : l)
      {
        minX = Math.min(p.x, minX);
        minY = Math.min(p.y, minY);
        
        maxX = Math.max(p.x + p.type.width*2, maxX);
        maxY = Math.max(p.y + p.type.height*2, maxY);
      }
    }
    
    if (minX % 2 == 1) --minX;
    if (minY % 2 == 1) --minY;
    if (maxX % 2 == 1) ++maxX;
    if (maxY % 2 == 1) ++maxY;
    
    return new Rectangle(minX/2, minY/2, maxX/2, maxY/2);
  }
  
  public boolean resize(Rectangle bounds, int w, int h, VerAttach va, HorAttach ha, boolean keepCentered)
  {
    int minX = bounds.x, minY = bounds.y;
    int maxX = bounds.width, maxY = bounds.height;
    int bwidth = maxX - minX, bheight = maxY - minY;
    int width = this.getWidth(), height = this.getHeight();
    boolean shrinkX = false, enlargeX = false, shrinkY = false, enlargeY = false;
    int deltaX = 0, deltaY = 0;
    
    if (w < bwidth || h < bheight) return false;
    
    if (w > width) enlargeX = true;
    else if (w < width) shrinkX = true;
    
    if (h > height) enlargeY = true;
    else if (h < height) shrinkY = true;
    
    Log.i("Resizing model");
    Log.i("Bounds X: "+minX+", "+maxX+"   Y: "+minY+", "+maxY);
    
    if (ha == HorAttach.LEFT) deltaX = -minX;
    else if (ha == HorAttach.RIGHT) deltaX = w - bwidth - minX;
    else if (ha == HorAttach.NONE)
    {
      int dw = w - bwidth;
      
      if (keepCentered && dw % 2 == 1)
      {
        ++w;
        ++dw;
      }
      
      dw /= 2;   
      deltaX = dw - minX;
    }
    
    if (va == VerAttach.TOP) deltaY = -minY;
    else if (va == VerAttach.BOTTOM) deltaY = h - bheight - minY;
    else if (va == VerAttach.NONE)
    {
      int dh = h - bheight;
      
      if (keepCentered && dh % 2 == 1)
      {
        ++h;
        ++dh;
      }
      
      dh /= 2;   
      deltaY = dh - minY;
    }
  
    Log.i("Delta "+deltaX+", "+deltaY);
   
    lmodel.info.width = w;
    lmodel.info.height = h;
    
    for (Level l : levels)
      for (Piece p : l)
      {
        p.x += deltaX*2;
        p.y += deltaY*2;
      }
    
    return true;
  }
  
  public Iterator<Level> iterator() { return levels.iterator(); }
  
  public ModelState dumpState()
  {
    ModelState state = new ModelState(getWidth(), getHeight(), lmodel.info.levels);
    
    int i = 0;
    for (Level l : this)
    {
      for (Piece p : l)
      {
        if (p.type != PieceType.CAP)
          state.addPiece(i, p);
      }
      ++i;
    }
    
    return state;
  }
  
  public void restoreState(ModelState state)
  {
    this.lmodel.info.width = state.getWidth();
    this.lmodel.info.height = state.getHeight();
    levels.clear();
    this.allocateLevels(lmodel.info.levels+1);
    for (ModelState.PieceState ps : state)
      this.addPiece(levels.get(ps.level), new Piece(ps.type, ps.color, ps.x, ps.y));
  }
}
