package pixbits.nanoblock.gui.menus;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Direction;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.gui.frames.Icon;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.*;

public class Toolbar
{
  private final static Item[] EDITOR_ITEMS = {
    Item.MODEL_SHIFT_NORTH,
    Item.MODEL_SHIFT_SOUTH,
    Item.MODEL_SHIFT_WEST,
    Item.MODEL_SHIFT_EAST,
    
    Item.SEPARATOR,
    
    Item.MODEL_ROTATE_LEFT,
    Item.MODEL_ROTATE_RIGHT,
    
    Item.SEPARATOR,
    
    Item.EDIT_RESIZE,
    Item.EDIT_REPLACE_COLOR,
    
    Item.SEPARATOR,
    
    Item.MODEL_INSERT_LEVEL_ABOVE,
    Item.MODEL_INSERT_LEVEL_BELOW,
    
    Item.SEPARATOR,

    
    Item.MODEL_SHIFT_LEVEL_UP,
    Item.MODEL_SHIFT_LEVEL_DOWN,
    
    Item.SEPARATOR,
    
    Item.MODEL_DELETE_LEVEL,
    
    Item.SEPARATOR,
    
    Item.EDIT_HALF_STEPS,
    Item.EDIT_USE_TAB_TO_ROTATE
  };
  
  private final static Item[] LIBRARY_ITEMS = {
    Item.LIBRARY_MODEL_NEW,
    Item.LIBRARY_MODEL_DUPLICATE,
    Item.SEPARATOR,
    Item.LIBRARY_MODEL_DELETE
    
  };
  
  private final static Map<AbstractButton, Item> mapping = new HashMap<AbstractButton, Item>();
  
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
        item.buttonToolbar = button;
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
      else if (item.builder != null)
      {
        OperationBuilder builder = item.builder;
        UndoableTask task = builder.build(Library.model);
        task.execute();
      }
      else if (item.setting != null)
      {
        Settings.values.toggle(item.setting);
        JMenuItem mi = Menus.findItem(item.setting);
        if (mi != null) mi.setSelected(src.isSelected());
        
        if (item.setting == Setting.USE_TAB_TO_ROTATE)
          Main.sketch.updatePiecePalette();
        
        Main.sketch.requestFocus();
      }
      
      Main.sketch.redraw();
      
    }
  };
  
  public static AbstractButton findItem(Setting setting)
  {
    for (Item i : Item.values())
      if (i.setting == setting)
        return i.buttonToolbar;
    
    return null;
  }

}
