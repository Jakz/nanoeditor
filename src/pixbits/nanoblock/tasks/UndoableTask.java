package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.Model;

public abstract class UndoableTask implements Task
{
  protected final Model model;
  
  public UndoableTask(Model model)
  {
    this.model = model;
  }
  
  public final void execute() { execute(model); }
  protected abstract void execute(Model model); 
}
