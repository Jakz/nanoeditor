package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.files.Library;

public abstract class ModelTask implements Task {
  public abstract boolean execute(Model model);
  
  public boolean execute() { return execute(Library.model); }
}
