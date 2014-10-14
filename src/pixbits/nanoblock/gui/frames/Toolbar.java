package pixbits.nanoblock.gui.frames;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import pixbits.nanoblock.tasks.*;

public class Toolbar
{
  private static enum Item
  {
    MODEL_ROTATE_LEFT(Icon.ROTATE_LEFT, Tasks.MODEL_ROTATE_WEST, "Rotate Left"),
    MODEL_ROTATE_RIGHT(Icon.ROTATE_RIGHT, Tasks.MODEL_ROTATE_EAST, "Rotate Right")

    ;
    
    Item(Icon icon, Task task, String tooltip)
    {
      this.icon = icon.icon();
      this.task = task;
      this.tooltip = tooltip;
    }
    
    public final String tooltip;
    public final ImageIcon icon;
    
    public final Task task;
  }
  
  private final static Item[] items = {
    Item.MODEL_ROTATE_LEFT,
    Item.MODEL_ROTATE_RIGHT
  };
  
  private final static Map<JButton, Item> mapping = new HashMap<JButton, Item>();
  
  public static JToolBar buildToolbar()
  {
    JToolBar bar = new JToolBar();
    
    for (Item item : items)
    {
      if (item.icon != null)
      {
        JButton button = new JButton(item.icon);
        
        if (item.tooltip != null)
          button.setToolTipText(item.tooltip);
        
        button.addActionListener(buttonListener);
        mapping.put(button, item);
        
        bar.add(button);
      }
    }
    
    bar.setFloatable(false);
    
    return bar;
  }
  
  private static final ActionListener buttonListener = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      JButton src = (JButton)e.getSource();
      Item item = mapping.get(src);
      
      if (item.task != null)
        item.task.execute();
    }
  };
}
