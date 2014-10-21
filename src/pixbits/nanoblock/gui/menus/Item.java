package pixbits.nanoblock.gui.menus;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import pixbits.nanoblock.data.Direction;
import pixbits.nanoblock.gui.frames.*;
import pixbits.nanoblock.misc.*;
import pixbits.nanoblock.tasks.*;

public enum Item
{  
  SEPARATOR(),
  
  FILE_SAVE("Save"),
  FILE_CLOSE("Close"),
  FILE_EXIT("Exit"),
  FILE_EXPORT("Export.."),
  FILE_EXPORT_INSTRUCTIONS("Export instructions.."),
      
  EDIT_HALF_STEPS("Use half steps", "Enable half steps", Icon.ENABLE_HALF_STEPS, Setting.HALF_STEPS_ENABLED),
  EDIT_USE_TAB_TO_ROTATE("Use TAB to rotate", "Use TAB to rotate", Icon.USE_TAB_ROTATION, Setting.USE_TAB_TO_ROTATE),
  EDIT_RESET("Reset"),
  EDIT_RESIZE("Resize", "Resize", Icon.MODEL_RESIZE, Tasks.MODEL_SHOW_RESIZE),
  EDIT_REPLACE_COLOR("Replace Color", "Replace Colore", Icon.MODEL_REPLACE_COLOR, Tasks.MODEL_SHOW_REPLACE_COLOR),

  
  VIEW_HIDE_CAPS("Draw caps", Setting.DRAW_CAPS),
  VIEW_SHOW_PIECE_ORDER("Show piece order",  Setting.SHOW_PIECE_ORDER),
  
  VIEW_SHOW_LAYER_GRID("Enable", Setting.VIEW_SHOW_LAYER_GRID),
  VIEW_LAYER_GRID_ALPHA("Use opacity", Setting.VIEW_LAYER_GRID_ALPHA),
  VIEW_HOVER_LAYER_MENU("Isometric Layer", new Item[]{ VIEW_SHOW_LAYER_GRID, VIEW_LAYER_GRID_ALPHA} ),
  VIEW_SHOW_GRID_POINTS("Enable half grid points", Setting.VIEW_SHOW_HALF_GRID_POINTS),
  VIEW_SHOW_GRID("Enable grid", Setting.VIEW_SHOW_GRID_LINES),
  VIEW_MARK_DELETED_PIECE_ON_LAYER("Mark deletable piece", Setting.VIEW_MARK_DELETED_PIECE_ON_LAYER),
  VIEW_GRID_LAYER_MENU("Layer Grid", new Item[] {VIEW_SHOW_GRID, VIEW_SHOW_GRID_POINTS, SEPARATOR, VIEW_MARK_DELETED_PIECE_ON_LAYER} ),
 
  VIEW_HOVER_PIECE_DISABLE("Disable", Setting.HoverSetter.INSTANCE, Setting.HoverPiece.GROUP, Setting.HoverPiece.DISABLE),
  VIEW_HOVER_PIECE_STROKE_BACK("Behind stroke", Setting.HoverSetter.INSTANCE, Setting.HoverPiece.GROUP, Setting.HoverPiece.BACK_STROKE),
  VIEW_HOVER_PIECE_STROKE_FRONT("Front stroke", Setting.HoverSetter.INSTANCE, Setting.HoverPiece.GROUP, Setting.HoverPiece.FRONT_STROKE),
  VIEW_HOVER_PIECE_FILL("Front stroke with back fill", Setting.HoverSetter.INSTANCE, Setting.HoverPiece.GROUP, Setting.HoverPiece.FRONT_STROKE_WITH_BACK_FILL),
  VIEW_HOVER_PIECE_MENU("Isometric Piece", new Item[] {VIEW_HOVER_PIECE_DISABLE, VIEW_HOVER_PIECE_STROKE_BACK, VIEW_HOVER_PIECE_STROKE_FRONT, VIEW_HOVER_PIECE_FILL}),
  
  MODEL_SHIFT_NORTH("North ^", "Shift North", Icon.SHIFT_NORTH, ModelOperations.buildShift(Direction.NORTH)),
  MODEL_SHIFT_SOUTH("South v", "Shift South", Icon.SHIFT_SOUTH, ModelOperations.buildShift(Direction.SOUTH)),
  MODEL_SHIFT_WEST("West <-", "Shift West", Icon.SHIFT_WEST, ModelOperations.buildShift(Direction.WEST)),
  MODEL_SHIFT_EAST("East ->", "Shift East", Icon.SHIFT_EAST, ModelOperations.buildShift(Direction.EAST)),
  MODEL_SHIFT_MENU("Shift", new Item[] {MODEL_SHIFT_NORTH, MODEL_SHIFT_SOUTH, MODEL_SHIFT_WEST, MODEL_SHIFT_EAST} ),
  
  MODEL_ROTATE_LEFT("Left", "Rotate Left", Icon.ROTATE_LEFT, ModelOperations.buildRotate(Direction.WEST)),
  MODEL_ROTATE_RIGHT("Right", "Rotate Right", Icon.ROTATE_RIGHT, ModelOperations.buildRotate(Direction.EAST)),
  MODEL_ROTATE_MENU("Rotate", new Item[] { MODEL_ROTATE_LEFT, MODEL_ROTATE_RIGHT }),
  
  MODEL_INSERT_LEVEL_ABOVE("Insert above", "Insert Level Above", Icon.INSERT_LEVEL_ABOVE, Tasks.MODEL_INSERT_LEVEL_ABOVE),
  MODEL_INSERT_LEVEL_BELOW("Insert below", "Insert Level Below", Icon.INSERT_LEVEL_BELOW, Tasks.MODEL_INSERT_LEVEL_BELOW),
  MODEL_INSERT_LEVEL_MENU("Insert Level", new Item[] {MODEL_INSERT_LEVEL_ABOVE, MODEL_INSERT_LEVEL_BELOW} ),
  
  MODEL_SHIFT_LEVEL_UP("Shift up", "Shift Level Up", Icon.SHIFT_LEVEL_UP, Tasks.MODEL_SHIFT_LEVEL_UP),
  MODEL_SHIFT_LEVEL_DOWN("Shift down", "Shift Level Down", Icon.SHIFT_LEVEL_DOWN, Tasks.MODEL_SHIFT_LEVEL_DOWN),
  MODEL_SHIFT_LEVEL_MENU("Shift Level", new Item[] {MODEL_SHIFT_LEVEL_UP, MODEL_SHIFT_LEVEL_DOWN} ),
  
  MODEL_DELETE_LEVEL("Delete Level", "Delete Level", Icon.DELETE_LEVEL, Tasks.MODEL_DELETE_LEVEL),
  
  
  
  // TOOLBAR ONLY
  
  LIBRARY_MODEL_NEW("New Model", Icon.MODEL_NEW, Tasks.LIBRARY_NEW_MODEL),
  LIBRARY_MODEL_DUPLICATE("Duplicate Model", Icon.MODEL_DUPLICATE, Tasks.LIBRARY_CLONE_MODEL),
  LIBRARY_MODEL_DELETE("Delete Model", Icon.MODEL_DELETE, Tasks.LIBRARY_DELETE_MODEL),
  ;
  
  Item(String caption, String tooltip, Icon icon, ItemType type, Setting setting, Task task, OperationBuilder builder)
  {
    this.caption = caption;
    this.tooltip = tooltip;
    this.icon = icon != null ? icon.icon() : null;
    this.type = type;
    this.setting = setting;
    this.task = task;
    this.builder = builder;
    
    this.radioValue = null;
    this.radioSetter = null;
    this.radioGroup = null;
    
    this.items = null;
  }
  
  private Item(String caption) { this(caption, null, null, ItemType.BUTTON, null, null, null); }

  
  Item()
  {
    this(null, null, null, null, null, null, null);
  }
    
  private Item(String caption, Setting setting) { this(caption, (String)null, null, setting); }
  private Item(String caption, String tooltip, Icon icon, Setting setting) { this(caption, tooltip, icon, ItemType.CHECKBOX, setting, null, null); }
  
  private Item(String caption, Task task) { this(caption, null, null, task); }
  private Item(String tooltip, Icon icon, Task task) { this(null, tooltip, icon, task); }
  private Item(String caption, String tooltip, Icon icon, Task task) { this(caption, tooltip, icon, ItemType.BUTTON, null, task, null); }
  
  
  private Item(String caption, String tooltip, Icon icon, OperationBuilder builder) { this(caption, tooltip, icon, ItemType.BUTTON, null, null, builder); }
  
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
    this.builder = null;
    this.tooltip = null;
    this.icon = null;
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
    this.builder = null;
    this.tooltip = null;
    this.icon = null;
  }
  
  public final String caption;
  public final String tooltip;
  public final ImageIcon icon;
  public final ItemType type;
  public final Setting setting;
  public final Item[] items;
  public final Enum<?> radioValue;
  public final Setting.EnumSetter radioSetter;
  public final ButtonGroup radioGroup;
  
  public final Task task;
  public final OperationBuilder builder;
  
  public AbstractButton buttonToolbar;
  public JMenuItem buttonMenu;
  
  
  public void setEnabled(boolean enabled)
  {
    if (buttonMenu != null) buttonMenu.setEnabled(enabled);
    if (buttonToolbar != null) buttonToolbar.setEnabled(enabled);
  }
  
  public static void setLevelOperationsEnabled(boolean enabled)
  {
    Item[] items = { Item.MODEL_INSERT_LEVEL_ABOVE, Item.MODEL_INSERT_LEVEL_BELOW, Item.MODEL_SHIFT_LEVEL_UP, Item.MODEL_SHIFT_LEVEL_DOWN, Item.MODEL_DELETE_LEVEL };
    for (Item i : items)
      i.setEnabled(enabled);
  }
}