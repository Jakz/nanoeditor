package pixbits.nanoblock;

import pixbits.nanoblock.files.*;
import pixbits.nanoblock.gui.*;
import pixbits.nanoblock.gui.frames.*;
import pixbits.nanoblock.gui.menus.Item;
import pixbits.nanoblock.tasks.Tasks;

import java.io.*;

import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

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
    if (System.getProperty("os.name").indexOf("Mac") == -1)
    {
      try {
          for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
              if ("Nimbus".equals(info.getName())) {
                  UIManager.setLookAndFeel(info.getClassName());
                  break;
              }
          }
      } catch (Exception e) {
          // If Nimbus is not available, you can set the GUI to another look and feel.
      }
    }
    
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    
    Tasks.loadSettings();
    
    sketch = new Sketch();
    mainFrame = new MainFrame();
    mainFrame.setLocationRelativeTo(null);
    
    try {
    Library.i().scan();
    } catch (Exception e) { e.printStackTrace(); }
    
    Library.i().computeMissingHashes();
    Library.i().fixModelFileNames();
    Library.i().cacheThumbnails();
    Library.i().deleteUselessThumbnails();
    
    libraryFrame = new LibraryFrame();
    libraryFrame.getModel().add(Library.i().getModels());
    libraryFrame.getModel().refresh();
    libraryFrame.showMe();
    
    resizeModelFrame = new ResizeModelFrame();
    replaceColorFrame = new ReplaceColorFrame();
    preferencesFrame = new PreferencesFrame();
    
    preferencesFrame.showMe();
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
