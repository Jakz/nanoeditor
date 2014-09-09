package pixbits.nanoblock.data;

import java.util.*;

public class PieceType
{
  public final int width, height;
  public final boolean rounded;

  PieceType(int width, int height, boolean rounded)
  {
    this.width = width;
    this.height = height;
    this.rounded = rounded;
  }
  
  public final static PieceType CAP = new PieceType(1,1,false);
  
  public final static PieceType P1x1 = new PieceType(1,1,false);
  public final static PieceType P1x1r = new PieceType(1,1,true);
  
  public final static PieceType P2x1 = new PieceType(2,1,false);
  public final static PieceType P1x2 = new PieceType(1,2,false);
  
  public final static PieceType P3x1 = new PieceType(3,1,false);
  public final static PieceType P3x1r = new PieceType(3,1,true);
  public final static PieceType P1x3 = new PieceType(1,3,false);
  public final static PieceType P1x3r = new PieceType(1,3,true);
  
  public final static PieceType P4x1 = new PieceType(4,1,false);
  public final static PieceType P4x1r = new PieceType(4,1,true);
  public final static PieceType P1x4 = new PieceType(1,4,false);
  public final static PieceType P1x4r = new PieceType(1,4,true);
  
  public final static PieceType P2x2 = new PieceType(2,2,false);
  
  public final static PieceType P4x2 = new PieceType(4,2,false);
  public final static PieceType P2x4 = new PieceType(2,4,false);
  
  public final static PieceType P8x2 = new PieceType(8,2,false);
  public final static PieceType P2x8 = new PieceType(2,8,false);
  
  public final static PieceType[] pieces = new PieceType[] {
    P1x1, P1x1r, P2x1, P1x2, P2x2, P3x1, P1x3, P4x1, P1x4, P4x2, P2x4, P8x2, P2x8
  };
  
  public static int count() { return pieces.length; }
  public static PieceType at(int index) { return pieces[index]; }
  
  private static final Map<String, PieceType> mapping = new HashMap<String, PieceType>();
  private static final Map<PieceType, String> mapping2 = new HashMap<PieceType, String>();

  public final String toString() { return ""+width+"x"+height+(rounded?"r":"")+(this == CAP?"c":""); }
  
  public static void initMapping()
  {
    mapping.put("cap", CAP);

    mapping.put("1x1", P1x1);
    mapping.put("1x1r", P1x1r);
    
    mapping.put("2x1", P2x1);
    mapping.put("1x2", P1x2);
    
    mapping.put("3x1", P3x1);
    mapping.put("1x3", P1x3);
    mapping.put("3x1r", P3x1r);
    mapping.put("1x3r", P1x3r);
    
    mapping.put("4x1", P4x1);
    mapping.put("1x4", P1x4);
    mapping.put("4x1r", P4x1r);
    mapping.put("1x4r", P1x4r);
    
    mapping.put("2x2", P2x2);
    
    mapping.put("4x2", P4x2);
    mapping.put("2x4", P2x4);
    
    mapping.put("8x2", P8x2);
    mapping.put("2x8", P2x8);
    
    for (String s : mapping.keySet())
      mapping2.put(mapping.get(s), s);
  }
  
  public static PieceType forName(String name) { return mapping.get(name); }
  public static String forPiece(PieceType type) { return mapping2.get(type); }
}
