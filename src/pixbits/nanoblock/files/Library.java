package pixbits.nanoblock.files;

import java.util.*;
import java.io.*;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;

public class Library
{
  public static Model model;
  
  private final List<LibraryModel> models;
  
  private LibraryModel libraryModel;
  
  Library()
  {
    models = new ArrayList<LibraryModel>();
    libraryModel = null;
  }
  
  public List<LibraryModel> getModels() { return Collections.unmodifiableList(models); }
  
  public void setLibraryModel(LibraryModel model) { this.libraryModel = model; }
  public LibraryModel getLibraryModel() { return libraryModel; }
  
  public void scan() throws IOException, FileNotFoundException
  {
    File file = new File(Settings.values.getPath(Setting.Path.LIBRARY));
    file.mkdirs();
    
    File[] fmodels = file.listFiles(new ModelFileFilter());
    
    Log.i("Found "+fmodels.length+" models inside library.");
    
    for (File model : fmodels)
    {
      Model rmodel = ModelLoader.loadModel(model);
      
      Log.i("Loaded "+rmodel.getInfo().name+" from "+rmodel.getInfo().author);
      
      LibraryModel lmodel = new LibraryModel(rmodel.getInfo(), model);
      
      lmodel.pieceCount = countPieces(rmodel);
      lmodel.colorCount = countColors(rmodel);
      
      models.add(lmodel);      
    }
  }
  
  public void insertModel(LibraryModel model)
  {
    // TODO: manage specific ordering of the list, maybe with apposite method to keep it always sorted
    models.add(model);
  }
  
  public void deleteModel(LibraryModel model)
  {
    models.remove(model);
  }
  
  public static int countPieces(Model model)
  {
    int total = 0;
    
    for (int i = 0; i < model.levelCount(); ++i)
    {
      Level l = model.levelAt(i); 
      for (Piece p : l)
        if (p.type != PieceType.CAP)
          ++total;
    }
    
    return total;
  }
  
  public static int countColors(Model model)
  {
    Set<PieceColor> colors = new HashSet<PieceColor>();
    
    for (int i = 0; i < model.levelCount(); ++i)
    {
      Level l = model.levelAt(i); 
      for (Piece p : l)
        colors.add(p.color);
    }
    
    return colors.size();
  }
  
  public void fixModelFileNames()
  {
    for (LibraryModel model : models)
    {
      if (!model.file.getName().equals(model.info.hashCode+".block"))
      {
        File newFile = new File(Settings.values.getPath(Setting.Path.LIBRARY)+File.separator+model.info.hashCode+".nblock");
        model.file.renameTo(newFile);
        model.file = newFile;
      }
      
    }
  }
  
  public void computeMissingHashes()
  {
    for (LibraryModel model : models)
      if (model.info.hashCode == null)
      {
        model.info.generateRandomHash();
        model.writeBack();
      }
  }
  
  public void cacheThumbnails()
  {
    for (LibraryModel model : models)
    {
      model.loadThumbnail();
    }
  }

  private final static class ModelFileFilter implements FileFilter
  {
    public boolean accept(File pathname) { return !pathname.isDirectory() && pathname.getName().endsWith(".nblock"); }
  }
  
  public static Library i() {
    if (instance == null)
      instance = new Library();
    
    return instance;
  }
  
  private static Library instance;
}
