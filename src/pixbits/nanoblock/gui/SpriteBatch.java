package pixbits.nanoblock.gui;

import java.awt.Point;
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
