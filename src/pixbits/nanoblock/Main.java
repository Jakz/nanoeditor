package pixbits.nanoblock;

import pixbits.nanoblock.files.*;
import pixbits.nanoblock.gui.*;
import pixbits.nanoblock.gui.frames.*;
import pixbits.nanoblock.tasks.Tasks;

import javax.swing.JPopupMenu;

import com.pixbits.lib.ui.UIUtils;

public class Main
{
  public static final int SW = 1440;
  public static final int SH = 900;
  
  
  public static Sketch sketch;
  public static MainFrame mainFrame;
  public static LibraryFrame libraryFrame;
  
  public static ResizeModelFrame resizeModelFrame;
  public static ReplaceColorFrame replaceColorFrame;
  public static PreferencesFrame preferencesFrame;
  
  public static void main(String[] args)
  {
    UIUtils.setNimbusLNF();
    
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    Tasks.loadSettings();
    
    sketch = new Sketch();
    mainFrame = new MainFrame();
    mainFrame.setLocationRelativeTo(null);
    
    try {
      Library.i().scan();
    } catch (Exception e) { e.printStackTrace(); }
    
    try
    {
      Library.i().computeMissingHashes();
      Library.i().fixModelFileNames();
      Library.i().cacheThumbnails();
      Library.i().deleteUselessThumbnails();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    libraryFrame = new LibraryFrame();
    libraryFrame.getModel().add(Library.i().getModels());
    libraryFrame.getModel().refresh();
    libraryFrame.showMe();
    
    resizeModelFrame = new ResizeModelFrame();
    replaceColorFrame = new ReplaceColorFrame();
    preferencesFrame = new PreferencesFrame();
    
    //preferencesFrame.showMe();
    //Main.mainFrame.setVisible(true);
  }
  
  public static void initAccordingToStatus()
  {
    // no models exists in library, create a new one and open directly editor
    if (Library.i().getModels().size() == 0)
    {
      
    }
  }
}
