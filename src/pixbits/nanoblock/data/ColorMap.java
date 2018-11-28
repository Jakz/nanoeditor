package pixbits.nanoblock.data;

import java.util.Arrays;
import java.util.Collection;

import com.pixbits.lib.ui.color.Color;

public class ColorMap
{
  private final Color[] colors;
  private final int[] icolors;

  public ColorMap(Color... colors)
  {
    this.colors = colors;
    icolors = Arrays.stream(colors)
        .mapToInt(Color::toInt)
        .toArray();
  }
  
  public int size() { return colors.length; }
  
  public Color getAsColor(int index) { return colors[index]; }  
  public int get(int index) { return icolors[index]; }
}
