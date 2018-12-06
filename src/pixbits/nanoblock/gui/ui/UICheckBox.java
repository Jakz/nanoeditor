package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.gui.*;

public class UICheckBox extends Node
{
  final String text; 
  final int boxSize;
  private boolean selected;
  
  public UICheckBox(Sketch p, int ox, int oy, int boxSize, String text)
  {
    super(p, ox, oy);
    this.text = text;
    this.boxSize = boxSize;
  }
  
  
  public boolean isInside(int x, int y)
  { 
    return x >= this.x && x < this.x + boxSize && y >= this.y && y < this.y + boxSize;
  }
  
  public void mouseReleased(int x, int y, int b)
  { 
    selected = !selected;
    p.redraw();
  }
  
  
  public void mouseDragged(int x, int y, int b) { }
  public void mouseMoved(int x, int y) { }
  public void mouseExited() { }
  public void mouseWheelMoved(int x, int y, int v) { }
  
  public void draw()
  {
    p.stroke(0);
    p.fill(160);
    p.strokeWeight(1.0f);
    
    p.rect(x, y, boxSize, boxSize);
        
    p.textFont(p.font);
    p.fill(0);
    p.text(text, x + boxSize + 10, y + boxSize/2 + p.getFontMetrics(p.font.getFont()).getHeight()/2);
    
    p.strokeWeight(3.0f);
    if (!selected)
    {
      p.line(x+4, y+4, x+boxSize-4, y+boxSize-4);
      p.line(x+boxSize-4, y+4, x+4, y+boxSize-4);
    }
  }
}
