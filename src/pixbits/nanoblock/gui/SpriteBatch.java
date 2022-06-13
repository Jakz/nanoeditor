package pixbits.nanoblock.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import processing.core.PGfx;

public class SpriteBatch
{
  private final Point position;
  
  private final List<Sprite> sprites;
  private boolean dirty;
  
  public SpriteBatch()
  {
    this.sprites = new ArrayList<>();
    this.dirty = false;
    this.position = new Point(0,0);
  }
  
  public void setPosition(int x, int y) { position.setLocation(x, y); }
  public Point position() { return position; }
  
  public boolean isEmpty() { return sprites.isEmpty(); }
  public void clear() { sprites.clear(); }
  public int size() { return sprites.size(); }
  
  public Rectangle bounds()
  {
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
    
    for (Sprite sprite : sprites)
    {
      minX = Math.min(minX, sprite.position.x);
      minY = Math.min(minY, sprite.position.y);
      
      maxX = Math.max(maxX, sprite.position.x + sprite.rect.width);
      maxY = Math.max(maxY, sprite.position.y + sprite.rect.height);
    }
    
    return new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }
  
  public void draw(PGfx gfx)
  {
    sortIfNeeded();
    
    for (Sprite sprite : sprites)
      sprite.draw(gfx, position.x, position.y);
  }
  
  protected void sort()
  {
    Collections.sort(sprites);
  }
  
  public void sortIfNeeded()
  {
    if (dirty)
    {
      sort();
      dirty = false;
    }
  }
  
  void add(Sprite sprite)
  {
    sprites.add(sprite);
    dirty = true;
  }
  
  void remove(Sprite sprite)
  {
    sprites.remove(sprite);
    dirty = true;
  }
}
