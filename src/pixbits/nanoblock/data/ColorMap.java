package pixbits.nanoblock.data;

import java.util.Arrays;
import java.util.Collection;

import com.pixbits.lib.ui.color.Color;

public class ColorMap
{
  private final Color[] colors;

  public ColorMap(Color... colors)
  {
    this.colors = colors;
  }
  
  public ColorMap(int... colors)
  {
    this.colors = Arrays.stream(colors)
      .mapToObj(Color::new)
      .toArray(i -> new Color[i]);
  }
  
  public int size() { return colors.length; }
  
  public Color getAsColor(int index) { return colors[index]; }  
  public int get(int index) { return colors[index].toInt(); }
}
