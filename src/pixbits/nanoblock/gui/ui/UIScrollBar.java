package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.gui.*;
import java.awt.Point;

public class UIScrollBar extends Drawable
{
  final int buttonSize;
  final int width;
  final int height;
  
  final Point length;
  final Point base;
  final boolean horizontal;
  
  float progress = 0.0f;
  
  public UIScrollBar(int ox, int oy, int width, int height, int buttonSize)
  {
    super(ox,oy);
    this.width = width;
    this.height = height;
    this.buttonSize = buttonSize;
    horizontal = width > height;

    if (horizontal)
    {
      length = new Point(width - buttonSize*2,0);
      base = new Point(ox + buttonSize, oy);
    }
    else
    {
      length = new Point(0,height - buttonSize*2);
      base = new Point(ox, oy + buttonSize);

    }

  }
  
  public boolean isInside(int x, int y)
  {
    return false;
  }
  
  public void mouseClicked(int x, int y)
  {
    
  }
  
  public void mouseMoved(int x, int y)
  {
    
  }
  
  public void mouseExited()
  {
    
  }
  
  public void draw(Sketch p)
  {
    p.fill(80);
    p.noStroke();
    
    if (horizontal)
    {
      p.rect(ox, oy, buttonSize, height);
      p.rect(ox+width-buttonSize*2, oy, buttonSize, height);
    }
    else
    {
      p.rect(ox, oy, width, buttonSize);
      p.rect(ox, oy+height-buttonSize*2, width, buttonSize);
    }
  }

}
