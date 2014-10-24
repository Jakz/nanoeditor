package pixbits.nanoblock.tasks;

import java.util.*;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.gui.menus.Item;

public class UndoManager
{
  private static LinkedList<UndoableTask> undos = new LinkedList<UndoableTask>();
  private static LinkedList<UndoableTask> redos = new LinkedList<UndoableTask>();
  
  public static void actionUndone(UndoableTask action)
  {
    redos.add(action);
    updateMenuEntries();
  }
  
  public static void actionDone(UndoableTask action, boolean isRedo)
  {
    undos.add(action);
    
    if (!isRedo)
      redos.clear();

    updateMenuEntries();
  }
  
  public static void undoAction()
  {
    UndoableTask action = undos.pollLast();
    action.undo();
  }
  
  public static void redoAction()
  {
    UndoableTask action = redos.pollLast();
    action.redo();
  }
  
  public static void clear()
  {
    undos.clear();
    redos.clear();
    updateMenuEntries();
  }
  
  
  private static void updateMenuEntries()
  {
    Item.setUndoRedoEnabled(!undos.isEmpty(), !redos.isEmpty());
  }
  
  public static final Task UNDO_TASK = new Task() { public boolean execute() { UndoManager.undoAction(); return true; } };
  public static final Task REDO_TASK = new Task() { public boolean execute() { UndoManager.redoAction(); return true; } };

}
