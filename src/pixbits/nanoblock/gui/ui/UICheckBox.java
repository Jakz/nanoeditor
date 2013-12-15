package pixbits.nanoblock.gui.ui;

import pixbits.nanoblock.gui.*;

public class UICheckBox extends Drawable
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
    return x >= ox && x < ox + boxSize && y >= oy && y < oy + boxSize;
  }
  
  public void mouseReleased(int x, int y)
  { 
    selected = !selected;
    p.redraw();
  }
  
  
  public void mouseDragged(int x, int y) { }
  public void mouseMoved(int x, int y) { }
  public void mouseExited() { }
  public void mouseWheelMoved(int x) { }
  
  public void draw()
  {
    p.stroke(0);
    p.fill(160);
    p.strokeWeight(1.0f);
    
    p.rect(ox, oy, boxSize, boxSize);
        
    p.textFont(p.font);
    p.fill(0);
    p.text(text, ox + boxSize + 10, oy + boxSize/2 + p.getFontMetrics(p.font.getFont()).getHeight()/2);
    
    p.strokeWeight(3.0f);
    if (!selected)
    {
      p.line(ox+4, oy+4, ox+boxSize-4, oy+boxSize-4);
      p.line(ox+boxSize-4, oy+4, ox+4, oy+boxSize-4);
    }
  }
}
