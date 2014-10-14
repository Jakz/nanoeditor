package pixbits.nanoblock.gui.frames;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import pixbits.nanoblock.files.Log;

public enum Icon
{
  ROTATE_LEFT("rotate_left"),
  ROTATE_RIGHT("rotate_right")
  
  
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

}
