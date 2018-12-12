package pixbits.nanoblock.gui;

import java.awt.Point;
import java.awt.Rectangle;

import pixbits.nanoblock.data.Piece;
import pixbits.nanoblock.data.PieceType;
import processing.core.PConstants;
import processing.core.PGfx;
import processing.core.PImage;

public class Sprite implements Comparable<Sprite>
{
  public static enum Type
  {
    BOTTOM,
    TOP,
    WALL
  };
  
  public static class Key implements Comparable<Key>
  {
    private final Piece piece;
    private final int x;
    private final int y;
    private final int z;
    private final Type type;
    
    public Key(Piece piece, int x, int y, int z, Type type)
    {
      this.piece = piece;
      
      this.x = x;
      this.y = y;
      this.z = z;
      this.type = type;
    }
    
    @Override
    public int compareTo(Key o) 
    {             
      if (z != o.z)
        return Integer.compare(z, o.z);
      else if (y != o.y || x != o.x)
        return Integer.compare(y + x, o.y + o.x);
      else 
        return type.compareTo(o.type);
    }
    
    @Override
    public boolean equals(Object o) {
      if (o instanceof Key)
      {
        Key k = (Key)o;
        return x == k.x && y == k.y && z == k.z && type == k.type;
      }
      else
        return false;
    }
    
    public String toString() { return String.format("(%s, %d, %d, %d, %s)", piece.type, x, y, z, type.toString()); }
  }
  
  private final Key key;
  
  public final PImage texture;
  public final Point position;
  public final Rectangle rect;

  public Sprite(Key key, PImage texture, Point position, Rectangle rect)
  {
    this.key = key;
    
    this.texture = texture;
    this.position = position;
    this.rect = rect;
  }
  
  public void draw(PGfx gfx) { draw(gfx, 0, 0); }
  public void draw(PGfx gfx, int offsetX, int offsetY)
  {
    gfx.loadPixels();
    texture.loadPixels();
    final int[] dest = gfx.pixels();
    final int[] src = texture.pixels();
    
    int dw = gfx.width(), sw = texture.width();
    
    for (int y = 0; y < rect.height; ++y)
      for (int x = 0; x < rect.width; ++x)
      {
        int dp = src[sw*(rect.y + y) + (rect.x + x)];
        int alpha = (dp >> 24) & 0xFF;
                
        if (alpha != 0)
        {              
          final int fi = dw*(y + position.y + offsetY) + position.x + offsetX + x;

          if (fi >= 0 && fi < dest.length)
          {
            final int sp = dest[fi];
            int fc = blend(sp, dp, alpha);
            dest[fi] = fc | (alpha << 24);
          }
        }
      }
    
    gfx.updatePixels();
  }
  
  public int blend(int sp, int dp, int alpha)
  {
    final float falpha = alpha / 255.0f;
    final float nalpha = 1.0f - falpha;
    
    final int sr = ((sp >> 16) & 0xFF);
    final int sg = ((sp >> 8) & 0xFF);
    final int sb = (sp & 0xFF);
    
    final int dr = (dp >> 16) & 0xFF;
    final int dg = (dp >> 8) & 0xFF;
    final int db = dp & 0xFF;              

    final int fr = (int)(sr*nalpha + dr*falpha);
    final int fg = (int)(sg*nalpha + dg*falpha);
    final int fb = (int)(sb*nalpha + db*falpha);
    
    /*if (fr < 0 || fr > 255 || fg < 0 || fg > 255 || fb < 0 || fb > 255)
      throw new IllegalArgumentException("Value out of bounds");*/
    
    return fr << 16 | fg << 8 | fb;
  }

  @Override
  public int compareTo(Sprite o) 
  { 
   //if (key.piece.type != PieceType.CAP && o.key.piece.type != PieceType.CAP)
   //  System.out.println("comparing "+this.key+" with "+o.key+" : "+key.compareTo(o.key));

    return key.compareTo(o.key); 
  }
}
