package pixbits.nanoblock.gui.frames;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.Tasks;

public class Menus
{
  private static enum ItemType
  {
    BUTTON,
    CHECKBOX
  }
  
  private static enum Item
  {
    FILE_NEW("New.."),
    FILE_OPEN("Open.."),
    FILE_SAVE_AS("Save as.."),
    FILE_SAVE("Save"),
    FILE_EXIT("Exit"),
    
    EDIT_HALF_STEPS("Use half steps", ItemType.CHECKBOX, Setting.HALF_STEPS_ENABLED),
    
    VIEW_HIDE_CAPS("Draw caps", ItemType.CHECKBOX, Setting.DRAW_CAPS),
    VIEW_SHOW_PIECE_ORDER("Show piece order", ItemType.CHECKBOX, Setting.SHOW_PIECE_ORDER),
    
    SEPARATOR(null),
    
    ;
    
    public final String caption;
    public final ItemType type;
    public final Setting setting;
    
    Item(String caption, ItemType type, Setting setting) { this.caption = caption; this.type = type; this.setting = setting; }
    Item(String caption) { this(caption,ItemType.BUTTON,null); }
  }
  
  private static final String[] menus = {"File", "Edit", "View"};
  private static final Item[][] menuItems = new Item[][]{
    new Item[]{Item.FILE_NEW, Item.FILE_OPEN, Item.SEPARATOR, Item.FILE_SAVE_AS, Item.FILE_SAVE, Item.SEPARATOR, Item.FILE_EXIT},
    new Item[]{Item.EDIT_HALF_STEPS},
    new Item[]{Item.VIEW_HIDE_CAPS, Item.SEPARATOR, Item.VIEW_SHOW_PIECE_ORDER}
  };
  
  private static final Map<JMenuItem, Item> mapping = new HashMap<JMenuItem, Item>();
  
  
  public static void buildMenu(JFrame frame)
  {
    JMenuBar bar = new JMenuBar();
    
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    
    for (int i = 0; i < menus.length; ++i)
    {
      JMenu menu = new JMenu(menus[i]);
      
      Item[] items = menuItems[i];
      
      for (int j = 0; j < items.length; ++j)
      {
        if (items[j] != Item.SEPARATOR)
        {
          JMenuItem item = null; 
          
          switch (items[j].type)
          {
            case BUTTON: item = new JMenuItem(items[j].caption); break;
            case CHECKBOX:
            {
              item = new JCheckBoxMenuItem(items[j].caption); 
              boolean value = Settings.values.get(items[j].setting);
              ((JCheckBoxMenuItem)item).setSelected(value);
              break;
            }
             
          }
          
          item.addActionListener(menuListener);
          mapping.put(item, items[j]);
          menu.add(item);
        }
        else
          menu.addSeparator();
      }
      
      bar.add(menu);
        
    }

    frame.setJMenuBar(bar);
  }
  
  private static ActionListener menuListener = new ActionListener() {
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
      
        case VIEW_SHOW_PIECE_ORDER:
        case VIEW_HIDE_CAPS:
          
        case EDIT_HALF_STEPS:
        {
          Settings.values.toggle(item.setting);
          Main.sketch.redraw();
          break;
        }
      
        default: break;
      }
    }
  };
}
