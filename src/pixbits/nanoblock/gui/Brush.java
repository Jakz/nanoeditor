package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;

public class Brush
{
  enum Mode
  {
    PLACING,
    REMOVING,
    NONE
  };
  
  public static Mode mode = Mode.NONE;
  
  public static PieceColor color = PieceColor.BLACK;
  
  
  public static boolean typeInverted = false;
  private static PieceType type = PieceType.P1x1;
  public static void setType(PieceType type) { Brush.type = type; }
  public static PieceType realType() { return type; }
  public static PieceType type()
  {
    if (!typeInverted) return type;
    else
    {
      return PieceType.getRotation(type);
    }
  }
  
  public static Tileset tileset;
}
