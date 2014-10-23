package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.Model;

public abstract class UndoableTask implements Task
{  
  protected final Model model;
  
  public UndoableTask(Model model)
  {
    this.model = model;    
  }
  
  public boolean execute() { return execute(model); };
  protected abstract boolean execute(Model model); 
  
  public void undo()
  {
    UndoManager.actionUndone(this);
  }
  
  public void redo()
  {
    UndoManager.actionDone(this, true);
  }
}
