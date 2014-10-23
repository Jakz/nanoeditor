package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.Model;

public abstract class UndoableLightTask extends UndoableTask
{  
  boolean isRealAction;
  
  protected UndoableLightTask(Model model, boolean isRealAction)
  {
    super(model);
    this.isRealAction = isRealAction;
  }
  
  UndoableLightTask(Model model)
  {
    this(model, true);
  }

  @Override
  public final void undo()
  {
    UndoableLightTask reverse = reverseAction();
    reverse.execute();
    super.undo();
  }
  
  @Override
  public final void redo()
  {
    super.execute();
    super.redo();
  }
  
  @Override
  public final boolean execute()
  {
    boolean executed = super.execute();
    
    if (isRealAction && executed)
      UndoManager.actionDone(this, false);
    
    return executed;
  }
  
  protected abstract boolean execute(Model model);
  protected abstract UndoableLightTask reverseAction();
}
