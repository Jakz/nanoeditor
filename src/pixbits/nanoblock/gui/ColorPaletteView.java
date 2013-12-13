package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.PieceColor;
import pixbits.nanoblock.gui.ui.ColorScrollBar;

public class ColorPaletteView extends Drawable 
{  
  public final int cellCount;
  public final int cellSize;
  
  private int offset;
  
  ColorPaletteView(Sketch p, int ox, int oy, int cellSize, int cellCount)
  {
    super(p,ox,oy);
    this.cellSize = cellSize;
    this.cellCount = cellCount;
    this.offset = 0;
    
    if (cellCount < PieceColor.count())
    {
      ColorScrollBar scrollBar = new ColorScrollBar(p, this, ox, oy + cellSize, cellSize*cellCount, 20, 20);
      p.addDrawable(scrollBar);
    }
  }
  
  public boolean isInside(int x, int y)
  {
    return x >= ox && x < ox+cellSize*cellCount && y >= oy && y < oy+cellSize;
  }
  
  public void mouseReleased(int x, int y)
  {
    x -= ox;
    y -= oy;
    
    x /= cellSize;
    Brush.color = PieceColor.at(offset+x);
    
    Main.sketch.redraw();
  }
  
  public void mouseMoved(int x, int y) { }
  
  public void mouseDragged(int x, int y) { }
  
  public void mouseExited() { }
  
  public void draw()
  {
    p.rectMode(Sketch.CORNER);
    
    for (int i = 0; i < cellCount; ++i)
    {
      PieceColor color = PieceColor.at(offset+i);
      
      if (color == Brush.color)
      {
        p.strokeWeight(3.0f);
        p.stroke(255,0,0);
      }
      else
      {
        p.strokeWeight(1.0f);
        p.stroke(color.strokeColor);
      }
      
      p.fill(color.fillColor);
      p.rect(ox+i*cellSize, oy, cellSize, cellSize);
    }
  }
  
  public void setOffset(int value) { offset = value; }
  public int offset() { return offset; }
}
