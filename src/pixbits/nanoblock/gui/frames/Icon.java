package pixbits.nanoblock.gui.frames;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import pixbits.nanoblock.files.Log;

public enum Icon
{
  ROTATE_LEFT("rotate_left"),
  ROTATE_RIGHT("rotate_right"),
  MODEL_RESIZE("resize_model"),
  MODEL_REPLACE_COLOR("replace_color"),
  
  MODEL_DUPLICATE("duplicate_model"),
  MODEL_NEW("new_model"),
  MODEL_DELETE("delete_model"),
  
  ARROW_UP("arrow_up"),
  ARROW_DOWN("arrow_down"),
  ARROW_LEFT("arrow_left"),
  ARROW_RIGHT("arrow_right"),
  ARROW_UP_LEFT("arrow_up_left"),
  ARROW_UP_RIGHT("arrow_up_right"),
  ARROW_DOWN_LEFT("arrow_down_left"),
  ARROW_DOWN_RIGHT("arrow_down_right")
  ;
  
  private final String filename;
  private ImageIcon icon;
  
  private Icon(String filename)
  {
    this.filename = filename + ".png";
  }
  
  private void cacheIcon()
  {
    try
    {
      InputStream stream = this.getClass().getResourceAsStream(ICONS_PATH+filename);
      BufferedImage img = ImageIO.read(stream);
      icon = new ImageIcon(img);
    }
    catch (Exception e)
    {
      Log.e(e);
    }
  }
  
  public ImageIcon icon()
  {
    if (icon == null)
      cacheIcon();
    
    return icon;
  }
  
  private final static String ICONS_PATH = "/pixbits/nanoblock/icons/";

  
  public static ImageIcon loadIcon(File file) throws FileNotFoundException, IOException
  {
    if (!file.exists())
      return null;
    else
    {
      Log.i("Loading cached thumbnail from "+file);
      BufferedImage img = ImageIO.read(file);
      return new ImageIcon(img);
    }
  }
}
