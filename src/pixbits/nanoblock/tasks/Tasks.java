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
    public void execute(LibraryModel model) {
      if (model == null)
        Dialogs.showErrorDialog(Main.libraryFrame, "Error", "You must select a model first");
      else
      {
        try
        {
          Log.i("Duplicating model "+model.info.name+".");
          
          LibraryModel nmodel = new LibraryModel(model);
          
          // TODO: check unicity of hash before going on
          
          if (model.thumbnail != null)
            ImageIO.write((RenderedImage)model.thumbnail.getImage(), "PNG", new File(nmodel.thumbnailName()));
          
          Model rmodel = ModelLoader.loadModel(model.file);
          
          nmodel.thumbnail = model.thumbnail;
          nmodel.file = new File(Settings.values.getPath(Setting.Path.LIBRARY)+File.separator+nmodel.info.hashCode+".nblock");
          
          rmodel.setInfo(nmodel.info);
          ModelLoader.saveModel(rmodel, nmodel.file);
          
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
  
  
  
  
  
  public static class ReplaceColorTask implements Task
  {
    private final PieceColor from;
    private final PieceColor to;
    private final Level level;
    private final Model model;
    private final Set<PieceType> types;
    
    public ReplaceColorTask(Model model, PieceColor from, PieceColor to, Set<PieceType> types)
    {
      this.model = model;
      this.from = from;
      this.to = to;
      this.types = types;
      this.level = null;
    }
    
    public ReplaceColorTask(Model model, Level level, PieceColor from, PieceColor to, Set<PieceType> types)
    {
      this.model = model;
      this.from = from;
      this.to = to;
      this.types = types;
      this.level = level;
    }
    
    private void recolorCaps(Piece piece, Level next)
    {
      if (next != null)
      {
        if (!piece.type.monocap)
        {        
          for (int i = 0; i < piece.type.width*2; i += 2)
            for (int j = 0; j < piece.type.height*2; j += 2)
            {
              Piece cap = next.pieceAt(piece.x+i, piece.y+j);
              if (cap.type == PieceType.CAP)
                cap.color = piece.color;
            }
        }
        else
        {
          int i = piece.type.width/2 + piece.x;
          int j = piece.type.height/2 + piece.y;
          Piece cap = next.pieceAt(i, j);
          if (cap.type == PieceType.CAP)
            cap.color = piece.color;
        }
      }
    }
    
    public void execute()
    {
      if (level != null)
      {
        Level next = level.next();
        for (Piece p : level)
        {
          if (types.contains(p.type) && p.color == from)
          { 
            p.color = to;
            recolorCaps(p, next);
          }
        }
      }
      else
      {
        for (Level l : model)
        {
          Level next = l.next();
          for (Piece p : l)
          {
            if (types.contains(p.type) && p.color == from)
            {
              p.color = to;
              recolorCaps(p, next);
            }
          }
        }
      }
    }
  }
}
