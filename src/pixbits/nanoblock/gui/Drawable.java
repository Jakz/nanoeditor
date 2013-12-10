package pixbits.nanoblock.gui;

import processing.core.*;

public abstract class Drawable
{
  final int ox, oy;
  
  Drawable(int ox, int oy)
  {
    this.ox = ox;
    this.oy = oy;
  }
  
  public abstract boolean isInside(int x, int y);
  public abstract void mouseClicked(int x, int y);
  
  public abstract void draw(PApplet p);
}
