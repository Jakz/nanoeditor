package pixbits.nanoblock.gui.menus;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;

import pixbits.nanoblock.misc.Settings;

public class Menus
{
  private static final Map<AbstractButton, Item> mapping = new HashMap<AbstractButton, Item>();

  
  
  private static final String[] editorMenus = {"File", "Edit", "Model", "View"};
  private static final Item[][] editorMenuItems = new Item[][]{
    new Item[]{Item.FILE_SAVE, Item.FILE_CLOSE, Item.SEPARATOR, Item.FILE_EXPORT, Item.FILE_EXPORT_INSTRUCTIONS, Item.SEPARATOR, Item.FILE_EXIT},
    new Item[]{Item.EDIT_UNDO, Item.EDIT_REDO, Item.SEPARATOR, Item.EDIT_HALF_STEPS, Item.EDIT_USE_TAB_TO_ROTATE, Item.SEPARATOR, Item.EDIT_RESET, Item.SEPARATOR, Item.EDIT_RESIZE, Item.EDIT_REPLACE_COLOR},
    new Item[]{Item.MODEL_SHIFT_MENU, Item.MODEL_ROTATE_MENU, Item.SEPARATOR, Item.MODEL_INSERT_LEVEL_MENU, Item.MODEL_SHIFT_LEVEL_MENU, Item.MODEL_DELETE_LEVEL},
    new Item[]{Item.VIEW_HIDE_CAPS, Item.SEPARATOR, Item.VIEW_GRID_LAYER_MENU, Item.VIEW_HOVER_PIECE_MENU, Item.VIEW_HOVER_LAYER_MENU, Item.SEPARATOR, Item.VIEW_SHOW_PIECE_ORDER}
  };
  
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
    
    for (int i = 0; i < editorMenus.length; ++i)
    {
      JMenu menu = buildMenu(editorMenus[i], editorMenuItems[i]);
      bar.add(menu);
    }

    return bar;
  }
 
  
  
  
  
  private final static Item[] EDITOR_ITEMS = {
    Item.EDIT_UNDO,
    Item.EDIT_REDO,
    
    Item.SEPARATOR,
    
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
        
        button.addActionListener(toolbarListener);
        mapping.put(button, item);
        item.buttonToolbar = button;
        bar.add(button);
      }
    }
    
    bar.setFloatable(false);
    
    return bar;
  }

  
  
  
  
  
  private static final ActionListener menuListener = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      AbstractButton src = (AbstractButton)e.getSource();
      Item item = mapping.get(src);
      System.out.println("Clicked "+item);
      item.clicked(src, false);
    }
  };
  
  private static final ActionListener toolbarListener = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      AbstractButton src = (AbstractButton)e.getSource();
      Item item = mapping.get(src);
      System.out.println("Clicked "+item);
      item.clicked(src, true);
    }
  };
}
