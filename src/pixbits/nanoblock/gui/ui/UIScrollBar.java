package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.gui.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;

public abstract class UIScrollBar extends Node
{
  final int buttonSize;
  int width;
  int height;
  final boolean smoothScroll = false;
  
  Point length;
  Point base;
  Dimension scrollButton;
  
  Rectangle button1, button2;
  Rectangle scroll;
  
  final boolean horizontal;
  
  float progress = 0.0f;
  int progressReal = 0;
  final int progressStep = 1;
    
  public UIScrollBar(Sketch p, int ox, int oy, int width, int height, int buttonSize)
  {
    super(p,ox,oy);
    this.width = width;
    this.height = height;
    this.buttonSize = buttonSize;
    horizontal = width > height;
    revalidate();
  }
  
  @Override
  public void revalidate()
  {
    if (horizontal)
    {
      button1 = new Rectangle(x, y, buttonSize, height);
      button2 = new Rectangle(x + width - buttonSize, y, buttonSize, height);
      
      scroll = new Rectangle(x + buttonSize, y, width - buttonSize*2, height);
      scrollButton = new Dimension(buttonSize,height);
      
      length = new Point(width - buttonSize*3,0);
      base = new Point(x + buttonSize, y);
    }
    else
    {
      button1 = new Rectangle(x, y, width, buttonSize);
      button2 = new Rectangle(x, y + height - buttonSize, width, buttonSize);
      
      scroll = new Rectangle(x, y + buttonSize, width, height - buttonSize*2);
      scrollButton = new Dimension(width, buttonSize);
      
      length = new Point(0, height - buttonSize*3);
      base = new Point(x, y + buttonSize);
    }
  }
  
  @Override
  public void setPosition(int x, int y)
  {
    super.setPosition(x, y);
    revalidate();
  }
  
  public void setSize(int w, int h)
  {
    this.width = w;
    this.height = h;
    revalidate();
  }
  
  public int width() { return width; }
  public int height() { return height; }

  public boolean isInside(int x, int y)
  {
    return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height;
  }
  
  public void draggingReset()
  {
    /*if (smoothScroll && dragging)
    {
      setProgressFromPercent(progress);
    }*/
    
    super.draggingReset();
  }

  public void setProgressFromPercent(float progress)
  {
    float steps = max() - min();
    float floatStep = 1.0f / steps;
    
    progress *= steps;
    progressReal = Math.round(progress);
    this.progress = progressReal*floatStep;

    progressChanged(progressReal);
  }
  
  public void upArrow()
  {
    progressReal -= progressStep;
    if (progressReal < min()) progressReal = min();
    
    progress = progressReal/(float)max();
    progressChanged(progressReal);
  }
  
  public void downArrow()
  {
    progressReal += progressStep;
    if (progressReal >= max()) progressReal = max();
    
    progress = progressReal/(float)max();
    progressChanged(progressReal);
  }
  
  public void mouseWheelMoved(int ___, int __, int v)
  {
    if (v < 0)
      upArrow();
    else
      downArrow();
    
    Main.sketch.redraw();
  }

  public void mouseReleased(int x, int y, int b)
  {
    Point p = new Point(x,y);
    
    if (button1.contains(p))
    {
      upArrow();
    }
    else if (button2.contains(p))
    {
      downArrow();
    }
    else if (scroll.contains(p))
    {
      float progress;
      
      if (horizontal)
        progress = (x - this.x - button1.width - buttonSize/2) / (float)length.x;
      else
        progress = (y - this.y - button1.height - buttonSize/2) / (float)length.y;
      
      setProgressFromPercent(progress);
    }
         
    Main.sketch.redraw();
  }
  
  public void mouseDragged(int x, int y, int b)
  {
    if (!dragging)
    {
      if (scroll.contains(new Point(x,y)))
        dragging = true;
      else
        return;
    }
    
    x -= this.x + button1.width;
    y -= this.y + button1.height;
    
    float progress;

    if (horizontal)
      progress = (x - buttonSize/2) / (float)length.x;
    else
      progress = (y - buttonSize/2) / (float)length.y;
    
    if (progress < 0.0f)
      progress = 0.0f;
    else if (progress > 1.0f)
      progress = 1.0f;

    if (smoothScroll)
      this.progress = progress;
    else
      setProgressFromPercent(progress);
    
    Main.sketch.redraw();
  }
  
  public void mouseMoved(int x, int y)
  {
    
  }
  
  public void mouseExited()
  {
    
  }
  
  public abstract int min();
  public abstract int max();
  public abstract void progressChanged(int value);
  
  public void draw()
  {
    p.strokeWeight(1.0f);
    p.strokeJoin(Sketch.MITER);
   
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
