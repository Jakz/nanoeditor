package pixbits.nanoblock.gui;

import com.pixbits.lib.lang.Size;

public abstract class Node
{
  private ParentNode<?> parent;
  protected boolean dirty;
  
  protected int x, y;
  protected Size.Int size;
  protected final Sketch p;
  protected boolean dragging;
    
  public Node(Sketch p)
  {
    this(p, 0, 0);
  }
  
  public Node(Sketch p, int x, int y)
  {
    this.x = x;
    this.y = y;
    this.p = p;
    this.dragging = false;
    
    this.dirty = false;
    this.parent = null;
  }
  
  public int x() { return x; }
  public int y() { return y; }

  
  public void setPosition(int x, int y) 
  { 
    this.x = x; 
    this.y = y; 
    dirty = true;
  }
  
  public void setSize(int w, int h)
  {
    this.size = new Size.Int(w, h);
  }
  
  public int w() { return size.w; }
  public int h() { return size.h; }
  
  public void setParent(ParentNode<?> parent) { this.parent = parent; }
  @SuppressWarnings("unchecked")
  protected <T extends ParentNode<?>> T parent() { return (T)parent; }
  
  public abstract boolean isInside(int x, int y);
  
  protected abstract void mouseReleased(int x, int y, int b);
  protected abstract void mouseDragged(int x, int y, int b);
  protected abstract void mouseMoved(int x, int y);
  protected abstract void mouseExited();
  protected abstract void mouseWheelMoved(int x, int y, int v);
  
  public void onRevalidate() { }
  protected void revalidate() { }
  
  public void onMouseReleased(int x, int y, int b) { mouseReleased(x, y, b); }
  public void onMouseDragged(int x, int y, int b) { mouseDragged(x, y, b); }
  public void onMouseMoved(int x, int y) { mouseMoved(x, y); }
  public void onMouseExited() { mouseExited(); }
  public void onMouseWheelMoved(int x, int y, int v) { mouseWheelMoved(x, y, v); }
  
  public boolean draggingLock() { return dragging; }
  public void draggingReset() { dragging = false; }

  protected abstract void draw();
  public void onDraw() { draw(); }
}
