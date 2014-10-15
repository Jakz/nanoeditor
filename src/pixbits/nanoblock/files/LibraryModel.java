package pixbits.nanoblock.files;

import java.io.File;

import javax.swing.ImageIcon;

import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.data.ModelInfo;
import pixbits.nanoblock.gui.frames.Icon;
import pixbits.nanoblock.misc.*;

public class LibraryModel
{
  public final ModelInfo info;
  public File file;
  public ImageIcon thumbnail;
  
  public int pieceCount;
  public int colorCount;
  
  LibraryModel(ModelInfo info, File file) { this.info = info; this.file = file; thumbnail = null; }
  
  public String thumbnailName() { return info.hashCode + ".png"; }
  
  public void writeBack()
  {
    Model model = ModelLoader.loadModel(file);
    model.setInfo(this.info);
    //TODO: if model is the current edited one update its info too
    ModelLoader.saveModel(model, file);
  }
  
  public void loadThumbnail() 
  {
    try
    {
      ImageIcon icon = Icon.loadIcon(new File(Settings.values.getPath(Setting.Path.CACHE)+File.separator+thumbnailName()));
      
      if (icon == null)
        icon = Thumbnails.generateThumbnail(this);
      
      thumbnail = icon;
    }
    catch (Exception e)
    {
      Log.e(e);
    }
  }
}