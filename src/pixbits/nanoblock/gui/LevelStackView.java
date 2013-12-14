package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;

import java.awt.Rectangle;

public class LevelStackView
{
  LevelView[] views;
  public LevelScrollBar scrollbar;
  
  private Rectangle hover;
  
  LevelStackView(Sketch p, int count, int ox, int oy, int cellSize, int margin, Model model)
  {
    views = new LevelView[count];
    
    for (int i = 0; i < count; ++i)
    {
      views[i] = new LevelView(this, p, model, model.levelAt(i), ox, oy+(count-i-1)*(margin+cellSize*model.height), cellSize);
      p.addDrawable(views[i]);
    }
    
    scrollbar = new LevelScrollBar(p, model, views, ox+cellSize*model.width, oy, 20, model.height*cellSize*count + margin*(count-1), 20);
    p.addDrawable(scrollbar);
  }
  
  public Rectangle hover()
  {
    return hover;
  }
  
  public void setHover(Rectangle r)
  {
    hover = r;
  }
}
