package pixbits.nanoblock.files;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.data.ModelInfo;
import pixbits.nanoblock.gui.frames.Icon;
import pixbits.nanoblock.misc.*;

public class LibraryModel implements Comparable<LibraryModel>
{
  public final ModelInfo info;
  public Model model;
  public File file;
  public ImageIcon thumbnail;
  
  public int pieceCount;
  public int colorCount;
  
  public LibraryModel(int w, int h, int l)
  {
    this.info = new ModelInfo();
    this.info.initialize(w, h, l);
    this.pieceCount = 0;
    this.colorCount = 0;
    this.file = new File(Settings.values.getPath(Setting.Path.LIBRARY)+File.separator+info.hashCode+".nblock");
    
    model = new Model(this);
    model.allocateLevels(l+1);
    
    ModelLoader.saveModel(this);
    
    this.loadThumbnail();
  }
  
  public LibraryModel(LibraryModel lmodel)
  {
    this.info = lmodel.info.dupe();
    this.file = null;
    this.pieceCount = lmodel.pieceCount;
    this.colorCount = lmodel.colorCount;
  }
  
  LibraryModel(ModelInfo info, File file) { this.info = info; this.file = file; thumbnail = null; }
  
  public String thumbnailName() { return Settings.values.getPath(Setting.Path.CACHE)+File.separator+info.hashCode + ".png"; }
  
  public void load()
  {
    model = ModelLoader.loadModel(this);
  }
  
  public void unload()
  {
    model = null;
  }
  
  public void save()
  {
    ModelLoader.saveModel(this);
  }
  
  public void lazySave()
  {
    load();
    ModelLoader.saveModel(this);
    unload();
  }
  
  public void refreshThumbnail() throws IOException
  {
    thumbnail = Thumbnails.generateThumbnail(this);
  }
  
  public void loadThumbnail() 
  {
    try
    {
      ImageIcon icon = Icon.loadIcon(new File(thumbnailName()));
      
      if (icon == null)
        refreshThumbnail();
      
      thumbnail = icon;
    }
    catch (Exception e)
    {
      Log.e(e);
    }
  }
  
  public int compareTo(LibraryModel o)
  {
    if (o.info.name == null && info.name == null)
      return 0;
    else if (o.info.name == null)
      return 1;
    else if (info.name == null)
      return -1;
    else
      return info.name.compareTo(o.info.name);
  }
}