package pixbits.nanoblock.gui.frames;

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
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.*;

public class Menus
{
  private static enum ItemType
  {
    BUTTON,
    CHECKBOX,
    RADIO,
    SUBMENU
  }
  
  private static enum Item
  {
    FILE_NEW("New.."),
    FILE_OPEN("Open.."),
    FILE_SAVE_AS("Save as.."),
    FILE_SAVE("Save"),
    FILE_EXPORT("Export.."),
    FILE_EXPORT_INSTRUCTIONS("Export instructions.."),
    
    FILE_EXIT("Exit"),
    
    EDIT_HALF_STEPS("Use half steps", ItemType.CHECKBOX, Setting.HALF_STEPS_ENABLED),
    EDIT_RESET("Reset"),
    
    VIEW_HIDE_CAPS("Draw caps", ItemType.CHECKBOX, Setting.DRAW_CAPS),
    VIEW_SHOW_PIECE_ORDER("Show piece order", ItemType.CHECKBOX, Setting.SHOW_PIECE_ORDER),
    
    VIEW_SHOW_LAYER_GRID("Enable", ItemType.CHECKBOX, Setting.VIEW_SHOW_LAYER_GRID),
    VIEW_LAYER_GRID_ALPHA("Use opacity", ItemType.CHECKBOX, Setting.VIEW_LAYER_GRID_ALPHA),
    VIEW_HOVER_LAYER_MENU("Isometric Layer", new Item[]{ VIEW_SHOW_LAYER_GRID, VIEW_LAYER_GRID_ALPHA} ),
   
    VIEW_HOVER_PIECE_DISABLE("Disable", Setting.HoverSetter.INSTANCE, Setting.HoverPiece.GROUP, Setting.HoverPiece.DISABLE),
    VIEW_HOVER_PIECE_STROKE_BACK("Behind stroke", Setting.HoverSetter.INSTANCE, Setting.HoverPiece.GROUP, Setting.HoverPiece.BACK_STROKE),
    VIEW_HOVER_PIECE_STROKE_FRONT("Front stroke", Setting.HoverSetter.INSTANCE, Setting.HoverPiece.GROUP, Setting.HoverPiece.FRONT_STROKE),
    VIEW_HOVER_PIECE_FILL("Front stroke with back fill", Setting.HoverSetter.INSTANCE, Setting.HoverPiece.GROUP, Setting.HoverPiece.FRONT_STROKE_WITH_BACK_FILL),
    VIEW_HOVER_PIECE_MENU("Isometric Piece", new Item[] {VIEW_HOVER_PIECE_DISABLE, VIEW_HOVER_PIECE_STROKE_BACK, VIEW_HOVER_PIECE_STROKE_FRONT, VIEW_HOVER_PIECE_FILL}),
    
    MODEL_SHIFT_NORTH("North ^", Tasks.MODEL_SHIFT_NORTH),
    MODEL_SHIFT_SOUTH("South v", Tasks.MODEL_SHIFT_SOUTH),
    MODEL_SHIFT_WEST("West <-", Tasks.MODEL_SHIFT_WEST),
    MODEL_SHIFT_EAST("East ->", Tasks.MODEL_SHIFT_EAST),
    MODEL_SHIFT_MENU("Shift", new Item[] {MODEL_SHIFT_NORTH, MODEL_SHIFT_SOUTH, MODEL_SHIFT_WEST, MODEL_SHIFT_EAST} ),
    
    MODEL_ROTATE_LEFT("Left", Tasks.MODEL_ROTATE_WEST),
    MODEL_ROTATE_RIGHT("Right", Tasks.MODEL_ROTATE_EAST),
    MODEL_ROTATE_MENU("Rotate", new Item[] { MODEL_ROTATE_LEFT, MODEL_ROTATE_RIGHT }),
    
    SEPARATOR(null),
    
    ;
    
    public final String caption;
    public final ItemType type;
    public final Setting setting;
    public final Task task;
    public final Item[] items;
    public final Enum radioValue;
    public final Setting.EnumSetter radioSetter;
    public final ButtonGroup radioGroup;
    
    Item(String caption, ItemType type, Setting setting) { this(caption, type, setting, null); }
    Item(String caption, ItemType type, Setting setting, Task task)
    { 
      this.caption = caption; 
      this.type = type; 
      this.setting = setting; 
      this.task = task; 
      this.items = null;
      this.radioValue = null;
      this.radioGroup = null;
      this.radioSetter = null;
    }
    Item(String caption) { this(caption, ItemType.BUTTON, null, null); }
    Item(String caption, Task task) { this(caption, ItemType.BUTTON, null, task); }
    
    Item(String caption, Item[] items)
    {
      this.caption = caption;
      type = ItemType.SUBMENU;
      setting = null;
      task = null;
      this.items = items;
      this.radioValue = null;
      this.radioGroup = null;
      this.radioSetter = null;
    }
    
    Item(String caption, Setting.EnumSetter setter, ButtonGroup radioGroup, Enum radioValue)
    {
      this.caption = caption;
      type = ItemType.RADIO;
      setting = null;
      task = null;
      this.items = null;
      this.radioValue = radioValue;
      this.radioGroup = radioGroup;
      this.radioSetter = setter;
    }
  }
  
  private static final String[] menus = {"File", "Edit", "Model", "View"};
  private static final Item[][] menuItems = new Item[][]{
    new Item[]{Item.FILE_NEW, Item.FILE_OPEN, Item.SEPARATOR, Item.FILE_SAVE_AS, Item.FILE_SAVE, Item.SEPARATOR, Item.FILE_EXPORT, Item.FILE_EXPORT_INSTRUCTIONS, Item.SEPARATOR, Item.FILE_EXIT},
    new Item[]{Item.EDIT_HALF_STEPS, Item.SEPARATOR, Item.EDIT_RESET},
    new Item[]{Item.MODEL_SHIFT_MENU, Item.MODEL_ROTATE_MENU},
    new Item[]{Item.VIEW_HIDE_CAPS, Item.SEPARATOR, Item.VIEW_HOVER_LAYER_MENU, Item.VIEW_HOVER_PIECE_MENU, Item.SEPARATOR, Item.VIEW_SHOW_PIECE_ORDER}
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
        
        case VIEW_HOVER_PIECE_DISABLE:
        case VIEW_HOVER_PIECE_FILL:
        case VIEW_HOVER_PIECE_STROKE_BACK:
        case VIEW_HOVER_PIECE_STROKE_FRONT:
        {
          Setting.EnumSetter setter = item.radioSetter;
          Enum value = item.radioValue;
          setter.set(value);
          Main.sketch.redraw();
          break;
        }
        
      
        case VIEW_SHOW_PIECE_ORDER:
        case VIEW_HIDE_CAPS:
        case VIEW_SHOW_LAYER_GRID:
        case VIEW_LAYER_GRID_ALPHA:
          
        case EDIT_HALF_STEPS:
        {
          Settings.values.toggle(item.setting);
          Main.sketch.redraw();
          break;
        }
        
        case EDIT_RESET:
        {
          if (Dialogs.drawConfirmDialog("Reset model", "Are you sure you want to reset the model?", Tasks.MODEL_RESET))
            Main.sketch.redraw();
          break;
        }
        
        case MODEL_SHIFT_NORTH:
        case MODEL_SHIFT_SOUTH:
        case MODEL_SHIFT_EAST:
        case MODEL_SHIFT_WEST:
        case MODEL_ROTATE_LEFT:
        case MODEL_ROTATE_RIGHT:
        {
          ModelTask mtask = (ModelTask)item.task;
          mtask.execute(Library.model);
        }
      
        default: break;
      }
    }
  };
}
