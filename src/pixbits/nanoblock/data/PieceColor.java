package pixbits.nanoblock.data;

import java.awt.Color;

public enum PieceColor {
  WHITE("White"),
  BLACK("Black"),
  GREEN_LIME("Green Lime"),
  YELLOW("Yellow"),
  ORANGE("Orange"),
  PINK("Pink"),
  BROWN("Brown")
  ;
  
  public Color fillColor;
  public Color strokeColor;
  public final String caption;
  
  PieceColor(String caption) { this.caption = caption; }
  
  public void setColors(Color strokeColor, Color fillColor)
  {
    this.strokeColor = strokeColor;
    this.fillColor = fillColor;
  }
  
  public static Color c(int r, int g, int b)
  {
    return new Color(r,g,b);
  }
  
  public static int count() { return PieceColor.values().length; }
  public static PieceColor at(int index) { return PieceColor.values()[index]; }
  public static PieceColor forName(String name) { return valueOf(name); }
  public static String forColor(PieceColor color) { return color.name(); }
}
