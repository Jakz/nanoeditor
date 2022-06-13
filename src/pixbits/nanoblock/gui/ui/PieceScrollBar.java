package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.data.PieceType;
import pixbits.nanoblock.gui.*;

public class PieceScrollBar extends UIScrollBar
{
  private final PiecePaletteView view;
  
  public PieceScrollBar(Sketch p, PiecePaletteView view, int ox, int oy, int width, int height, int buttonSize)
  {
    super(p,ox,oy,width,height,buttonSize);
    this.view = view;
  }    
  
  public int min() { return 0; }
  
  public int max() {
    return view.brushCount() - view.cellCount;
  }
  
  public void progressChanged(int value)
  {
    view.setOffset(value);
  }
}
