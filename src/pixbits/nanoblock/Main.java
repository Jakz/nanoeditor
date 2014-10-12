package pixbits.nanoblock;

import pixbits.nanoblock.files.*;
import pixbits.nanoblock.gui.*;
import pixbits.nanoblock.gui.frames.LibraryFrame;
import pixbits.nanoblock.gui.frames.MainFrame;

public class Main
{
  public static final int SW = 1440;
  public static final int SH = 900;
  
  
  public static Sketch sketch;
  public static MainFrame mainFrame;
  
  public static void main(String[] args)
  {
    TileSetLoader.loadTileset("tileset.json");
    
    try {
    Library.i().scan();
    } catch (Exception e) { e.printStackTrace(); }
    LibraryFrame libraryFrame = new LibraryFrame();
    libraryFrame.getModel().add(Library.i().getModels());
    libraryFrame.setVisible(true);
    
    sketch = new Sketch();
    mainFrame = new MainFrame();
    mainFrame.setLocationRelativeTo(null);
  }
}
