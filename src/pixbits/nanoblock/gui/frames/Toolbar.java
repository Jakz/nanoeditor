package pixbits.nanoblock.gui.frames;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.*;

public class Toolbar
{
  private static enum ItemType
  {
    BUTTON,
    TOGGLE
  }
  
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
    
    EDIT_ENABLE_HALF_STEPS(Icon.ENABLE_HALF_STEPS, Setting.HALF_STEPS_ENABLED, "Enable half steps", 0),
    EDIT_USE_TAB_TO_ROTATE(Icon.USE_TAB_ROTATION, Setting.USE_TAB_TO_ROTATE, "Use TAB to rotate", 0),
    
    SEPARATOR(null, null, null)
    ;
    
    Item(Icon icon, Task task, String tooltip)
    {
      this.icon = icon != null ? icon.icon() : null;
      this.task = task;
      this.tooltip = tooltip;
      this.type = ItemType.BUTTON;
      this.setting = null;
    }
    
    Item(Icon icon, Setting setting, String tooltip, int dummy)
    {
      this.icon = icon != null ? icon.icon() : null;
      this.task = null;
      this.tooltip = tooltip;
      this.type = ItemType.TOGGLE;
      this.setting = setting;
    }
    
    public final String tooltip;
    public final ImageIcon icon;
    public final ItemType type;
    
    public final Setting setting;
    
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
    
    Item.MODEL_DELETE_LEVEL,
    
    Item.SEPARATOR,
    
    Item.EDIT_ENABLE_HALF_STEPS,
    Item.EDIT_USE_TAB_TO_ROTATE
  };
  
  private final static Item[] LIBRARY_ITEMS = {
    Item.LIBRARY_MODEL_NEW,
    Item.LIBRARY_MODEL_DUPLICATE,
    Item.SEPARATOR,
    Item.LIBRARY_MODEL_DELETE
    
  };
  
  private final static Map<AbstractButton, Item> mapping = new HashMap<AbstractButton, Item>();
  private final static Map<Item, AbstractButton> rmapping = new HashMap<Item, AbstractButton>();

  
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
        AbstractButton button = null;
        
        if (item.setting != null)
        {
          JToggleButton tbutton = new JToggleButton(item.icon);
          tbutton.setSelected(Settings.values.get(item.setting));
          button = tbutton; 
        }
        else
          button = new JButton(item.icon);
        
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
      AbstractButton src = (AbstractButton)e.getSource();
      Item item = mapping.get(src);
      
      if (item.task != null)
        item.task.execute();
      else if (item.setting != null)
      {
        Settings.values.toggle(item.setting);
        JMenuItem mi = Menus.findItem(item.setting);
        if (mi != null) mi.setSelected(src.isSelected());
        
        if (item.setting == Setting.USE_TAB_TO_ROTATE)
          Main.sketch.updatePiecePalette();
      }
      
      Main.sketch.redraw();
      Main.sketch.requestFocus();
    }
  };
  
  public static AbstractButton findItem(Setting setting)
  {
    for (Item i : Item.values())
      if (i.setting == setting)
        return rmapping.get(i);
    
    return null;
  }
  
  public static void toggleLevelSpecificEntries(boolean enable)
  {
    rmapping.get(Item.MODEL_INSERT_LEVEL_ABOVE).setEnabled(enable);
    rmapping.get(Item.MODEL_INSERT_LEVEL_BELOW).setEnabled(enable);
    rmapping.get(Item.MODEL_SHIFT_LEVEL_UP).setEnabled(enable);
    rmapping.get(Item.MODEL_SHIFT_LEVEL_DOWN).setEnabled(enable);
    rmapping.get(Item.MODEL_DELETE_LEVEL).setEnabled(enable);

  }
}
