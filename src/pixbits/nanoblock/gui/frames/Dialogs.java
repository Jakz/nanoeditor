package pixbits.nanoblock.gui.frames;

import java.awt.Component;
import javax.swing.JOptionPane;

import pixbits.nanoblock.tasks.Task;

public class Dialogs
{
  public static boolean showConfirmDialog(Component parent, String title, String text, Task taskOnYes)
  {
    int result = JOptionPane.showConfirmDialog(parent, text, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (result == JOptionPane.YES_OPTION)
    {
      if (taskOnYes != null)
        taskOnYes.execute();
      return true;
    }
    
    return false;
  }
  
  public static void showErrorDialog(Component parent, String title, String text)
  {
    JOptionPane.showMessageDialog(parent, text, title, JOptionPane.ERROR_MESSAGE);
  }
  
  //public static void 
}
