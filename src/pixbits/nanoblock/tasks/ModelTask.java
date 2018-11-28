package pixbits.nanoblock.tasks;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.LibraryModel;

public abstract class ModelTask implements Task
{
  public abstract boolean execute(Model model);
  
  public boolean execute()
  { 
    Model model = Main.sketch.getModel();
    
    if (model == null)
    {
      LibraryModel lmodel = Library.i().getLibraryModel();
      boolean wasNull = false;

      if (lmodel.model == null)
      {
        lmodel.load();
        wasNull = true;
      }
      
      model = lmodel.model;
      
      if (wasNull)
        lmodel.unload();
    }

    return execute(model);
    
  }
}
