package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.Main;
import processing.core.PImage;
import java.util.*;
import java.awt.Point;
import java.awt.Rectangle;

public class Tileset {
  final public PImage image;
  final public int hOffset;
  final public int xOffset;
  final public int yOffset;
  final public Map<PieceType, PieceSpec> specs;
  final public Map<PieceColor, Point> colors;
  
  public Tileset(String image, int hOffset, int xOffset, int yOffset)
  {
    specs = new HashMap<PieceType, PieceSpec>();
    colors = new HashMap<PieceColor, Point>();
    this.image = Main.sketch.loadImage(image);
    this.hOffset = hOffset;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }
  
  public void addSpec(PieceType piece, int x, int y, int w, int h, int ox, int oy)
  {
    specs.put(piece, new PieceSpec(x,y,w,h,ox,oy));
  }
  
  public void addColor(PieceColor color, int ox, int oy)
  {
    colors.put(color, new Point(ox,oy));
  }
  
  public Point color(PieceColor color)
  {
    return colors.get(color);
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
    Point cspec = colors.get(color);
    
    return new Rectangle(spec.x + cspec.x, spec.y + cspec.y, spec.w, spec.h);
  }
}
