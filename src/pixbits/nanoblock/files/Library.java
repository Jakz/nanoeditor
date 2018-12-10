package pixbits.nanoblock.files;

import java.util.*;

import com.pixbits.lib.functional.StreamException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;

public class Library implements Iterable<LibraryModel>
{
  private final List<LibraryModel> models;

  private LibraryModel libraryModel;
  
  public Library()
  {
    models = new ArrayList<LibraryModel>();
    libraryModel = null;
  }
  
  public Iterator<LibraryModel> iterator() { return models.iterator(); }
  
  public List<LibraryModel> getModels() { return Collections.unmodifiableList(models); }
  
  public void setLibraryModel(LibraryModel model) { this.libraryModel = model; }
  public LibraryModel getLibraryModel() { return libraryModel; }
  
  public void scan() throws IOException, FileNotFoundException
  {
    File file = new File(Settings.values.getPath(Setting.Path.LIBRARY));
    file.mkdirs();
    
    File[] fmodels = file.listFiles(new ModelFileFilter());
    
    Log.i("Found "+fmodels.length+" models inside library.");
    
    for (File fmodel : fmodels)
    {
      ModelInfo info = ModelLoader.loadInfo(fmodel);
      LibraryModel lmodel = new LibraryModel(info, fmodel);
      
      Log.i("Loaded "+lmodel.info.name+" from "+lmodel.info.author);

      lmodel.load();
      lmodel.pieceCount = countPieces(lmodel.model);
      lmodel.colorCount = countColors(lmodel.model);
      lmodel.unload();
      
      models.add(lmodel);      
    }
    
    sort();
  }
  
  public void sort()
  {
    Collections.sort(models);
  }
  
  public boolean isHashUnique(String hash)
  {
    return true;
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
        model.lazySave();
      }
  }
  
  public void cacheThumbnails()
  {
    for (LibraryModel model : models)
    {
      model.loadThumbnail();
    }
  }
  
  public void deleteUselessThumbnails() throws IOException
  {
    Path cachePath = Paths.get(Settings.values.getPath(Setting.Path.CACHE));
    
    if (!Files.exists(cachePath))
      return;
    
    Files.list(cachePath).forEach(StreamException.rethrowConsumer(file -> {
      //TODO: use file filter?
      String fileName = file.getFileName().toString();
      
      if (fileName.endsWith(".png") && !Files.isDirectory(file))
      {
        String hashCode = fileName.substring(0, fileName.length()-4);
        boolean found = false;
        
        for (LibraryModel model : models)
        {
          if (hashCode.equals(model.info.hashCode))
          {
            found = true;
            break;
          }
        }
        
        if (!found)
        {
          Log.i("Deleting thumbnail "+fileName+" not paired with any model.");
          Files.delete(file);
        }
      }
    }));
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
