package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.PieceColor;

public class ColorPaletteView extends Drawable 
{  
  final int cellCount;
  final int cellSize;
  
  ColorPaletteView(Sketch p, int ox, int oy, int cellSize, int cellCount)
  {
    super(p,ox,oy);
    this.cellSize = cellSize;
    this.cellCount = cellCount;
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
    Brush.color = PieceColor.at(x);
    
    Main.sketch.redraw();
  }
  
  public void mouseMoved(int x, int y) { }
  
  public void mouseDragged(int x, int y) { }
  
  public void mouseExited() { }
  
  public void draw()
  {
    p.rectMode(Sketch.CORNER);
    
    for (int i = 0; i < PieceColor.count(); ++i)
    {
      PieceColor color = PieceColor.at(i);
      
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
}
