package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.data.PieceColor;
import pixbits.nanoblock.gui.*;

public class LevelScrollBar extends UIScrollBar
{
  private final LevelView[] views;
  private final Model model;
  
  public LevelScrollBar(Sketch p, Model model, LevelView[] views, int ox, int oy, int width, int height, int buttonSize)
  {
    super(p,ox,oy,width,height,buttonSize);
    this.views = views;
    this.model = model;
    this.progress = 1.0f;
    this.progressReal = max();
  }    
  
  public int min() { return 0; }
  
  public int max() {
    return model.levelCount() - views.length - 1;
  }
  
  public void progressChanged(int value)
  {
    for (int i = 0; i < views.length; ++i)
    {
      views[i].moveToLevel(max() - value + i);
    }
  }
}
