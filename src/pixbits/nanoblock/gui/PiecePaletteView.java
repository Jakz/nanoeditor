package pixbits.nanoblock.gui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import com.pixbits.lib.lang.Size;

public class PiecePaletteView extends ParentNode<Node>
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
  
  
  public int cellSize, cellCount;
  private int offset;
  private final PGraphics buffer;
  
  private final PiecesPaletteWrapper wrapper;
  
  private PieceScrollBar scrollBar;
  
  private final Map<PieceColor, Map<PieceType, PImage>> cache;
  
  PiecePaletteView(Sketch p, int cellSize)
  {
    super(p);
    
    if (true)
      wrapper = new RotatedPiecesWrapper();
    else
      //TODO: reimplement
      wrapper = new NormalPiecesWrapper();

    this.cellSize = cellSize;
    this.offset = 0;
    
    buffer = Main.sketch.createGraphics(cellSize*2, cellSize*2, Sketch.P2D);

    cache = new HashMap<>();
  }
  
  @Override
  public void revalidate()
  {
    clear();
    
    LevelStackView levelStackView = parent().at(0);
    Size.Int parentSize = parent().size;
    
    int baseX = levelStackView != null ? levelStackView.totalWidth() + GUI.margin : 0;
    int baseY = parentSize.h - GUI.piecePaletteCellSize - GUI.scrollBarWidth;
    int availableWidth = parentSize.h - baseX - GUI.margin;
    int availableCells = availableWidth / GUI.piecePaletteCellSize;
    
    setPosition(baseX, baseY);
    this.cellCount = Math.min(availableCells, wrapper.size);
    
    if (cellCount < wrapper.size)
      scrollBar = new PieceScrollBar(p, this, x, y + cellSize, cellSize*cellCount, GUI.scrollBarWidth, GUI.scrollBarWidth);

    add(scrollBar);
  }
  
  public int brushCount() { return wrapper.size(); }
  
  public boolean isInside(int x, int y)
  {
    return x >= this.x && x < this.x+cellSize*cellCount && y >= this.y && y < this.y+cellSize;
  }

  public void mouseReleased(int x, int y, int b)
  {
    x -= this.x;
    y -= this.y;
    
    x /= cellSize;
    Brush.setType(wrapper.brushAt(offset+x));
    
    p.redraw();
  }

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
  
  public PImage getPieceGfx(PieceType type, PieceColor color)
  {
    Map<PieceType, PImage> byPiece = cache.computeIfAbsent(color, c -> new HashMap<>());
    PImage image = byPiece.computeIfAbsent(type, t -> {
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
      
      PImage gfx = p.createGraphics(cellSize, cellSize, Sketch.P2D);
      gfx.blend(buffer, cellSize, cellSize, cellSize, cellSize, 0, 0, cellSize, cellSize, Sketch.BLEND);
      return gfx;
    });
    
    return image;
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
      
      PImage image = getPieceGfx(type, color);

      p.blend(image, 0, 0, cellSize, cellSize, x + i*cellSize, y, cellSize, cellSize, Sketch.BLEND);
      
      p.strokeWeight(1.0f);
      p.stroke(0);
      p.rect(x + i*cellSize, y, cellSize, cellSize);
    }
    
    if (selectedIndex >= 0)
    {
      p.strokeWeight(3.0f);
      p.stroke(255,0,0);
      p.rect(x + selectedIndex*cellSize, y, cellSize, cellSize);
    }
  }
  
  public int offset() { return offset; }
  public void setOffset(int value) { offset = value; }
}
