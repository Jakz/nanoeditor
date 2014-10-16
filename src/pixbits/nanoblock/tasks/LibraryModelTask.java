package pixbits.nanoblock.tasks;

import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.LibraryModel;

public abstract class LibraryModelTask implements Task
{
  public abstract void execute(LibraryModel lmodel);
  
  @Override
  public void execute()
  {
    execute(Library.i().getLibraryModel());
  }
}
