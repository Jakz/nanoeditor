package processing.core;

import java.awt.Rectangle;

public interface PGfx
{
  public void blend(PImage src, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh, int mode);
  
  default void blend(PImage src, Rectangle rect, int dx, int dy, int mode)
  {
    blend(src, rect.x, rect.y, rect.width, rect.height, dx, dy, rect.width, rect.height, mode);
  }
  
  default void text(String text, float x, float y) { }
}
