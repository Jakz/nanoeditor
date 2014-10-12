package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.Model;

public interface ModelTask extends Task {
  public void execute(Model model);
}
