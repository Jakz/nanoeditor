package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;

import java.awt.Rectangle;

public class LevelStackView extends ParentNode<Node>
{
  private LevelView[] views;
  private LevelScrollBar scrollbar;
  
  private Level locked;
  
  public void setLocked(Level level) { this.locked = level; }
  public Level getLocked() { return locked; }
  
  private PieceHover hover;
  private Level hoverLayer;
  
  private final Model model;
  private final int cellSize;
  private final int minMargin;
    
  LevelStackView(Sketch p, int cellSize, int minMargin, Model model)
  {    
    super(p);
    
    this.cellSize = cellSize;
    this.minMargin = minMargin;
    this.model = model;

    locked = null;
  }
  
  @Override
  public void revalidate()
  {    
    final int height = parent().size.h;
    
    final int gridHeight = model.getHeight()*cellSize;
    final int count = height / (gridHeight+minMargin);
    final int leftover = height - minMargin - gridHeight*count - minMargin*(count-1);
    final int distance = minMargin + leftover / (count-1);
        
    clear();
    views = new LevelView[count];
    
    for (int i = 0; i < count; ++i)
    {
      views[i] = new LevelView(p, model, model.levelAt(i), i, 0, height - minMargin - (i+1)*gridHeight - i*distance, cellSize);
      add(views[i]);
    }
    
    scrollbar = new LevelScrollBar(p, model, views, gridWidth(), 0, GUI.scrollBarWidth, gridHeight*count + distance*(count-1) /*+ minMargin*/, GUI.scrollBarWidth);
    add(scrollbar);
  }
  
  int totalWidth() { return gridWidth() + scrollbar.width(); }
  int gridWidth() { return cellSize*model.getWidth(); }
  UIScrollBar scrollbar() { return scrollbar; }
  
  public PieceHover hover()
  {
    return hover;
  }
  
  public void setHover(PieceHover r)
  {
    hover = r;
  }
  
  public void clearToBeDeleted()
  {
    for (LevelView v : views)
      v.wouldBeRemovedPiece = null;
  }
  
  public void refreshAccordingToLockedLevel()
  {
    int index = 0;
    
    // first search for the locked level in the array
    for (index = 0; index < views.length; ++index)
      if (views[index].level() == locked)
        break;

    // TODO
  }
  
  public Level getHoveredLevel() { return hoverLayer; }
  public void setHoveredLevel(Level level) { this.hoverLayer = level; }
}
