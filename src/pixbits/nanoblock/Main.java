package pixbits.nanoblock;

import pixbits.nanoblock.gui.*;

public class Main
{
  public static final int SW = 1280;
  public static final int SH = 800;
  
  public static Sketch sketch;
  public static MainFrame mainFrame;
  
  public static void main(String[] args)
  {
    sketch = new Sketch();
    mainFrame = new MainFrame();
  }
}
