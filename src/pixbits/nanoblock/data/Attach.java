package pixbits.nanoblock.data;

public enum Attach
{ 
  TOP, 
  BOTTOM,
  
  LEFT,
  RIGHT,
  
  NONE 
  
  ;
  
  public static Attach[] horizontal = { LEFT, NONE, RIGHT };
  public static Attach[] vertical = { TOP, NONE, BOTTOM };
}