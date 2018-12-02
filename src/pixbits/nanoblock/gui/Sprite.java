package pixbits.nanoblock.gui;

import java.awt.Point;
import java.awt.Rectangle;

import processing.core.PConstants;
import processing.core.PGfx;

public class Sprite implements Comparable<Sprite>
{
  public static enum Type
  {
    TOP,
    WALL
  };
  
  public static class Key implements Comparable<Key>
  {
    private final int x;
    private final int y;
    private final int z;
    private final Type type;
    
    public Key(int x, int y, int z, Type type)
    {
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
      else if (y != o.y)
        return Integer.compare(y, o.y);
      else if (x != o.x)
        return Integer.compare(x, o.x);
      else 
        return type.compareTo(o.type);
    }
    
    public String toString() { return String.format("(%d, %d, %d, %s)", x, y, z, type.toString()); }
  }
  
  private final Key key;
  private final Point position;
  private final Rectangle rect;
  
  public Sprite(Key key, Point position, Rectangle rect)
  {
    this.key = key;
    this.position = position;
    this.rect = rect;
  }
  
  public void draw(PGfx gfx) { draw(gfx, 0, 0); }
  public void draw(PGfx gfx, int offsetX, int offsetY)
  {
    gfx.blend(Brush.tileset.image, rect, position.x + offsetX, position.y + offsetY, PConstants.BLEND);
  }

  @Override
  public int compareTo(Sprite o) { return key.compareTo(o.key); }
}
