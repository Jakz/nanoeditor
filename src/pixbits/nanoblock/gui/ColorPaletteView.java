package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.PieceColor;
import pixbits.nanoblock.gui.ui.ColorScrollBar;
import processing.core.PConstants;

public class ColorPaletteView extends ParentNode<Node>
{  
  public final int cellCount;
  public final int cellSize;
  
  private int offset;
  
  private ColorScrollBar scrollBar;
  
  ColorPaletteView(Sketch p, int cellSize, int cellCount)
  {
    super(p);
    this.cellSize = cellSize;
    this.cellCount = cellCount;
    this.offset = 0;
    
    if (cellCount < PieceColor.count())
    {
      scrollBar = new ColorScrollBar(p, this, 0, cellSize, cellSize*cellCount, GUI.scrollBarWidth, GUI.scrollBarWidth);
      add(scrollBar);
    }
  }

  @Override 
  public void revalidate()
  {
    LevelStackView levelStackView = parent().at(0);
    PiecePaletteView piecePaletteView = parent().at(2);
    
    int x = levelStackView.totalWidth() + GUI.margin;
    int y = parent().size.h - piecePaletteView.cellSize - GUI.margin*2 - GUI.scrollBarWidth;
    
    setPosition(x, y);
  }
  
  public boolean isInside(int x, int y)
  {
    return x >= this.x && x < this.x+cellSize*cellCount && y >= this.y && y < this.y+cellSize;
  }
  
  public void mouseReleased(int x, int y, int b)
  {
    x -= this.x;
    y -= this.y;
    
    x /= cellSize;
    
    if (offset+x < PieceColor.count())
    {
      Brush.color = PieceColor.at(offset+x);
      Main.sketch.redraw();
    }
  }

  public void mouseWheelMoved(int ___, int __, int v) 
  {
    int i = 0;
    for (i = 0; i < PieceColor.count(); ++i)
    {
      if (Brush.color == PieceColor.at(i))
        break;
    }
    
    if (v > 0 && i + 1 < PieceColor.count())
    {
      Brush.color = PieceColor.at(i+1);
      if (scrollBar != null && i >= offset + cellCount - 1)
        scrollBar.downArrow();
    }
    else if (v < 0 && i > 0)
    {
      Brush.color = PieceColor.at(i-1);
      if (scrollBar != null && i < offset + 1)
        scrollBar.upArrow();
    }
    
    Main.sketch.redraw();
  }
  
  public void draw()
  {
    p.strokeWeight(1.0f);
    
    int selectedIndex = -1;
    for (int i = 0; i < cellCount && offset + i < PieceColor.count(); ++i)
    {
      PieceColor color = PieceColor.at(offset+i);
      
      if (color == Brush.color)
        selectedIndex = i;

      p.stroke(color.strokeColor);
      p.fill(color.fillColor);
      p.rect(x + i*cellSize, y, cellSize, cellSize);
    }
    
    /* draw selection border */
    if (selectedIndex >= 0)
    {
      p.strokeWeight(3.0f);
      p.stroke(255,0,0);
      p.noFill();
      p.rect(x + selectedIndex*cellSize, y, cellSize, cellSize);
    }
  }
  
  public void setOffset(int value) { offset = value; }
  public int offset() { return offset; }
}
