package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;

import java.awt.Rectangle;

public class LevelStackView
{
  LevelView[] views;
  public LevelScrollBar scrollbar;
  
  private Level locked;
  
  public void setLocked(Level level) { this.locked = level; }
  public Level getLocked() { return locked; }
  
  private PieceHover hover;
  private Level hoverLayer;
  
  private final Model model;
  private final int cellSize;
    
  LevelStackView(Sketch p, int count, int ox, int oy, int cellSize, int margin, Model model)
  {
    views = new LevelView[count];
    
    this.cellSize = cellSize;
    this.model = model;
    
    for (int i = 0; i < count && i < model.levelCount(); ++i)
    {
      views[i] = new LevelView(this, p, model, model.levelAt(i), i, ox, oy+(count-i-1)*(margin+cellSize*model.getHeight()), cellSize);
      p.addDrawable(views[i]);
    }
    
    scrollbar = new LevelScrollBar(p, model, views, ox+cellSize*model.getWidth(), oy, GUI.scrollBarWidth, model.getHeight()*cellSize*count + margin*(count-1), GUI.scrollBarWidth);
    p.addDrawable(scrollbar);
    
    locked = null;
  }
  
  int totalWidth() { return gridWidth() + scrollbar.width(); }
  int gridWidth() { return cellSize*model.getWidth(); }
  
  void dispose(Sketch p)
  {
    for (LevelView view : views)
      p.removeDrawable(view);
    if (scrollbar != null) p.removeDrawable(scrollbar);
  }
  
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
