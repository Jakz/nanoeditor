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
  public static PieceType type = PieceType.P1x1;
  public static Tileset tileset;
}
