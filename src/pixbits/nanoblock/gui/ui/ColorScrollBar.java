package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.data.PieceColor;
import pixbits.nanoblock.gui.*;

public class ColorScrollBar extends UIScrollBar
{
  private final ColorPaletteView view;
  
  public ColorScrollBar(Sketch p, ColorPaletteView view, int ox, int oy, int width, int height, int buttonSize)
  {
    super(p,ox,oy,width,height,buttonSize);
    this.view = view;
  }    
  
  public int min() { return 0; }
  
  public int max() {
    return PieceColor.count() - view.cellCount;
  }
  
  public void progressChanged(int value)
  {
    view.setOffset(value);
  }
}
