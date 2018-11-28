package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.Main;
import processing.core.PImage;
import java.util.*;

import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.ui.color.Color;

import java.awt.Rectangle;

public class Tileset
{
  final private PImage image;
  final public int hOffset;
  final public int xOffset;
  final public int yOffset;
  final public ColorMap baseColors;
  final private Map<PieceType, PieceSpec> specs;
  final private Map<PieceColor, ColorMap> colors;
  
  
  final private Map<PieceType, Map<PieceColor, PImage>> cache;
  
  public Tileset(PImage image, int hOffset,int xOffset, int yOffset, ArrayList<Integer[]> baseCols)
  {
    specs = new HashMap<>();
    colors = new HashMap<>();
    
    cache = new HashMap<>();
    this.image = image;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.hOffset = hOffset;
    
    baseColors = new ColorMap(baseCols.stream().map(Color::new).toArray(i -> new Color[i]));
  }
  
  public void addSpec(PieceType piece, int x, int y, int w, int h, int ox, int oy)
  {
    specs.put(piece, new PieceSpec(x,y,w,h,ox,oy));
  }
  
  public void addColor(PieceColor color, ColorMap map)
  {
    colors.put(color, map);
  }
  
  public PImage imageForTypeAndColor(PieceType type, PieceColor color)
  {
    Map<PieceColor, PImage> entry = cache.computeIfAbsent(PieceType.CAP, t -> new HashMap<>());
    PImage image = entry.computeIfAbsent(color, c -> createColoredCopy(this.image, baseColors, colors.get(c)));
    printStatistics();
    return image;
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
  
  private static PImage createColoredCopy(PImage source, ColorMap base, ColorMap replacement)
  {
    PImage tex = Main.sketch.createImage(source.width, source.height, Sketch.ARGB);

    tex.loadPixels();
    source.loadPixels();

    for (int i = 0; i < tex.width*tex.height; ++i)
    {
      boolean found = false;
      
      for (int j = 0; j < base.size(); ++j)
      {
        if (source.pixels[i] == base.get(j))
        {
          tex.pixels[i] = replacement.get(j);
          found = true;
        }
      }
      
      if (!found)
        tex.pixels[i] = source.pixels[i];
    }
    
    tex.updatePixels();
    source.updatePixels();
    
    return tex;
  }
  
  private void printStatistics()
  {
    long count = cache.values().stream().flatMap(m -> m.values().stream()).count();
    System.out.println("Image cache size: "+count);
  }
  
  // WHITE 231 208 172 163
  // BLACK 
}
