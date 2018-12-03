package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.Main;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.ui.color.Color;

import java.awt.Point;
import java.awt.Rectangle;

public class Tileset
{
  final public PImage image;
  
  final public int hOffset;
  final public int xOffset;
  final public int yOffset;
  final public ColorMap baseColors;
  final private Map<PieceType, PieceSpec> specs;
  final private Map<PieceColor, ColorMap> colors;
  
  final private Map<PieceType, PImage> baseCache;
  final private Map<PieceColor, PImage> cache;
  
  public Tileset(PImage image, int hOffset,int xOffset, int yOffset, ArrayList<Integer[]> baseCols)
  {
    specs = new HashMap<>();
    colors = new HashMap<>();
    
    baseCache =new HashMap<>();
    cache = new HashMap<>();
    
    this.image = image;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.hOffset = hOffset;
    
    baseColors = new ColorMap(baseCols.stream().map(Color::new).toArray(i -> new Color[i]));
  }
  
  public void addSpec(PieceType piece, int x, int y, int w, int h, int ox, int oy, boolean flipX)
  {
    specs.put(piece, new PieceSpec(x,y,w,h,ox,oy,flipX));
  }
  
  public void addColor(PieceColor color, ColorMap map)
  {
    colors.put(color, map);
  }
  
  public PImage imageForTypeAndColor(PieceType type, PieceColor color)
  {    
    PImage image = cache.computeIfAbsent(color, t -> { 
      return createColoredCopy(specs.get(type), this.image, baseColors, colors.get(color));
    });
    
    return image;
  }

  public PieceSpec spec(PieceType type) { return specs.get(type); }
  
  public static class PieceSpec
  {
    public final int x, y;
    public final int w, h;
    public final int ox, oy;
    public final boolean flipX;
    
    PieceSpec(int x, int y, int w, int h, int ox, int oy, boolean flipX)
    {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.ox = flipX ? -(w + ox) : ox;
      this.oy = oy;
      this.flipX = flipX;
    }   
  }
  
  //TODO: make private again
  public static PImage createColoredCopy(PieceSpec spec, PImage source, ColorMap baseColors, ColorMap replacement)
  {
    PImage tex = Main.sketch.createImage(source.width, source.height, Sketch.ARGB);

    tex.loadPixels();
    source.loadPixels();
    
    //TODO: hardcoded for now
    ColorMap base = spec.flipX ? new ColorMap(baseColors.get(0), baseColors.get(2), baseColors.get(1), baseColors.get(3)) : baseColors;

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
  
  // WHITE 231 208 172 163
  // BLACK 
}
