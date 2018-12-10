package pixbits.nanoblock;

import pixbits.nanoblock.data.PieceType;
import pixbits.nanoblock.files.*;
import pixbits.nanoblock.gui.*;
import pixbits.nanoblock.gui.frames.*;
import pixbits.nanoblock.tasks.Tasks;
import pixbits.nanoblock.tasks.UndoManager;

import java.nio.file.Paths;

import javax.swing.JPopupMenu;

import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.WrapperFrame;

public class Main
{
  public static final int SW = 1440;
  public static final int SH = 1200;
  
  
  public static Sketch sketch;
  public static MainFrame mainFrame;
  public static LibraryFrame libraryFrame;
  
  public static ResizeModelFrame resizeModelFrame;
  public static ReplaceColorFrame replaceColorFrame;
  public static PreferencesFrame preferencesFrame;
  
  private static WrapperFrame<HistoryPanel> historyFrame;
  
  public static void main(String[] args)
  {
    UIUtils.setNimbusLNF();
    
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    Tasks.loadSettings();
    
    PieceType.initMapping();
    Brush.tileset = TileSetLoader.loadAndBuild(Paths.get("tileset.json"));
    
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

    if (!Library.i().getModels().isEmpty())
      Tasks.LIBRARY_OPEN_IN_EDITOR.execute(Library.i().getModels().get(0));
    else
      libraryFrame.showMe();

    
    resizeModelFrame = new ResizeModelFrame();
    replaceColorFrame = new ReplaceColorFrame();
    preferencesFrame = new PreferencesFrame();
    
    historyFrame = UIUtils.buildFrame(new HistoryPanel(UndoManager.undos), "History");
    historyFrame.setVisible(true);
    
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
