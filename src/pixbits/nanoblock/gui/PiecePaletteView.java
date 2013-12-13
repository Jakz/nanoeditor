package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;

import processing.core.*;
import java.awt.Rectangle;

public class PiecePaletteView extends Drawable
{
  private final int cellSize, cellCount;
  private final PGraphics buffer;
  
  PiecePaletteView(int ox, int oy, int cellSize, int cellCount)
  {
    super(ox,oy);
    this.cellSize = cellSize;
    this.cellCount = cellCount;
    buffer = Main.sketch.createGraphics(cellSize*2, cellSize*2, PGraphics.P2D);
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
  
  public void mouseExited() { }

  public void draw(Sketch p)
  {
    p.stroke(0);
    p.noFill();
    
    PieceColor color = Brush.color;
    
    Rectangle rectc = Brush.tileset.rectFor(PieceType.CAP, color);
    int orx = Brush.tileset.spec(PieceType.CAP).ox, ory = Brush.tileset.spec(PieceType.CAP).oy;
    
    for (int i = 0; i < PieceType.count(); ++i)
    {
      PieceType type = PieceType.at(i);
      
      if (type == Brush.type)
      {
        p.strokeWeight(3.0f);
        p.stroke(255,0,0);
      }
      else
      {
        p.strokeWeight(1.0f);
        p.stroke(0);
      }

      Rectangle rect = Brush.tileset.rectFor(type, color);
      
      int maxW = rect.width;//Math.min(cellSize, rect.width);
      int maxH = rect.height;//Math.min(cellSize, rect.height);
      
      int opx = cellSize+cellSize/2-maxW/2, opy = cellSize+cellSize/2-maxH/2;
            
      buffer.beginDraw();
      buffer.fill(220);
      buffer.rect(0,0,cellSize*2,cellSize*2);
      buffer.blend(Brush.tileset.image, rect.x, rect.y, rect.width, rect.height, opx, opy, maxW, maxH, Sketch.BLEND);
      
      for (int ix = 0; ix < PieceType.at(i).width; ++ix)
        for (int iy = 0; iy < PieceType.at(i).height; ++iy)
        {
          int rx = opx - Brush.tileset.spec(type).ox + orx + Brush.tileset.xOffset*ix-Brush.tileset.yOffset*iy;
          int ry = opy - Brush.tileset.spec(type).oy + ory + Brush.tileset.hOffset*(ix+iy-2);
          
          buffer.blend(Brush.tileset.image, rectc.x, rectc.y, rectc.width, rectc.height, rx, ry, rectc.width, rectc.height, Sketch.BLEND);
        }
      
      buffer.endDraw();
      
      p.blend(buffer, cellSize, cellSize, cellSize, cellSize, ox+i*cellSize, oy, cellSize, cellSize, Sketch.BLEND);
      
      p.rectMode(Sketch.CORNER);
      p.rect(ox+i*cellSize,oy,cellSize,cellSize);
    }
  }

}
