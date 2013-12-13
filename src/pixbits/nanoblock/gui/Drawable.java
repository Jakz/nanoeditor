package pixbits.nanoblock.gui;

public abstract class Drawable
{
  final protected int ox, oy;
  
  public Drawable(int ox, int oy)
  {
    this.ox = ox;
    this.oy = oy;
  }
  
  public abstract boolean isInside(int x, int y);
  public abstract void mouseClicked(int x, int y);
  public abstract void mouseMoved(int x, int y);
  public abstract void mouseExited();
  
  public abstract void draw(Sketch p);
}
