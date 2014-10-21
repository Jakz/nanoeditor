package pixbits.nanoblock.tasks;

import java.util.*;
import java.awt.image.RenderedImage;
import java.io.*;

import javax.imageio.ImageIO;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.Main;
import pixbits.nanoblock.files.FileUtils;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.LibraryModel;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.files.ModelLoader;
import pixbits.nanoblock.gui.frames.Dialogs;
import pixbits.nanoblock.misc.*;

public class Tasks
{
  public static ModelTask MODEL_RESET = new ModelTask() {
    public void execute(Model model) {
      Library.model.clear();
    }
  };
  
  public static ModelTask MODEL_INSERT_LEVEL_ABOVE = new ModelTask() {
    public void execute(Model model) {
      //TODO: update level inside LevelView
      model.insertAbove(Main.sketch.levelStackView.getLocked());
      Main.sketch.redraw();
    }
  };
  
  public static ModelTask MODEL_INSERT_LEVEL_BELOW = new ModelTask() {
    public void execute(Model model) {
      model.insertBelow(Main.sketch.levelStackView.getLocked());
      //TODO: update level inside LevelView
      Main.sketch.redraw();
      
    }
  };
  
  public static ModelTask MODEL_SHIFT_LEVEL_UP = new ModelTask() {
    public void execute(Model model) {
      
    }
  };
  
  public static ModelTask MODEL_SHIFT_LEVEL_DOWN = new ModelTask() {
    public void execute(Model model) {
      
    }
  };
  
  public static ModelTask MODEL_DELETE_LEVEL = new ModelTask() {
    public void execute(Model model) {
      Level level = Main.sketch.levelStackView.getLocked();
      model.deleteLevel(level);
      //TODO: update level inside LevelView
      Main.sketch.levelStackView.setLocked(level.previous());
      Main.sketch.levelStackView.clearToBeDeleted();
      Main.sketch.redraw();
    }
  };
  
  public static ModelTask MODEL_SHOW_RESIZE = new ModelTask() { public void execute(Model model) { Main.resizeModelFrame.show(model); } };
  public static ModelTask MODEL_SHOW_REPLACE_COLOR = new ModelTask() { public void execute(Model model) { Main.replaceColorFrame.show(model); } };
  
  
  public static Task LIBRARY_NEW_MODEL = new Task() {
    public void execute() {
      Log.i("Creating new empty model");
      LibraryModel nmodel = new LibraryModel(20,20,15);
      
      Library.i().insertModel(nmodel);
      Library.i().sort();
      Main.libraryFrame.refreshLibrary();
      
    }
  };
  
  public static LibraryModelTask LIBRARY_CLONE_MODEL = new LibraryModelTask() {
    public void execute(LibraryModel lmodel) {
      if (lmodel == null)
        Dialogs.showErrorDialog(Main.libraryFrame, "Error", "You must select a model first");
      else
      {
        try
        {
          Log.i("Duplicating model "+lmodel.info.name+".");
          
          LibraryModel nmodel = new LibraryModel(lmodel);
          
          // TODO: check unicity of hash before going on
          
          if (lmodel.thumbnail != null)
            ImageIO.write((RenderedImage)lmodel.thumbnail.getImage(), "PNG", new File(nmodel.thumbnailName()));
          
          lmodel.load();
          nmodel.model = lmodel.model;
          nmodel.thumbnail = lmodel.thumbnail;
          nmodel.file = new File(Settings.values.getPath(Setting.Path.LIBRARY)+File.separator+nmodel.info.hashCode+".nblock");
          lmodel.unload();
          
          ModelLoader.saveModel(nmodel);
          
          Library.i().insertModel(nmodel);
          Library.i().sort();
          Main.libraryFrame.refreshLibrary();
        }
        catch (Exception e)
        {
          Log.e(e);
        }
      }
    }
  };
  
  public static LibraryModelTask LIBRARY_DELETE_MODEL = new LibraryModelTask() {
    public void execute(LibraryModel model) {
      if (model == null)
        Dialogs.showErrorDialog(Main.libraryFrame, "Error", "You must select a model first");
      else
      {
        if (Dialogs.showConfirmDialog(Main.libraryFrame, "Delete Model", "Are you sure you want to delete the model?", null))
        {
          Log.i("Deleting model "+model.info.name+".");
          
          if (model.file != null && model.file.exists())
            FileUtils.deleteFile(model.file);
          
          if (model.info.hashCode != null)
          {
            File file = new File(model.thumbnailName());
            if (file.exists())
              FileUtils.deleteFile(file);
          }
          
          Library.i().deleteModel(model);
          Library.i().sort();
          Main.libraryFrame.refreshLibrary();
          
          //TODO: if it can happen that current opened model is the one just deleted we must make something
        }
      }
    }
  };
  
  
  
  
  public static void saveModel()
  {
    Log.i("Saving model.");
    ModelLoader.saveModel(Library.i().getLibraryModel());
    // TODO: update thumbnail
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
  
  public static void loadModelFromLibrary(LibraryModel lmodel)
  {
    Main.libraryFrame.setVisible(false);
    Library.i().setLibraryModel(lmodel);
    lmodel.load();
    Library.i().model = lmodel.model;
    Main.sketch.initForModel(lmodel.model);
    Main.mainFrame.setVisible(true);
    Main.sketch.redraw();
  }
}
