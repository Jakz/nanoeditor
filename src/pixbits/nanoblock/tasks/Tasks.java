package pixbits.nanoblock.tasks;

import java.io.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.files.ModelLoader;

public class Tasks
{

  
  
  public static void saveModel()
  {
    Log.i("Saving model.");
    ModelLoader.saveModel(Main.sketch.model, new File("model.nblock"));

  }
}
