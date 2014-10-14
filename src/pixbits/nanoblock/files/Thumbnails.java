package pixbits.nanoblock.files;

import java.io.File;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.gui.PieceDrawer;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

import javax.swing.ImageIcon;

public class Thumbnails
{
  public static ImageIcon generateThumbnail(LibraryModel lmodel)
  {
    Log.i("Generating thumbnail for "+lmodel.info.name+" ("+lmodel.info.author+")");
    
    Model model = ModelLoader.loadModel(lmodel.file);
    
    PImage image = Main.sketch.createImage(1024, 768, PApplet.ARGB);
    PieceDrawer.drawModelOnImage(image, 512, 768/2, model);
    PImage output = PieceDrawer.scaleImage(image, 0.25f);
    
    output.save(Settings.values.getPath(Setting.Path.CACHE)+File.separator+lmodel.thumbnailName());
    
    return new ImageIcon(output.getImage());
  }
}
