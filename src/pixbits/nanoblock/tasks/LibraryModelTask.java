package pixbits.nanoblock.tasks;

import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.LibraryModel;

public abstract class LibraryModelTask implements Task
{  
  public abstract boolean execute(LibraryModel lmodel);
  
  @Override
  public boolean execute()
  {
    return execute(Library.i().getLibraryModel());
  }
}
