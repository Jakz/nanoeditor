package pixbits.nanoblock.tasks;

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
import pixbits.nanoblock.gui.menus.Item;
import pixbits.nanoblock.misc.*;

public class Tasks
{
  
  public static ModelTask MODEL_INSERT_LEVEL_ABOVE = new ModelTask() {
    public boolean execute(Model model) {
      //TODO: update level inside LevelView
      model.insertAbove(Main.sketch.levelStackView.getLocked());
      Main.sketch.redraw();
      return true;
    }
  };
  
  public static ModelTask MODEL_INSERT_LEVEL_BELOW = new ModelTask() {
    public boolean execute(Model model) {
      model.insertBelow(Main.sketch.levelStackView.getLocked());
      //TODO: update level inside LevelView
      Main.sketch.redraw();
      return true;
    }
  };
  
  public static ModelTask MODEL_SHIFT_LEVEL_UP = new ModelTask() {
    public boolean execute(Model model) {
      return true;
    }
  };
  
  public static ModelTask MODEL_SHIFT_LEVEL_DOWN = new ModelTask() {
    public boolean execute(Model model) {
      return true;
    }
  };
  
  public static ModelTask MODEL_DELETE_LEVEL = new ModelTask() {
    public boolean execute(Model model) {
      Level level = Main.sketch.levelStackView.getLocked();
      model.deleteLevel(level);
      //TODO: update level inside LevelView
      Main.sketch.levelStackView.setLocked(level.previous());
      Main.sketch.levelStackView.clearToBeDeleted();
      Main.sketch.redraw();
      return true;
    }
  };
  
  
  
  public static ModelTask MODEL_SHOW_RESIZE = new ModelTask() { public boolean execute(Model model) { Main.resizeModelFrame.show(model); return true; } };
  public static ModelTask MODEL_SHOW_REPLACE_COLOR = new ModelTask() { public boolean execute(Model model) { Main.replaceColorFrame.show(model); return true; } };
  
  
  public static Task LIBRARY_NEW_MODEL = new Task() {
    public boolean execute() {
      Log.i("Creating new empty model");
      LibraryModel nmodel = new LibraryModel(20,20,15);
      
      Library.i().insertModel(nmodel);
      Library.i().sort();
      Main.libraryFrame.refreshLibrary();
      return true;
    }
  };
  
  public static LibraryModelTask LIBRARY_CLONE_MODEL = new LibraryModelTask() {
    public boolean execute(LibraryModel lmodel) {
      if (lmodel == null)
      {
        Dialogs.showErrorDialog(Main.libraryFrame, "Error", "You must select a model first");
        return false;
      }
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
          return true;
        }
        catch (Exception e)
        {
          Log.e(e);
        }
        
        return false;
      }
    }
  };
  
  public static LibraryModelTask LIBRARY_DELETE_MODEL = new LibraryModelTask() {
    public boolean execute(LibraryModel model) {
      if (model == null)
      {
        Dialogs.showErrorDialog(Main.libraryFrame, "Error", "You must select a model first");
        return false;
      } 
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
        
        return true;
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
    Item.setLevelOperationsEnabled(false);
    Item.setUndoRedoEnabled(false, false);
    
    Main.libraryFrame.setVisible(false);
    Library.i().setLibraryModel(lmodel);
    lmodel.load();
    Library.model = lmodel.model;
    Main.sketch.initForModel(lmodel.model);
    Main.mainFrame.setVisible(true);
    Main.sketch.redraw();
  }
  
  public static void closeEditor()
  {
    //TODO: warn to save
    saveModel();
    saveSettings();
    UndoManager.clear();
    Main.sketch.hideMe();
    Main.mainFrame.setVisible(false);
    Main.libraryFrame.setLocationRelativeTo(Main.mainFrame);
    Main.libraryFrame.showMe();
  }
}
