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
    MODEL_SHIFT_NORTH(Icon.SHIFT_NORTH, Tasks.MODEL_SHIFT_NORTH, "Shift North"),
    MODEL_SHIFT_EAST(Icon.SHIFT_EAST, Tasks.MODEL_SHIFT_EAST, "Shift East"),
    MODEL_SHIFT_SOUTH(Icon.SHIFT_SOUTH, Tasks.MODEL_SHIFT_SOUTH, "Shift South"),
    MODEL_SHIFT_WEST(Icon.SHIFT_WEST, Tasks.MODEL_SHIFT_WEST, "Shift West"),
    
    MODEL_ROTATE_LEFT(Icon.ROTATE_LEFT, Tasks.MODEL_ROTATE_WEST, "Rotate Left"),
    MODEL_ROTATE_RIGHT(Icon.ROTATE_RIGHT, Tasks.MODEL_ROTATE_EAST, "Rotate Right"),
    MODEL_RESIZE(Icon.MODEL_RESIZE, Tasks.MODEL_SHOW_RESIZE, "Resize"),
    MODEL_REPLACE_COLOR(Icon.MODEL_REPLACE_COLOR, Tasks.MODEL_SHOW_REPLACE_COLOR, "Replace Color"),
    
    MODEL_INSERT_LEVEL_ABOVE(Icon.INSERT_LEVEL_ABOVE, Tasks.MODEL_INSERT_LEVEL_ABOVE, "Insert Level Above"),
    MODEL_INSERT_LEVEL_BELOW(Icon.INSERT_LEVEL_BELOW, Tasks.MODEL_INSERT_LEVEL_BELOW, "Insert Level Below"),
    
    MODEL_SHIFT_LEVEL_UP(Icon.SHIFT_LEVEL_UP, Tasks.MODEL_SHIFT_LEVEL_UP, "Shift Level Up"),
    MODEL_SHIFT_LEVEL_DOWN(Icon.SHIFT_LEVEL_DOWN, Tasks.MODEL_SHIFT_LEVEL_DOWN, "Shift Level Down"),
    
    MODEL_DELETE_LEVEL(Icon.DELETE_LEVEL, Tasks.MODEL_DELETE_LEVEL, "Delete Level"),

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
    Item.MODEL_SHIFT_NORTH,
    Item.MODEL_SHIFT_SOUTH,
    Item.MODEL_SHIFT_WEST,
    Item.MODEL_SHIFT_EAST,
    
    Item.SEPARATOR,
    
    Item.MODEL_ROTATE_LEFT,
    Item.MODEL_ROTATE_RIGHT,
    
    Item.SEPARATOR,
    
    Item.MODEL_RESIZE,
    Item.MODEL_REPLACE_COLOR,
    
    Item.SEPARATOR,
    
    Item.MODEL_INSERT_LEVEL_ABOVE,
    Item.MODEL_INSERT_LEVEL_BELOW,
    
    Item.SEPARATOR,

    
    Item.MODEL_SHIFT_LEVEL_UP,
    Item.MODEL_SHIFT_LEVEL_DOWN,
    
    Item.SEPARATOR,
    
    Item.MODEL_DELETE_LEVEL
  };
  
  private final static Item[] LIBRARY_ITEMS = {
    Item.LIBRARY_MODEL_NEW,
    Item.LIBRARY_MODEL_DUPLICATE,
    Item.SEPARATOR,
    Item.LIBRARY_MODEL_DELETE
    
  };
  
  private final static Map<JButton, Item> mapping = new HashMap<JButton, Item>();
  private final static Map<Item, JButton> rmapping = new HashMap<Item, JButton>();

  
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
        rmapping.put(item, button);
        
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
  
  public static void toggleLevelSpecificEntries(boolean enable)
  {
    rmapping.get(Item.MODEL_INSERT_LEVEL_ABOVE).setEnabled(enable);
    rmapping.get(Item.MODEL_INSERT_LEVEL_BELOW).setEnabled(enable);
    rmapping.get(Item.MODEL_SHIFT_LEVEL_UP).setEnabled(enable);
    rmapping.get(Item.MODEL_SHIFT_LEVEL_DOWN).setEnabled(enable);
    rmapping.get(Item.MODEL_DELETE_LEVEL).setEnabled(enable);

  }
}
