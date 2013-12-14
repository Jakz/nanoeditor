package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;

import processing.core.*;
import java.awt.Rectangle;

public class PiecePaletteView extends Drawable
{
  public final int cellSize, cellCount;
  private int offset;
  private final PGraphics buffer;
  
  private PieceScrollBar scrollBar;
  
  PiecePaletteView(Sketch p, int ox, int oy, int cellSize, int cellCount)
  {
    super(p,ox,oy);
    this.cellSize = cellSize;
    this.cellCount = cellCount;
    this.offset = 0;
    
    buffer = Main.sketch.createGraphics(cellSize*2, cellSize*2, Sketch.P2D);
    
    if (cellCount < PieceType.count())
    {
      scrollBar = new PieceScrollBar(p, this, ox, oy + cellSize, cellSize*cellCount, 20, 20);
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
    Brush.type = PieceType.at(offset+x);
    
    Main.sketch.redraw();
  }

  public void mouseMoved(int x, int y)
  {

  }
  
  public void mouseDragged(int x, int y) { }

  public void mouseExited() { }
  
  public void mouseWheelMoved(int x) 
  {
    int i = 0;
    for (i = 0; i < PieceType.count(); ++i)
    {
      if (Brush.type == PieceType.at(i))
        break;
    }
    
    if (x > 0 && i + 1 < PieceType.count())
    {
      Brush.type = PieceType.at(i+1);
      if (scrollBar != null && i >= offset + cellCount - 1)
        scrollBar.downArrow();
    }
    else if (x < 0 && i > 0)
    {
      Brush.type = PieceType.at(i-1);
      if (scrollBar != null && i < offset + 1)
        scrollBar.upArrow();
    }
    
    Main.sketch.redraw();
  }

  public void draw()
  {
    p.stroke(0);
    p.noFill();
    
    PieceColor color = Brush.color;
    
    Rectangle rectc = Brush.tileset.rectFor(PieceType.CAP, color);
    int orx = Brush.tileset.spec(PieceType.CAP).ox, ory = Brush.tileset.spec(PieceType.CAP).oy;
    
    for (int i = 0; i < cellCount; ++i)
    {
      PieceType type = PieceType.at(offset+i);
      
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
      
      for (int ix = 0; ix < type.width; ++ix)
        for (int iy = 0; iy < type.height; ++iy)
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
  
  public int offset() { return offset; }
  public void setOffset(int value) { offset = value; }

}
