package pixbits.nanoblock.tasks;

import java.io.*;

import pixbits.nanoblock.data.Direction;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.Main;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.files.ModelLoader;
import pixbits.nanoblock.misc.Settings;

public class Tasks
{
  public static ModelTask MODEL_SHIFT_NORTH = new ModelTask() {
    public void execute(Model model) {
      if (model.canShift(Direction.NORTH)) { model.shift(Direction.NORTH); Main.sketch.redraw(); }
    }
  };
  
  public static ModelTask MODEL_SHIFT_SOUTH = new ModelTask() {
    public void execute(Model model) {
      if (model.canShift(Direction.SOUTH)) { model.shift(Direction.SOUTH); Main.sketch.redraw(); }
    }
  };
  
  public static ModelTask MODEL_SHIFT_EAST = new ModelTask() {
    public void execute(Model model) {
      if (model.canShift(Direction.EAST)) { model.shift(Direction.EAST); Main.sketch.redraw(); }
    }
  };
  
  public static ModelTask MODEL_SHIFT_WEST = new ModelTask() {
    public void execute(Model model) {
      if (model.canShift(Direction.WEST)) { model.shift(Direction.WEST); Main.sketch.redraw(); }
    }
  };
  
  public static ModelTask MODEL_ROTATE_WEST = new ModelTask() {
    public void execute(Model model) {
      model.rotate(Direction.WEST); Main.sketch.redraw();
    }
  };
  
  public static ModelTask MODEL_ROTATE_EAST = new ModelTask() {
    public void execute(Model model) {
      model.rotate(Direction.EAST); Main.sketch.redraw();
    }
  };
  
  public static ModelTask MODEL_RESET = new ModelTask() {
    public void execute(Model model) {
      Library.model.clear();
    }
  };
  
  
  
  
  public static void saveModel()
  {
    Log.i("Saving model.");
    ModelLoader.saveModel(Library.model, new File("model.nblock"));
  }
  
  public static void saveSettings()
  {
    try {
      Log.i("Saving settings.");
      Settings.saveSettings();
    } catch (Exception e) {
      Log.e(e);
    }
  }
  
  
  public static void loadSettings()
  {
    try {
      Log.i("Loading settings.");
      Settings.loadSettings();
    } catch (Exception e) {
      Log.e(e);
    }
  }
}
