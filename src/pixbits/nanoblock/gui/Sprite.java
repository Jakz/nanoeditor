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
        int p = src[sw*(rect.y + y) + (rect.x + x)];

        if ((p & 0xFF000000) != 0)
        {
          final int fi = dw*(y + position.y + offsetY) + position.x + offsetX + x;
          
          if (fi >= 0 && fi < dest.length)
            dest[fi] = p;
        }
      }
    
    gfx.updatePixels();
  }

  @Override
  public int compareTo(Sprite o) 
  { 
   //if (key.piece.type != PieceType.CAP && o.key.piece.type != PieceType.CAP)
   //  System.out.println("comparing "+this.key+" with "+o.key+" : "+key.compareTo(o.key));

    return key.compareTo(o.key); 
  }
}
