package pixbits.nanoblock.gui;

public abstract class Drawable
{
  final protected int ox, oy;
  final protected Sketch p;
  
  public Drawable(Sketch p, int ox, int oy)
  {
    this.ox = ox;
    this.oy = oy;
    this.p = p;
  }
  
  public abstract boolean isInside(int x, int y);
  public abstract void mouseReleased(int x, int y);
  public abstract void mouseDragged(int x, int y);
  public abstract void mouseMoved(int x, int y);
  public abstract void mouseExited();
  
  public abstract void draw();
}
