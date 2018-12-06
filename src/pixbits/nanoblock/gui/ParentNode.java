package pixbits.nanoblock.gui;

import java.util.ArrayList;
import java.util.List;

public class ParentNode<T extends Node> extends Node
{
  private final List<T> nodes;
  
  public ParentNode(Sketch p)
  {
    this(p, 0, 0);
  }
  
  public ParentNode(Sketch p, int x, int y)
  {
    super(p, x, y);
    nodes = new ArrayList<>();

  }
  
  @Override
  public final void onRevalidate()
  {
    System.out.println("Revalidating "+this.getClass().getName());

    for (T node : nodes)
      node.onRevalidate();
    
    revalidate();
  }
  
  @Override
  public final void onDraw()
  {
    for (Node node : nodes)
      node.onDraw();
      
    draw();
  }
  
  public void clear() { nodes.clear(); }
  public void add(T node) 
  { 
    node.setParent(this);
    nodes.add(node); 
  }
  
  @SuppressWarnings("unchecked")
  public <U extends Node> U at(int index) { return (U)nodes.get(index); }
  
  @Override
  public boolean isInside(int x, int y)
  {
    final int rx = x - this.x, ry = y - this.y;
    return nodes.stream().anyMatch(p -> p.isInside(rx, ry));
  }

  @Override
  public void onMouseReleased(int x, int y, int b)
  {
    final int rx = x - this.x, ry = y - this.y;
    
    for (Node d : nodes)
    {
      boolean dragLocked = d.draggingLock(); 
      d.draggingReset();
      
      if (!dragLocked && d.isInside(rx, ry))
      {
        d.onMouseReleased(rx, ry, b);
        return;
      }
    }
    
    mouseReleased(rx, ry, b);
  }

  @Override
  public void onMouseDragged(int x, int y, int b)
  {
    final int rx = x - this.x, ry = y - this.y;
 
    for (Node d : nodes)
      if (d.draggingLock())
      {
        d.onMouseDragged(rx, ry, b);
        return;
      }
    
    for (Node d : nodes)
    {
      if (d.isInside(rx, ry))
      {
        d.onMouseDragged(rx, ry, b);
        return;
      }
    }
    
    mouseDragged(rx, ry, b);
  }

  @Override
  public void onMouseMoved(int x, int y) 
  {
    final int rx = x - this.x, ry = y - this.y;
    for (Node node : nodes)
    {
      if (node.isInside(rx, ry))
      {
        node.onMouseMoved(rx, ry);
        return;
      }
      else
        node.onMouseExited();
    }
    
    mouseMoved(rx, ry);
  }

  @Override
  public void onMouseWheelMoved(int x, int y, int v)
  {
    final int rx = x - this.x, ry = y - this.y;
    
    for (Node node : nodes)
    {
      if (node.isInside(rx, ry))
      {
        node.onMouseWheelMoved(rx, ry, v);
        return;
      }
    }
    
    mouseWheelMoved(rx, ry, v);
  }
  
  @Override protected void mouseReleased(int x, int y, int b) { }
  @Override protected void mouseDragged(int x, int y, int b) { }
  @Override protected void mouseMoved(int x, int y) { }
  @Override protected void mouseExited() { }
  @Override protected void mouseWheelMoved(int x, int y, int v) { }
  @Override protected void draw() { }
}
