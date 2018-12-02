package processing.core;

public interface PGfx
{
  public void blend(PImage src, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh, int mode);
  
  default void text(String text, float x, float y) { }
}
