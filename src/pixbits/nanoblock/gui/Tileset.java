package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.Main;
import processing.core.PImage;
import java.util.*;

public class Tileset {
  final public PImage image;
  final public int hOffset;
  final public int xOffset;
  final public int yOffset;
  final public Map<PieceType, PieceSpec> specs;
  
  Tileset(String image, int hOffset, int xOffset, int yOffset)
  {
    specs = new HashMap<PieceType, PieceSpec>();
    this.image = Main.sketch.loadImage(image);
    this.hOffset = hOffset;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }
  
  public void addSpec(PieceType piece, int x, int y, int w, int h, int ox, int oy)
  {
    specs.put(piece, new PieceSpec(x,y,w,h,ox,oy));
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
}
