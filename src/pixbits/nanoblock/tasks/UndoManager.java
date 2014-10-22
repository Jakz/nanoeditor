package pixbits.nanoblock.tasks;

import java.util.*;

import pixbits.nanoblock.data.*;

public class UndoManager
{
  private static LinkedList<UndoableTask> undos = new LinkedList<UndoableTask>();
  private static LinkedList<UndoableTask> redos = new LinkedList<UndoableTask>();
  
  public static void actionDone(UndoableTask action)
  {
    undos.add(action);
    redos.clear();
    updateMenuEntries();
  }
  
  public static void undoAction()
  {
    UndoableTask action = undos.pollLast();
    
    ModelState state = action.state;
    Model model = action.model;
    
    model.restoreState(state);
    
    redos.addLast(action);
    updateMenuEntries();
  }
  
  public static void redoAction()
  {
    UndoableTask action = redos.pollLast();
    action.execute();
    
    updateMenuEntries();
    
    // TODO: check behavior
  }
  
  public static void clear()
  {
    undos.clear();
    redos.clear();
    updateMenuEntries();
  }
  
  
  private static void updateMenuEntries()
  {
    
  }
}
