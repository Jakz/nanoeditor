package pixbits.nanoblock.tasks;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.JDialog;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.Main;
import pixbits.nanoblock.files.FileUtils;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.LibraryModel;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.files.ModelLoader;
import pixbits.nanoblock.gui.PieceDrawer;
import pixbits.nanoblock.gui.Sketch;
import pixbits.nanoblock.gui.frames.Dialogs;
import pixbits.nanoblock.gui.frames.ExportImageFrame;
import pixbits.nanoblock.gui.menus.Item;
import pixbits.nanoblock.misc.*;
import processing.core.PImage;

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
  public static ModelTask MODEL_SHOW_EXPORT_IMAGE = new ModelTask() { public boolean execute(Model model) { ExportImageFrame.showMe(model); return true; } };

  
  public static Task LIBRARY_NEW_MODEL = new Task() {
    public boolean execute() {
      Log.i("Creating new empty model");
      LibraryModel nmodel = new LibraryModel(20,20,15);
      
      Library.i().insertModel(nmodel);
      Library.i().sort();
      Main.libraryFrame.refreshLibrary(nmodel);
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
          Main.libraryFrame.refreshLibrary(nmodel);
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
          Main.libraryFrame.refreshLibrary(null);
          
          //TODO: if it can happen that current opened model is the one just deleted we must make something
        }
        
        return true;
      }
    }
  };
  
  public static LibraryModelTask LIBRARY_OPEN_IN_EDITOR = new LibraryModelTask() {  
    public boolean execute(LibraryModel lmodel) {
      Item.setLevelOperationsEnabled(false);
      Item.setUndoRedoEnabled(false, false);
      
      Main.libraryFrame.setVisible(false);
      Library.i().setLibraryModel(lmodel);
      lmodel.load();
      Main.sketch.initForModel(lmodel.model);
      Main.mainFrame.setVisible(true);
      Item.setLibraryModelOperationsEnabled(true);
      Main.sketch.redraw();
      return true;
    }
  };
  
  public static class ExportModelImageTask implements Task
  {
    private final Model model;
    private final boolean withCaps;
    private final boolean allRotations;
    private final File file;
    
    private JDialog parent;
    
    public ExportModelImageTask(JDialog parent, Model model, File file, boolean withCaps, boolean allRotations)
    {
      this.model = model;
      this.file = file;
      this.withCaps = withCaps;
      this.allRotations = allRotations;
      
      this.parent = parent;
    }
    
    @Override
    public boolean execute()
    {
      if (file.exists())
      {
        if (!Dialogs.showConfirmDialog(parent, "File exists", "File already exists, do you want to overwrite it?", null))
          return false;
      }
      
      RenderedImage image = null;
      
      if (!allRotations)
      {
        Rectangle bounds = PieceDrawer.computeRealBounds(model, withCaps);
        PImage pimage = Main.sketch.createImage(bounds.width, bounds.height, Sketch.ARGB);
        PieceDrawer.drawModelOnImage(pimage, -bounds.x, -bounds.y, model, withCaps);
        image = (RenderedImage)pimage.getImage();
      }
      else
      {
        boolean vertical = true;
        final int padding = 20;
        
        PImage images[] = new PImage[4];
        int common = Integer.MIN_VALUE;
        int[] separated = new int[4];
        
        for (int i = 0; i < 4; ++i)
        {
          Rectangle bounds = PieceDrawer.computeRealBounds(model, withCaps);
          images[i] = Main.sketch.createImage(bounds.width, bounds.height, Sketch.ARGB);
          PieceDrawer.drawModelOnImage(images[i], -bounds.x, -bounds.y, model, withCaps);
          model.rotate(Direction.EAST);
          
          if (vertical)
          {
            common = Math.max(common, bounds.width);
            separated[i] = bounds.height;
          }
          else
          {
            common = Math.max(common, bounds.height);
            separated[i] = bounds.width;
          }
        }
        
        int finalHeight = 0;
        int finalWidth = 0;
        
        if (vertical)
        {
          finalWidth = common;
          for (int i = 0; i < 4; ++i) finalHeight += separated[i] + padding;
        }
        else
        {
          // TODO
        }
        
        BufferedImage bimage = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)bimage.getGraphics();
        image = bimage;
        
        int c = 0;
        for (int i = 0; i < 4; ++i)
        {
          if (vertical)
          {
            g.drawImage(images[i].getImage(), (finalWidth - images[i].width)/2, c, null);
            c += separated[i] + padding;
          }
        }
        
      }
      
      try 
      {
        ImageIO.write(image, "PNG", file);   
      }
      catch (IOException e)
      {
        Log.e(e);
      }
      
      return true;
    }
  }
  
  public static void saveModel()
  {
    Log.i("Saving model.");
    ModelLoader.saveModel(Library.i().getLibraryModel());
    
    try
    {
      Library.i().getLibraryModel().refreshThumbnail();
    }
    catch (IOException e)
    {
      Log.e(e);
    }
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
  
  public static void closeEditor()
  {
    //TODO: warn to save
    saveModel();
    saveSettings();
    Library.i().getLibraryModel().unload();
    UndoManager.clear();
    Main.sketch.hideMe();
    Main.mainFrame.setVisible(false);
    Main.libraryFrame.setLocationRelativeTo(Main.mainFrame);
    Main.libraryFrame.showMe();
  }
}
