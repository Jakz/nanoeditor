package pixbits.nanoblock.files;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.gui.PieceDrawer;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.imgscalr.Scalr;

public class Thumbnails
{
  public static void refreshAllThumbnails()
  {
    try
    {
      for (LibraryModel lm : Library.i())
        lm.refreshThumbnail();
    }
    catch (Exception e)
    {
      Log.e(e);
    }
  }

  public static ImageIcon generateThumbnail(LibraryModel lmodel) throws IOException
  {
    Log.i("Generating thumbnail for "+lmodel.info.name+" ("+lmodel.info.author+")");
    
    Model model = lmodel.model;
    
    if (model == null)
    {
      lmodel.load();
      model = lmodel.model;
      lmodel.unload();
    }
    
    Dimension size = Settings.values.getThumbnailSize();
    int padding = Settings.values.getThumbnailPadding();
    float ratio = ((float)(size.width)) / (size.height);
    
    Rectangle bound = PieceDrawer.computeRealBounds(model, false);
    
    if (bound == null)
      return new ImageIcon(new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB));
    
    float mratio = ((float)bound.width)/bound.height;
    
    Dimension mbounds = new Dimension(bound.width,bound.height);

    // adjusting width and height to fulfill the specified thumbnail ratio
    if (mratio > ratio)
    {
      float newHeight = bound.width * ratio;
      mbounds.height = (int) (newHeight);
    }
    else if (mratio < ratio)
    {
      float newWidth = bound.height / ratio;
      mbounds.width = (int) (newWidth);
    }
    
    System.out.println(mbounds.height+" - "+bound.height);
    // calculating new padding according to adjusted dimension
    Dimension mpadding = new Dimension((mbounds.width - bound.width)/2,(mbounds.height - bound.height)/2);
    
    PImage image = Main.sketch.createImage(mbounds.width, mbounds.height, PApplet.ARGB);
    
    PieceDrawer.drawModelOnImage(image, mpadding.width-bound.x, mpadding.height-bound.y, model, false);
    BufferedImage output = scaleImage((BufferedImage)image.getImage(), size.width/(float)image.width);

    // add padding if necessary
    if (padding > 0)
    {
      BufferedImage output2 = new BufferedImage(output.getWidth()+padding*2, output.getHeight()+padding*2, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D)output2.getGraphics();
      g.drawImage(output, padding, padding, null);
      output = output2;
    }
        
    ImageIO.write(output, "PNG", new File(lmodel.thumbnailName()));

    return new ImageIcon(output);
  }

  public static BufferedImage scaleImage(BufferedImage src, float factor)
  {
    return Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, (int)(src.getWidth(null)*factor), (int)(src.getHeight(null)*factor));
    
    /*BufferedImage dst = new BufferedImage((int)(src.getWidth(null)*factor), (int)(src.getHeight(null)*factor), BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();

    at.scale(factor, factor);
    
    AffineTransformOp scaleOp = null;
    //if (factor < 1)
      scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    //else
    //  scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

    dst = scaleOp.filter(src, dst);
    return dst;*/
  }
  
  public static BufferedImage gaussianBlur(BufferedImage image,double sigma) {

    int height = image.getHeight(null);
    int width = image.getWidth(null);

    BufferedImage tempImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
    BufferedImage filteredImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);

    //--->>
    int n = (int) (6 * sigma + 1);

    double[] window = new double[n];
    double s2 = 2 * sigma * sigma;

    window[(n - 1) / 2] = 1;
    for (int i = 0; i < (n - 1) / 2; i++) {
        window[i] = Math.exp((double) (-i * i) / (double) s2);
        window[n - i - 1] = window[i];
    }

    //--->>
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            double sum = 0;
            double[] colorRgbArray = new double[]{0, 0, 0};
            for (int k = 0; k < window.length; k++) {
                int l = i + k - (n - 1) / 2;
                if (l >= 0 && l < width) {
                    Color imageColor = new Color(image.getRGB(l, j));
                    colorRgbArray[0] = colorRgbArray[0] + imageColor.getRed() * window[k];
                    colorRgbArray[1] = colorRgbArray[1] + imageColor.getGreen() * window[k];
                    colorRgbArray[2] = colorRgbArray[2] + imageColor.getBlue() * window[k];
                    sum += window[k];
                }
            }
            for (int t = 0; t < 3; t++) {
                colorRgbArray[t] = colorRgbArray[t] / sum;
            }
            Color tmpColor = new Color((int) colorRgbArray[0], (int) colorRgbArray[1], (int) colorRgbArray[2]);
            tempImage.setRGB(i, j, tmpColor.getRGB());
        }
    }

    //--->>
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            double sum = 0;
            double[] colorRgbArray = new double[]{0, 0, 0};
            for (int k = 0; k < window.length; k++) {
                int l = j + k - (n - 1) / 2;
                if (l >= 0 && l < height) {
                    Color imageColor = new Color(tempImage.getRGB(i, l));
                    colorRgbArray[0] = colorRgbArray[0] + imageColor.getRed() * window[k];
                    colorRgbArray[1] = colorRgbArray[1] + imageColor.getGreen() * window[k];
                    colorRgbArray[2] = colorRgbArray[2] + imageColor.getBlue() * window[k];
                    sum += window[k];
                }
            }
            for (int t = 0; t < 3; t++) {
                colorRgbArray[t] = colorRgbArray[t] / sum;
            }
            Color tmpColor = new Color((int) colorRgbArray[0], (int) colorRgbArray[1], (int) colorRgbArray[2]);
            filteredImage.setRGB(i, j, tmpColor.getRGB());
        }
    }

    return filteredImage;
}
}
