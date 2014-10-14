package pixbits.nanoblock.files;

import java.io.File;

import javax.swing.ImageIcon;

import pixbits.nanoblock.data.ModelInfo;
import pixbits.nanoblock.gui.frames.Icon;
import pixbits.nanoblock.misc.*;

public class LibraryModel
{
  public final ModelInfo info;
  public final File file;
  public ImageIcon thumbnail;
  
  LibraryModel(ModelInfo info, File file) { this.info = info; this.file = file; thumbnail = null; }
  
  public String thumbnailName() { return (info.name+"-"+info.author+".png").toLowerCase(); }
  
  public void loadThumbnail() 
  {
    try
    {
      ImageIcon icon = Icon.loadIcon(new File(Settings.values.getPath(Setting.Path.CACHE)+File.separator+thumbnailName()));
      
      if (icon == null)
        icon = Thumbnails.generateThumbnail(this);
    }
    catch (Exception e)
    {
      Log.e(e);
    }
  }
}