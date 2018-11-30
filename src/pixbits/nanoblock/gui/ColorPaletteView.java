package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.PieceColor;
import pixbits.nanoblock.gui.ui.ColorScrollBar;
import processing.core.PConstants;

public class ColorPaletteView extends Drawable 
{  
  public final int cellCount;
  public final int cellSize;
  
  private int offset;
  
  private ColorScrollBar scrollBar;
  
  ColorPaletteView(Sketch p, int ox, int oy, int cellSize, int cellCount)
  {
    super(p,ox,oy);
    this.cellSize = cellSize;
    this.cellCount = cellCount;
    this.offset = 0;
    
    if (cellCount < PieceColor.count())
    {
      scrollBar = new ColorScrollBar(p, this, ox, oy + cellSize, cellSize*cellCount, 20, 20);
      p.addDrawable(scrollBar);
    }
  }
  
  public boolean isInside(int x, int y)
  {
    return x >= ox && x < ox+cellSize*cellCount && y >= oy && y < oy+cellSize;
  }
  
  public void mouseReleased(int x, int y, int b)
  {
    x -= ox;
    y -= oy;
    
    x /= cellSize;
    
    if (offset+x < PieceColor.count())
    {
      Brush.color = PieceColor.at(offset+x);
      Main.sketch.redraw();
    }
  }
  
  public void mouseMoved(int x, int y) { }
  
  int lockX = -1, lockY = -1;
  
  public void mouseDragged(int x, int y, int b)
  { 

    
    if (b == PConstants.RIGHT)
    {
      if (!dragging)
      {        
        if (x >= ox && y >= oy && x < ox + cellSize*cellCount && y < oy + cellSize)
        {
          dragging = true;
          lockX = x;
          lockY = y;
        }
        else
          return;
      }
      
      this.ox += x - lockX;
      this.oy += y - lockY;
      
      if (scrollBar != null)
      {
        scrollBar.shift(x-lockX, y-lockY);
      }
      
      lockX = x;
      lockY = y;
      
      p.redraw();
    }
  }
  
  public void mouseExited() { }

  public void mouseWheelMoved(int x) 
  {
    int i = 0;
    for (i = 0; i < PieceColor.count(); ++i)
    {
      if (Brush.color == PieceColor.at(i))
        break;
    }
    
    if (x > 0 && i + 1 < PieceColor.count())
    {
      Brush.color = PieceColor.at(i+1);
      if (scrollBar != null && i >= offset + cellCount - 1)
        scrollBar.downArrow();
    }
    else if (x < 0 && i > 0)
    {
      Brush.color = PieceColor.at(i-1);
      if (scrollBar != null && i < offset + 1)
        scrollBar.upArrow();
    }
    
    Main.sketch.redraw();
  }
  
  public void draw()
  {
    p.rectMode(Sketch.CORNER);
    
    for (int i = 0; i < cellCount && offset + i < PieceColor.count(); ++i)
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
