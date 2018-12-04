package pixbits.nanoblock.data;

import pixbits.nanoblock.gui.Sketch;

public class PieceOutline
{
  public final int[] steps;
    
  public PieceOutline(int[] steps)
  {
    this.steps = steps;
    
  }
  
  public void render(Sketch p, int x, int y, int ox, int oy, int m)
  {
    boolean hor = true;
    
    p.beginShape();
    p.vertex(ox + x*m, oy + y*m);
    for (int step : steps)
    {
      if (hor)
        x += step*2;
      else
        y += step*2;
      
      p.vertex(ox + x*m, oy + y*m);

      hor = !hor;
    }
    
    p.endShape(Sketch.CLOSE);
  }
}
