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
      if (x < o.x || y < o.y || z < o.z) return -1;
      else return type.compareTo(o.type);
    }
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
  
  public void draw(PGfx gfx)
  {
    gfx.blend(Brush.tileset.image, rect, position.x, position.y, PConstants.BLEND);
  }

  @Override
  public int compareTo(Sprite o) { return key.compareTo(o.key); }
}
