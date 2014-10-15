package pixbits.nanoblock.files;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.gui.PieceDrawer;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Thumbnails
{
  public static ImageIcon generateThumbnail(LibraryModel lmodel) throws IOException
  {
    Log.i("Generating thumbnail for "+lmodel.info.name+" ("+lmodel.info.author+")");
    
    Model model = ModelLoader.loadModel(lmodel.file);
    
    Rectangle bound = PieceDrawer.computeBoundsForModel(model);
    PImage image = Main.sketch.createImage(bound.width, bound.height, PApplet.ARGB);
    PieceDrawer.drawModelOnImage(image, bound.x, bound.y, model, false);
    BufferedImage output = scaleImage((BufferedImage)image.getImage(), 0.25f);
    
    ImageIO.write(output, "PNG", new File(Settings.values.getPath(Setting.Path.CACHE)+File.separator+lmodel.thumbnailName()));

    return new ImageIcon(output);
  }

  public static BufferedImage scaleImage(BufferedImage src, float factor)
  {
    BufferedImage dst = new BufferedImage((int)(src.getWidth(null)*factor), (int)(src.getHeight(null)*factor), BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(factor, factor);
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    dst = scaleOp.filter(src, dst);
    return dst;
  }
}
