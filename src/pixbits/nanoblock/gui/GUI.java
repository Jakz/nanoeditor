package pixbits.nanoblock.gui;

import com.pixbits.lib.ui.color.Color;

public class GUI
{
  public static class Theme
  {
    Color background;
  }
  
  public static int margin = 20;
  public static int scrollBarWidth = 20;
  
  public static int piecePaletteCellSize = 100;

  private static Theme DEFAULT_THEME = new Theme();
  
  static
  {
    DEFAULT_THEME.background = new Color(220, 220, 220);
  }
  
  public static Theme theme = DEFAULT_THEME;

}
