package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.data.ModelState;

public abstract class UndoableTask implements Task
{  
  protected final ModelState state;
  protected final Model model;
  
  public UndoableTask(Model model)
  {
    this.model = model;
    this.state = model.dumpState();
  }
  
  public final void execute() { execute(model); }
  protected abstract void execute(Model model); 
}
