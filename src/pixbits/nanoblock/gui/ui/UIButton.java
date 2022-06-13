package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.gui.*;
import java.awt.Color;

public class UIButton extends Node
{
  private final int w;
  private final int h;
  private boolean hover;
  
  public UIButton(Sketch p, int x, int y, int w, int h)
  {
    super(p,x,y);
    
    this.w = w;
    this.h = h;
    
    hover = false;
  }
  
  public boolean isInside(int x, int y) { return x >= this.x && x < this.x+this.w && y >= this.y && y < this.y+this.h; }
  
  public void mouseReleased(int x, int y, int b) { }
  public void mouseDragged(int x, int y, int b) { }
  public void mouseMoved(int x, int y) { hover = true; }
  public void mouseExited() { hover = false;}
  public void mouseWheelMoved(int x, int y, int v) { }
  
  public void draw()
  {
    if (!hover)
      p.stroke(new Color(80,80,80));
    else
      p.stroke(new Color(180,0,0));
    
    p.fill(new Color(200,200,200));
    p.rect(this.x, this.y, w, h);
  }
}
