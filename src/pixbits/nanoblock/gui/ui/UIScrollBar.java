package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.gui.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;

public class UIScrollBar extends Drawable
{
  final int buttonSize;
  final int width;
  final int height;
  
  final Point length;
  final Point base;
  final Dimension scrollButton;
  
  final Rectangle button1, button2;
  final Rectangle scroll;
  
  final boolean horizontal;
  
  float progress = 0.0f;
  float progressStep = 0.1f;
  
  public UIScrollBar(Sketch p, int ox, int oy, int width, int height, int buttonSize)
  {
    super(p,ox,oy);
    this.width = width;
    this.height = height;
    this.buttonSize = buttonSize;
    horizontal = width > height;

    if (horizontal)
    {
      button1 = new Rectangle(ox, oy, buttonSize, height);
      button2 = new Rectangle(ox + width - buttonSize, oy, buttonSize, height);
      
      scroll = new Rectangle(ox + buttonSize, oy, width - buttonSize*2, height);
      scrollButton = new Dimension(buttonSize,height);
      
      length = new Point(width - buttonSize*3,0);
      base = new Point(ox + buttonSize, oy);
    }
    else
    {
      button1 = new Rectangle(ox, oy, width, buttonSize);
      button2 = new Rectangle(ox, oy + height - buttonSize, width, buttonSize);
      
      scroll = new Rectangle(ox, oy + buttonSize, width, height - buttonSize*2);
      scrollButton = new Dimension(width, buttonSize);
      
      length = new Point(0,height - buttonSize*3);
      base = new Point(ox, oy + buttonSize);

    }

  }
  
  public boolean isInside(int x, int y)
  {
    return x >= ox && x < ox + width && y >= oy && y < oy + height;
  }
  
  public void mouseReleased(int x, int y)
  {
    Point p = new Point(x,y);
    
    if (button1.contains(p))
    {
      progress -= progressStep;
      if (progress < 0.0f) progress = 0.0f;
    }
    else if (button2.contains(p))
    {
      progress += progressStep;
      if (progress >= 1.0f) progress = 1.0f;
    }
    else if (scroll.contains(p))
    {
      if (horizontal)
        this.progress = (x - ox - button1.width - buttonSize/2) / (float)length.x;
      else
        this.progress = (y - oy - button1.height - buttonSize/2) / (float)length.y;
    }
     
    Main.sketch.redraw();
  }
  
  public void mouseDragged(int x, int y)
  {
    x -= ox + button1.width;
    y -= oy + button1.height;
    
    float progress;
    
    if (horizontal)
      progress = (x - buttonSize/2) / (float)length.x;
    else
      progress = (y - buttonSize/2) / (float)length.y;
    
    if (progress >= 0.0 && progress <= 1.0)
      this.progress = progress;
    
    Main.sketch.redraw();
  }
  
  public void mouseMoved(int x, int y)
  {
    
  }
  
  public void mouseExited()
  {
    
  }
  
  public void draw()
  {
    p.strokeCap(Sketch.PROJECT);
   
    p.fill(80);
    p.stroke(0);

    p.rect(button1.x, button1.y, button1.width, button1.height);
    p.rect(button2.x, button2.y, button2.width, button2.height);
    
    p.fill(160);
    p.rect(scroll.x, scroll.y, scroll.width, scroll.height);
    
    p.fill(120);
    p.rect(base.x+length.x*progress, base.y+length.y*progress, scrollButton.width, scrollButton.height);
  }

}
