package pixbits.nanoblock.gui;

public abstract class Drawable
{
  final protected int ox, oy;
  final protected Sketch p;
  protected boolean dragging;
  
  public Drawable(Sketch p, int ox, int oy)
  {
    this.ox = ox;
    this.oy = oy;
    this.p = p;
    this.dragging = false;
  }
  
  public abstract boolean isInside(int x, int y);
  public abstract void mouseReleased(int x, int y, int b);
  public abstract void mouseDragged(int x, int y);
  public abstract void mouseMoved(int x, int y);
  public abstract void mouseExited();
  public abstract void mouseWheelMoved(int x);
  
  public boolean draggingLock() { return dragging; }
  public void draggingReset() { dragging = false; }
  
  public abstract void draw();
}
