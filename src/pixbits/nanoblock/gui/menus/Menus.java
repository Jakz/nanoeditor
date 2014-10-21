package pixbits.nanoblock.gui.menus;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Direction;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.gui.frames.Dialogs;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.*;

public class Menus
{
  private static final String[] menus = {"File", "Edit", "Model", "View"};
  private static final Item[][] menuItems = new Item[][]{
    new Item[]{Item.FILE_SAVE, Item.FILE_CLOSE, Item.SEPARATOR, Item.FILE_EXPORT, Item.FILE_EXPORT_INSTRUCTIONS, Item.SEPARATOR, Item.FILE_EXIT},
    new Item[]{Item.EDIT_HALF_STEPS, Item.EDIT_USE_TAB_TO_ROTATE, Item.SEPARATOR, Item.EDIT_RESET, Item.SEPARATOR, Item.EDIT_RESIZE, Item.EDIT_REPLACE_COLOR},
    new Item[]{Item.MODEL_SHIFT_MENU, Item.MODEL_ROTATE_MENU, Item.SEPARATOR, Item.MODEL_INSERT_LEVEL_MENU, Item.MODEL_SHIFT_LEVEL_MENU, Item.MODEL_DELETE_LEVEL},
    new Item[]{Item.VIEW_HIDE_CAPS, Item.SEPARATOR, Item.VIEW_GRID_LAYER_MENU, Item.VIEW_HOVER_PIECE_MENU, Item.VIEW_HOVER_LAYER_MENU, Item.SEPARATOR, Item.VIEW_SHOW_PIECE_ORDER}
  };
  
  private static final Map<JMenuItem, Item> mapping = new HashMap<JMenuItem, Item>();
  
  private static JMenu buildMenu(String caption, Item[] items)
  {
    JMenu menu = new JMenu(caption);
    
    for (int i = 0; i < items.length; ++i)
    {
      if (items[i] != Item.SEPARATOR)
      {
        JMenuItem item = null; 
        
        switch (items[i].type)
        {
          case BUTTON: item = new JMenuItem(items[i].caption); break;
          case CHECKBOX:
          {
            item = new JCheckBoxMenuItem(items[i].caption); 
            boolean value = Settings.values.get(items[i].setting);
            ((JCheckBoxMenuItem)item).setSelected(value);
            break;
          }
          case RADIO:
          {
            item = new JRadioButtonMenuItem(items[i].caption);
            items[i].radioGroup.add(item);
            if (items[i].radioSetter.get() == items[i].radioValue)
              item.setSelected(true);
            
            break; 
          }
          case SUBMENU:
          {
            JMenu smenu = buildMenu(items[i].caption, items[i].items);
            menu.add(smenu);
            break;
          }
          
        }
        
        if (item != null)
        {
          item.addActionListener(menuListener);
          mapping.put(item, items[i]);
          items[i].buttonMenu = item;
          menu.add(item);
        }
      }
      else
        menu.addSeparator();
    }
    
    return menu;
  }
  
  public static JMenuBar buildMenu()
  {
    JMenuBar bar = new JMenuBar();
    
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    
    for (int i = 0; i < menus.length; ++i)
    {
      JMenu menu = buildMenu(menus[i], menuItems[i]);
      bar.add(menu);
    }

    return bar;
  }
  
  private static final ActionListener menuListener = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      JMenuItem src = (JMenuItem)e.getSource();
      Item item = mapping.get(src);
      
      System.out.println("Clicked "+item);
      
      switch (item)
      {
        case FILE_EXIT:
        {
          Tasks.saveModel();
          System.exit(0);
          break;
        }
        
        case FILE_SAVE:
        {
          Tasks.saveModel();
          break;
        }
        
        case FILE_CLOSE:
        {
          //TODO: warn to save
          Main.sketch.hideMe();
          Main.mainFrame.setVisible(false);
          Main.libraryFrame.setLocationRelativeTo(Main.mainFrame);
          Main.libraryFrame.showMe();
          break;
        }
        
        case VIEW_HOVER_PIECE_DISABLE:
        case VIEW_HOVER_PIECE_FILL:
        case VIEW_HOVER_PIECE_STROKE_BACK:
        case VIEW_HOVER_PIECE_STROKE_FRONT:
        {
          Setting.EnumSetter setter = item.radioSetter;
          Enum<?> value = item.radioValue;
          setter.set(value);
          Main.sketch.redraw();
          Main.sketch.requestFocus();
          break;
        }
        
      
        case VIEW_SHOW_PIECE_ORDER:
        case VIEW_HIDE_CAPS:
        case VIEW_SHOW_LAYER_GRID:
        case VIEW_LAYER_GRID_ALPHA:
          
        case VIEW_SHOW_GRID:
        case VIEW_SHOW_GRID_POINTS:
        case VIEW_MARK_DELETED_PIECE_ON_LAYER:
          
        case EDIT_HALF_STEPS:
        case EDIT_USE_TAB_TO_ROTATE:
        {
          Settings.values.toggle(item.setting);
          AbstractButton mi = Toolbar.findItem(item.setting);
          if (mi != null) mi.setSelected(src.isSelected());
          
          if (item.setting == Setting.USE_TAB_TO_ROTATE)
            Main.sketch.updatePiecePalette();
          
          Main.sketch.redraw();
          Main.sketch.requestFocus();
          break;
        }
        
        case EDIT_RESET:
        {
          if (Dialogs.showConfirmDialog(Main.mainFrame, "Reset model", "Are you sure you want to reset the model?", Tasks.MODEL_RESET))
          {
            Main.sketch.redraw();
            Main.sketch.requestFocus();
          }
          break;
        }
        
        case MODEL_ROTATE_LEFT:
        case MODEL_ROTATE_RIGHT:
        case MODEL_SHIFT_NORTH:
        case MODEL_SHIFT_SOUTH:
        case MODEL_SHIFT_EAST:
        case MODEL_SHIFT_WEST:
        {
          OperationBuilder builder = item.builder;
          UndoableTask task = builder.build(Library.model);
          task.execute();
          break;
        }
          
        case MODEL_INSERT_LEVEL_ABOVE:
        case MODEL_INSERT_LEVEL_BELOW:
        {
          ModelTask mtask = (ModelTask)item.task;
          mtask.execute(Library.model);
          Main.sketch.requestFocus();
        }
      
        default: break;
      }
    }
  };
  
  public static JMenuItem findItem(Setting setting)
  {
    for (Item i : Item.values())
      if (i.setting == setting)
        return i.buttonMenu;
    
    return null;
  }
}
