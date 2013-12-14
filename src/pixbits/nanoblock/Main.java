package pixbits.nanoblock;

import pixbits.nanoblock.files.*;
import pixbits.nanoblock.gui.*;

public class Main
{
  public static final int SW = 1440;
  public static final int SH = 900;
  
  
  public static Sketch sketch;
  public static MainFrame mainFrame;
  
  public static boolean drawCaps = true;
  
  public static void main(String[] args)
  {
    TileSetLoader.loadTileset("tileset.json");
    
    sketch = new Sketch();
    mainFrame = new MainFrame();
    mainFrame.setLocationRelativeTo(null);
  }
}
