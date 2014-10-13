package pixbits.nanoblock.misc;

import javax.swing.ButtonGroup;

public enum Setting
{
  DRAW_CAPS,
  SHOW_PIECE_ORDER,
  HALF_STEPS_ENABLED,
  
  VIEW_SHOW_LAYER_GRID,
  VIEW_LAYER_GRID_ALPHA,
  
  ;
  
  public static enum Path
  {
    LIBRARY
  };
   
  
  public static enum HoverPiece
  {
    DISABLE,
    FRONT_STROKE,
    BACK_STROKE,
    FRONT_STROKE_WITH_BACK_FILL
    
    ;
    
    public static final ButtonGroup GROUP = new ButtonGroup();
  }
  
  public static interface EnumSetter
  {
    void set(Enum value);
    Enum get();
  }
  
  
  public static class HoverSetter implements EnumSetter {
    public void set(Enum piece) { Settings.values.hoverPiece = (HoverPiece)piece; }
    public HoverPiece get() { return (HoverPiece)Settings.values.hoverPiece; }
    public final static HoverSetter INSTANCE = new HoverSetter();
  }
  
}
