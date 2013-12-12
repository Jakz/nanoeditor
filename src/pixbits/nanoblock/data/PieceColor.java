package pixbits.nanoblock.data;

import java.awt.Color;

public enum PieceColor {
  WHITE(c(233,233,233), c(164,164,164)),
  BLACK(c(76,76,76), c(47,47,47)),
  GREEN_LIME(c(164,219,15), c(107,166,11)),
  YELLOW(c(238,223,38), c(202,175,26)),
  PINK(c(250,170,185), c(227,100,106))
  ;
  
  public final Color fillColor;
  public final Color strokeColor;
  
  PieceColor(Color fillColor, Color strokeColor)
  {
    this.fillColor = fillColor;
    this.strokeColor = strokeColor;
  }
  
  public static Color c(int r, int g, int b)
  {
    return new Color(r,g,b);
  }
  
  public static int count() { return PieceColor.values().length; }
  public static PieceColor at(int index) { return PieceColor.values()[index]; }
}
