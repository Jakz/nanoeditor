package pixbits.nanoblock.gui;

import java.awt.Rectangle;

import com.pixbits.lib.lang.Point;
import com.pixbits.lib.lang.Size;

public class Atlas
{
  private final Point.Int base;
  private final Size.Int delta;
  private final Size.Int size;
  private final int rowLength;
  
  public Atlas(int x, int y, int w, int h, int dw, int dh, int row)
  {
    this.base = new Point.Int(x, y);
    this.size = new Size.Int(w, h);
    this.delta = new Size.Int(dw, dh);
    this.rowLength = row;
  }
  
  public Atlas(int x, int y, int w, int h, int row)
  {
    this(x, y, w, h, w, h, row);
  }
  
  public Atlas(int x, int y, int w, int h)
  {
    this(x, y, w, h, w, h, Integer.MAX_VALUE);
  }
  
  public Atlas(int x, int y, int w, int h, int dw, int dh)
  {
    this(x, y, w, h, dw, dh, Integer.MAX_VALUE);
  }
  
  Rectangle get(int i)
  {
    return new Rectangle(base.x + (i % rowLength) * delta.w, base.y + (i / rowLength) * delta.h, size.w, size.h);
  }
}
