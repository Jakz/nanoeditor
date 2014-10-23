package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.*;

public abstract class UndoableHeavyTask extends UndoableTask
{
  ModelState state;
  
  UndoableHeavyTask(Model model)
  {
    super(model);
    this.state = model.dumpState();
  }

  @Override
  public final void undo()
  {
    model.restoreState(state);
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
    
    if (executed)
      UndoManager.actionDone(this, false);
    
    return executed;
  }
  
  protected abstract boolean execute(Model model);

}
