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
    MODEL_ROTATE_RIGHT(Icon.ROTATE_RIGHT, Tasks.MODEL_ROTATE_EAST, "Rotate Right"),

    LIBRARY_MODEL_NEW(Icon.MODEL_NEW, Tasks.LIBRARY_NEW_MODEL, "New Model"),
    LIBRARY_MODEL_DUPLICATE(Icon.MODEL_DUPLICATE, Tasks.LIBRARY_CLONE_MODEL, "Duplicate Model"),
    LIBRARY_MODEL_DELETE(Icon.MODEL_DELETE, Tasks.LIBRARY_DELETE_MODEL, "Delete Model"),
    
    SEPARATOR(null, null, null)
    ;
    
    Item(Icon icon, Task task, String tooltip)
    {
      this.icon = icon != null ? icon.icon() : null;
      this.task = task;
      this.tooltip = tooltip;
    }
    
    public final String tooltip;
    public final ImageIcon icon;
    
    public final Task task;
  }
  
  private final static Item[] EDITOR_ITEMS = {
    Item.MODEL_ROTATE_LEFT,
    Item.MODEL_ROTATE_RIGHT
  };
  
  private final static Item[] LIBRARY_ITEMS = {
    Item.LIBRARY_MODEL_NEW,
    Item.LIBRARY_MODEL_DUPLICATE,
    Item.SEPARATOR,
    Item.LIBRARY_MODEL_DELETE
    
  };
  
  private final static Map<JButton, Item> mapping = new HashMap<JButton, Item>();
  
  public static JToolBar buildEditorToolbar() { return buildToolbar(EDITOR_ITEMS); }
  public static JToolBar buildLibraryToolbar() { return buildToolbar(LIBRARY_ITEMS); }

  
  private static JToolBar buildToolbar(Item[] items)
  {
    JToolBar bar = new JToolBar();
    
    for (Item item : items)
    {
      if (item == Item.SEPARATOR)
        bar.add(new JToolBar.Separator());
      else if (item.icon != null)
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
