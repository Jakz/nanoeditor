package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;

import java.awt.Rectangle;

public class PiecePaletteView extends Drawable
{
  private final int cellSize, cellCount;
  
  PiecePaletteView(int ox, int oy, int cellSize, int cellCount)
  {
    super(ox,oy);
    this.cellSize = cellSize;
    this.cellCount = cellCount;
  }
  
  
  public boolean isInside(int x, int y)
  {
    return x >= ox && x < ox+cellSize*cellCount && y >= oy && y < oy+cellSize;
  }

  public void mouseClicked(int x, int y)
  {
    x -= ox;
    y -= oy;
    
    x /= cellSize;
    Brush.type = PieceType.at(x);
    
    Main.sketch.redraw();
  }

  public void mouseMoved(int x, int y)
  {

  }


  public void draw(Sketch p)
  {
    p.stroke(0);
    p.noFill();
    
    PieceColor color = Brush.color;
    
    for (int i = 0; i < PieceType.count(); ++i)
    {
      if (PieceType.at(i) == Brush.type)
      {
        p.strokeWeight(3.0f);
        p.stroke(255,0,0);
      }
      else
      {
        p.strokeWeight(1.0f);
        p.stroke(0);
      }
      
      Rectangle rect = Brush.tileset.rectFor(PieceType.at(i), color);
 
      int maxW = Math.min(cellSize, rect.width);
      int maxH = Math.min(cellSize, rect.height);
      
      p.rectMode(Sketch.CORNER);
      p.rect(ox+i*cellSize,oy,cellSize,cellSize);
      p.blend(Brush.tileset.image, rect.x, rect.y, rect.width, rect.height, ox+i*cellSize+cellSize/2-maxW/2, oy+cellSize/2-maxH/2, maxW, maxH, Sketch.BLEND);
    }
  }

}
