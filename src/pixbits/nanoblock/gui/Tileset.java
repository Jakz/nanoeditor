package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.Main;
import processing.core.PImage;
import java.util.*;
import java.awt.Rectangle;
import java.awt.Color;

public class Tileset
{
  final private PImage image;
  final public int hOffset;
  final public int xOffset;
  final public int yOffset;
  final public int[] baseColors;
  final private Map<PieceType, PieceSpec> specs;
  final private Map<PieceColor, PImage> textures;
  
  public Tileset(PImage image, int hOffset,int xOffset, int yOffset, ArrayList<Integer[]> baseCols)
  {
    specs = new HashMap<PieceType, PieceSpec>();
    textures = new HashMap<PieceColor, PImage>();
    this.image = image;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.hOffset = hOffset;
    
    baseColors = new int[baseCols.size()];
    
    for (int i = 0; i < baseCols.size(); ++i)
    {
      Integer[] rc = baseCols.get(i);
      int c = (rc[0] << 16) | (rc[1] << 8) | (rc[2] << 0) | ((rc.length > 3 ? rc[3] : 255) << 24);
      baseColors[i] = c;
    }
  }
  
  public void addSpec(PieceType piece, int x, int y, int w, int h, int ox, int oy)
  {
    specs.put(piece, new PieceSpec(x,y,w,h,ox,oy));
  }
  
  public void addColor(PieceColor color, ArrayList<Integer[]> cols)
  {
    int[] newCols = new int[baseColors.length];
    for (int i = 0; i < cols.size(); ++i)
    {
      Integer[] rc = cols.get(i);
      int c = (rc[0] << 16) | (rc[1] << 8) | (rc[2] << 0) | ((rc.length > 3 ? rc[3] : 255) << 24);
      newCols[i] = c;
    }
    
    color.setColors(new Color(cols.get(3)[0], cols.get(3)[1], cols.get(3)[2]), new Color(cols.get(0)[0], cols.get(0)[1], cols.get(0)[2]));
    
    PImage tex = Main.sketch.createImage(image.width, image.height, Sketch.ARGB);

    tex.loadPixels();
    image.loadPixels();

    for (int i = 0; i < tex.width*tex.height; ++i)
    {
      boolean found = false;
      
      for (int j = 0; j < baseColors.length; ++j)
      {
        if (image.pixels[i] == baseColors[j])
        {
          tex.pixels[i] = newCols[j];
          found = true;
        }
      }
      
      if (!found)
        tex.pixels[i] = image.pixels[i];
    }
    
    tex.updatePixels();
    image.updatePixels();
    
    textures.put(color, tex);
  }
  
  public PImage imageForColor(PieceColor color)
  {
    //return image;
    return textures.get(color);
  }

  public PieceSpec spec(PieceType type) { return specs.get(type); }
  
  public static class PieceSpec
  {
    public final int x, y;
    public final int w, h;
    public final int ox, oy;
    
    PieceSpec(int x, int y, int w, int h, int ox, int oy)
    {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.ox = ox;
      this.oy = oy;
    }
  }
  
  public Rectangle rectFor(PieceType type, PieceColor color)
  {
    PieceSpec spec = specs.get(type);  
    
    if (spec == null)
      throw new IllegalArgumentException("Missing piece spec for "+type);
    
    return new Rectangle(spec.x, spec.y, spec.w, spec.h);
  }
  
  // WHITE 231 208 172 163
  // BLACK 
}
