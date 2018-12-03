package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

import java.awt.Point;
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
    public final int size() { return size; }
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
      scrollBar = new PieceScrollBar(p, this, ox, oy + cellSize, cellSize*cellCount, GUI.scrollBarWidth, GUI.scrollBarWidth);
  }
  
  @Override
  public void setPosition(int x, int y)
  {
    if (scrollBar != null)
      scrollBar.setPosition(x, y + cellSize);
    super.setPosition(x, y);
  }
  
  public int brushCount() { return wrapper.size(); }
  
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
  
  public void mouseWheelMoved(int ___, int __, int v) 
  {
    int i = 0;
    for (i = 0; i < wrapper.size; ++i)
    {
      if (Brush.type() == wrapper.at(i))
        break;
    }
    
    if (v > 0 && i + 1 < wrapper.size)
    {    
      Brush.setType(wrapper.brushAt(i+1));
      if (scrollBar != null && i >= offset + cellCount - 1)
        scrollBar.downArrow();
    }
    else if (v < 0 && i > 0)
    {
      Brush.setType(wrapper.brushAt(i-1));
      if (scrollBar != null && i < offset + 1)
        scrollBar.upArrow();
    }
    
    Main.sketch.redraw();
  }

  public void draw()
  {
    if (scrollBar != null)
      scrollBar.draw();
    
    p.stroke(0);
    p.noFill();
    
    PieceColor color = Brush.color;
           
    int selectedIndex = -1;
    
    for (int i = 0; i < cellCount; ++i)
    {
      PieceType type = wrapper.at(offset+i);
      
      if (type == Brush.type())
        selectedIndex = i;
      
      SpriteBatch batch = new SpriteBatch();
      PieceDrawer.generateSprites(new Piece(type, color, 0, 0), batch);
      
      /* draw caps on piece */
      if (Settings.values.get(Setting.DRAW_CAPS))
      {
        type.forEachCap((x,y) -> 
          PieceDrawer.generateSprites(new Piece(PieceType.CAP, color, x, y), batch, 1)
        );
      }
      
      //TODO: cache this graphics 
      buffer.beginDraw();
      buffer.fill(220);
      buffer.rect(0,0,cellSize*2,cellSize*2);
      Rectangle bounds = batch.bounds();
      int opx = - bounds.x + cellSize+cellSize/2 - bounds.width/2, opy = - bounds.y + cellSize+cellSize/2 - bounds.height/2;

      batch.setPosition(opx, opy);
      batch.draw(buffer);

      buffer.endDraw();
      p.blend(buffer, cellSize, cellSize, cellSize, cellSize, ox+i*cellSize, oy, cellSize, cellSize, Sketch.BLEND);
      
      p.strokeWeight(1.0f);
      p.stroke(0);
      p.rect(ox+i*cellSize,oy,cellSize,cellSize);
    }
    
    if (selectedIndex >= 0)
    {
      p.strokeWeight(3.0f);
      p.stroke(255,0,0);
      p.rect(ox + selectedIndex*cellSize,oy,cellSize,cellSize);
    }
  }
  
  public int offset() { return offset; }
  public void setOffset(int value) { offset = value; }
}
