package pixbits.nanoblock.gui.frames;

import javax.swing.JOptionPane;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.tasks.Task;

public class Dialogs
{
  public static boolean drawConfirmDialog(String title, String text, Task taskOnYes)
  {
    int result = JOptionPane.showConfirmDialog(Main.mainFrame, text, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (result == JOptionPane.YES_OPTION)
    {
      taskOnYes.execute();
      return true;
    }
    
    return false;
  }
}
