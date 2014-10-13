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
  
  Library()
  {
    models = new ArrayList<LibraryModel>();
  }
  
  public List<LibraryModel> getModels() { return Collections.unmodifiableList(models); }
  
  public void scan() throws IOException, FileNotFoundException
  {
    File file = new File(Settings.values.getPath(Setting.Path.LIBRARY));
    file.mkdirs();
    
    File[] fmodels = file.listFiles(new ModelFileFilter());
    
    Log.i("Found "+fmodels.length+" models inside library.");
    
    for (File model : fmodels)
    {
      ModelInfo info = ModelLoader.loadInfo(model);
      
      Log.i("Loaded "+info.name+" from "+info.author);
      
      models.add(new LibraryModel(info, model));      
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
