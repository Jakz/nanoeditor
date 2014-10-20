package pixbits.nanoblock.gui.frames;

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
    SEPARATOR(null),
    
    FILE_NEW("New.."),
    FILE_OPEN("Open.."),
    FILE_SAVE_AS("Save as.."),
    FILE_SAVE("Save"),
    FILE_EXPORT("Export.."),
    FILE_EXPORT_INSTRUCTIONS("Export instructions.."),
    
    FILE_EXIT("Exit"),
    
    EDIT_HALF_STEPS("Use half steps", ItemType.CHECKBOX, Setting.HALF_STEPS_ENABLED),
    EDIT_USE_TAB_TO_ROTATE("Use tab to rotate", ItemType.CHECKBOX, Setting.USE_TAB_TO_ROTATE),
    EDIT_RESET("Reset"),
    EDIT_RESIZE("Resize", Tasks.MODEL_SHOW_RESIZE),
    EDIT_REPLACE_COLOR("Replace Color", Tasks.MODEL_SHOW_REPLACE_COLOR),

    
    VIEW_HIDE_CAPS("Draw caps", ItemType.CHECKBOX, Setting.DRAW_CAPS),
    VIEW_SHOW_PIECE_ORDER("Show piece order", ItemType.CHECKBOX, Setting.SHOW_PIECE_ORDER),
    
    VIEW_SHOW_LAYER_GRID("Enable", ItemType.CHECKBOX, Setting.VIEW_SHOW_LAYER_GRID),
    VIEW_LAYER_GRID_ALPHA("Use opacity", ItemType.CHECKBOX, Setting.VIEW_LAYER_GRID_ALPHA),
    VIEW_HOVER_LAYER_MENU("Isometric Layer", new Item[]{ VIEW_SHOW_LAYER_GRID, VIEW_LAYER_GRID_ALPHA} ),
    VIEW_SHOW_GRID_POINTS("Enable half grid points", ItemType.CHECKBOX, Setting.VIEW_SHOW_HALF_GRID_POINTS),
    VIEW_SHOW_GRID("Enable grid", ItemType.CHECKBOX, Setting.VIEW_SHOW_GRID_LINES),
    VIEW_MARK_DELETED_PIECE_ON_LAYER("Mark deletable piece", ItemType.CHECKBOX, Setting.VIEW_MARK_DELETED_PIECE_ON_LAYER),
    VIEW_GRID_LAYER_MENU("Layer Grid", new Item[] {VIEW_SHOW_GRID, VIEW_SHOW_GRID_POINTS, SEPARATOR, VIEW_MARK_DELETED_PIECE_ON_LAYER} ),
   
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
    
    MODEL_INSERT_LEVEL_ABOVE("Insert above", Tasks.MODEL_INSERT_LEVEL_ABOVE),
    MODEL_INSERT_LEVEL_BELOW("Insert below", Tasks.MODEL_INSERT_LEVEL_BELOW),
    MODEL_INSERT_LEVEL_MENU("Insert Level", new Item[] {MODEL_INSERT_LEVEL_ABOVE, MODEL_INSERT_LEVEL_BELOW} ),
    
    MODEL_SHIFT_LEVEL_UP("Shift up", Tasks.MODEL_SHIFT_LEVEL_UP),
    MODEL_SHIFT_LEVEL_DOWN("Shift down", Tasks.MODEL_SHIFT_LEVEL_DOWN),
    MODEL_SHIFT_LEVEL_MENU("Shift Level", new Item[] {MODEL_SHIFT_LEVEL_UP, MODEL_SHIFT_LEVEL_DOWN} ),
    
    MODEL_DELETE_LEVEL("Delete Level", Tasks.MODEL_DELETE_LEVEL)
    ;
    
    public final String caption;
    public final ItemType type;
    public final Setting setting;
    public final Task task;
    public final Item[] items;
    public final Enum<?> radioValue;
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
    
    Item(String caption, Setting.EnumSetter setter, ButtonGroup radioGroup, Enum<?> radioValue)
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
    new Item[]{Item.EDIT_HALF_STEPS, Item.EDIT_USE_TAB_TO_ROTATE, Item.SEPARATOR, Item.EDIT_RESET, Item.SEPARATOR, Item.EDIT_RESIZE, Item.EDIT_REPLACE_COLOR},
    new Item[]{Item.MODEL_SHIFT_MENU, Item.MODEL_ROTATE_MENU, Item.SEPARATOR, Item.MODEL_INSERT_LEVEL_MENU, Item.MODEL_SHIFT_LEVEL_MENU, Item.MODEL_DELETE_LEVEL},
    new Item[]{Item.VIEW_HIDE_CAPS, Item.SEPARATOR, Item.VIEW_GRID_LAYER_MENU, Item.VIEW_HOVER_PIECE_MENU, Item.VIEW_HOVER_LAYER_MENU, Item.SEPARATOR, Item.VIEW_SHOW_PIECE_ORDER}
  };
  
  private static final Map<JMenuItem, Item> mapping = new HashMap<JMenuItem, Item>();
  private static final Map<Item, JMenuItem> rmapping = new HashMap<Item, JMenuItem>();

  
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
          rmapping.put(items[i], item);
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
        {
          Settings.values.toggle(item.setting);
          AbstractButton mi = Toolbar.findItem(item.setting);
          if (mi != null) mi.setSelected(src.isSelected());
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
        
        case MODEL_SHIFT_NORTH:
        case MODEL_SHIFT_SOUTH:
        case MODEL_SHIFT_EAST:
        case MODEL_SHIFT_WEST:
        case MODEL_ROTATE_LEFT:
        case MODEL_ROTATE_RIGHT:
          
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
