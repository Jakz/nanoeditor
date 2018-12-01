package pixbits.nanoblock.gui;

public abstract class Drawable
{
  protected int ox, oy;
  protected final Sketch p;
  protected boolean dragging;
  
  public Drawable(Sketch p, int ox, int oy)
  {
    this.ox = ox;
    this.oy = oy;
    this.p = p;
    this.dragging = false;
  }
  
  public int x() { return ox; }
  public int y() { return oy; }
  public void setPosition(int x, int y) { ox = x; oy = y; }
  
  public abstract boolean isInside(int x, int y);
  public abstract void mouseReleased(int x, int y, int b);
  public abstract void mouseDragged(int x, int y, int b);
  public abstract void mouseMoved(int x, int y);
  public abstract void mouseExited();
  public abstract void mouseWheelMoved(int x, int y, int v);
  
  public boolean draggingLock() { return dragging; }
  public void draggingReset() { dragging = false; }
  
  public abstract void draw();
  
  public static class Wrapper<T extends Drawable> extends Drawable
  {
    private T drawable;
    public Wrapper(T drawable)
    { 
      super(null, 0, 0);
      this.drawable = drawable;
    }
    
    @Override public boolean isInside(int x, int y) { return drawable != null ? drawable.isInside(x, y) : false; }
    @Override public void mouseReleased(int x, int y, int b) { drawable.mouseReleased(x, y, b); }
    @Override public void mouseDragged(int x, int y, int b) { drawable.mouseDragged(x, y, b); }
    @Override public void mouseMoved(int x, int y) { drawable.mouseMoved(x, y); }
    @Override public void mouseExited() { drawable.mouseExited(); }
    @Override public void mouseWheelMoved(int x, int y, int v) { drawable.mouseWheelMoved(x, y, v); }
    @Override public void draw() { if (drawable != null) drawable.draw(); }
    
    public T get() { return drawable; }
    public void set(T d) { this.drawable = d; }
  }
}
