package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;

import processing.core.*;
import java.awt.Rectangle;

public class PiecePaletteView extends Drawable
{
  private static abstract class PiecesPaletteWrapper
  {
    public final int size;
   
    PiecesPaletteWrapper(int size)
    {
      this.size = size;
    }
    
    public abstract PieceType brushAt(int i);
    public abstract PieceType at(int i);
  };
  
  private static class NormalPiecesWrapper extends PiecesPaletteWrapper
  {
    NormalPiecesWrapper() { super(PieceType.count()); }
    
    public PieceType at(int i) { return PieceType.at(i); }
    public PieceType brushAt(int i) { return at(i); }
  }
  
  private static class RotatedPiecesWrapper extends PiecesPaletteWrapper
  {
    RotatedPiecesWrapper() { super(PieceType.spieces.length); }
    
    public PieceType at(int i) { return Brush.typeInverted ? PieceType.getRotation(PieceType.spieces[i]) : PieceType.spieces[i]; }
    public PieceType brushAt(int i) { return PieceType.spieces[i]; }
  }
  
  
  public final int cellSize, cellCount;
  private int offset;
  private final PGraphics buffer;
  
  private final PiecesPaletteWrapper wrapper;
  
  private PieceScrollBar scrollBar;
  
  PiecePaletteView(Sketch p, int ox, int oy, int cellSize, int cellCount, boolean implicitRotations)
  {
    super(p,ox,oy);
    
    if (implicitRotations)
      wrapper = new RotatedPiecesWrapper();
    else
      wrapper = new NormalPiecesWrapper();

    
    this.cellSize = cellSize;
    this.cellCount = Math.min(cellCount, wrapper.size);
    this.offset = 0;
    
    buffer = Main.sketch.createGraphics(cellSize*2, cellSize*2, Sketch.P2D);
    
    if (cellCount < wrapper.size)
    {
      scrollBar = new PieceScrollBar(p, this, ox, oy + cellSize, cellSize*cellCount, 20, 20);
      p.addDrawable(scrollBar);
    }
  }
  
  void dispose(Sketch p)
  {
    p.removeDrawable(this);
    if (scrollBar != null) p.removeDrawable(scrollBar);
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
    Brush.setType(wrapper.brushAt(offset+x));
    
    p.redraw();
  }

  public void mouseMoved(int x, int y)
  {

  }
  
  int lockX = -1;
  int lockY = -1;
  
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
    for (i = 0; i < wrapper.size; ++i)
    {
      if (Brush.type() == wrapper.at(i))
        break;
    }
    
    if (x > 0 && i + 1 < wrapper.size)
    {
      Brush.setType(wrapper.brushAt(i+1));
      if (scrollBar != null && i >= offset + cellCount - 1)
        scrollBar.downArrow();
    }
    else if (x < 0 && i > 0)
    {
      Brush.setType(wrapper.brushAt(i-1));
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
    PImage texture = Brush.tileset.imageForColor(color);
    int orx = Brush.tileset.spec(PieceType.CAP).ox, ory = Brush.tileset.spec(PieceType.CAP).oy;
    
    for (int i = 0; i < cellCount; ++i)
    {
      PieceType type = wrapper.at(offset+i);
            
      if (type == Brush.type())
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
      buffer.blend(texture, rect.x, rect.y, rect.width, rect.height, opx, opy, maxW, maxH, Sketch.BLEND);
      
      if (!type.monocap)
      {
        for (int ix = 0; ix < type.width; ++ix)
          for (int iy = 0; iy < type.height; ++iy)
          {
            int rx = opx - Brush.tileset.spec(type).ox + orx + Brush.tileset.xOffset*(ix-iy);
            int ry = opy - Brush.tileset.spec(type).oy + ory + Brush.tileset.yOffset*(ix+iy) - Brush.tileset.hOffset;
            
            
            buffer.blend(texture, rectc.x, rectc.y, rectc.width, rectc.height, rx, ry, rectc.width, rectc.height, Sketch.BLEND);
          }
      }
      else
      {
        float ix = 0.5f - (type.width > type.height ? 0.0f : 1.0f);
        float iy = 0.0f;
        
        
        int rx = (int) (opx - Brush.tileset.spec(type).ox + orx + Brush.tileset.xOffset*(ix-iy));
        int ry = (int) (opy - Brush.tileset.spec(type).oy + ory + Brush.tileset.yOffset*(ix+iy)) - Brush.tileset.hOffset;
        
        if (type.width < type.height) ry += Brush.tileset.yOffset;
        
        buffer.blend(texture, rectc.x, rectc.y, rectc.width, rectc.height, rx, ry, rectc.width, rectc.height, Sketch.BLEND);
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
