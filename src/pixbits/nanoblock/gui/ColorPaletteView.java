package pixbits.nanoblock.gui;

import com.pixbits.lib.lang.Size;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.PieceColor;
import pixbits.nanoblock.gui.ui.ColorScrollBar;
import processing.core.PConstants;

public class ColorPaletteView extends ParentNode<Node>
{  
  public int cellCount;
  public final int cellSize;
  
  private int offset;
  
  private ColorScrollBar scrollBar;
  
  ColorPaletteView(Sketch p, int cellSize, int cellCount)
  {
    super(p);
    this.cellSize = cellSize;
    this.cellCount = cellCount;
    this.offset = 0;
  }

  @Override 
  public void revalidate()
  {
    clear();

    LevelStackView levelStackView = parent().at(0);
    PiecePaletteView piecePaletteView = parent().at(2);
    Size.Int parentSize = parent().size;

    int x = levelStackView.totalWidth() + GUI.margin;
    int availableWidth = parentSize.w - x - GUI.margin;

    int availableCells = availableWidth / GUI.colorPaletteCellSize;
    this.cellCount = Math.min(availableCells, PieceColor.count());
    
    final boolean hasScrollbar = cellCount < PieceColor.count();
    
    int y = parentSize.h - piecePaletteView.cellSize - GUI.margin*2 - GUI.scrollBarWidth - (hasScrollbar ? GUI.scrollBarWidth : 0);
    
    setPosition(x, y);

    if (hasScrollbar)
    {
      scrollBar = new ColorScrollBar(p, this, x, y + cellSize, cellSize*cellCount, GUI.scrollBarWidth, GUI.scrollBarWidth);
      add(scrollBar);
    }
  }
  
  public boolean isInside(int x, int y)
  {
    return x >= this.x && x < this.x+cellSize*cellCount && y >= this.y && y < this.y+cellSize;
  }
  
  public void mouseReleased(int x, int y, int b)
  {
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
