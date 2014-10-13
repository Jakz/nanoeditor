package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.files.Library;

public abstract class ModelTask implements Task {
  public abstract void execute(Model model);
  
  public void execute() { execute(Library.model); }
}
