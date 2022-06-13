package pixbits.nanoblock.tasks;

import pixbits.nanoblock.data.Model;

public interface OperationBuilder
{
  UndoableTask build(Model model);
}
