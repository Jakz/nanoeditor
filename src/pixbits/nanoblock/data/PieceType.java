package pixbits.nanoblock.data;

import java.util.*;

public class PieceType
{
  public final int width, height;
  public final boolean rounded;
  public final boolean monocap;

  PieceType(int width, int height, boolean rounded, boolean monocap)
  {
    this.width = width;
    this.height = height;
    this.rounded = rounded;
    this.monocap = monocap;
  }
  
  public final static PieceType CAP = new PieceType(1,1,false,false);
  
  public final static PieceType P1x1 = new PieceType(1,1,false,false);
  public final static PieceType P1x1r = new PieceType(1,1,true,false);
  
  public final static PieceType P2x1 = new PieceType(2,1,false,false);
  public final static PieceType P1x2 = new PieceType(1,2,false,false);
  
  public final static PieceType P2x1c = new PieceType(2,1,false,true);
  public final static PieceType P1x2c = new PieceType(1,2,false,true);
  
  public final static PieceType P2x1r = new PieceType(2,1,true, false);
  
  public final static PieceType P3x1 = new PieceType(3,1,false,false);
  public final static PieceType P3x1r = new PieceType(3,1,true,false);
  public final static PieceType P1x3 = new PieceType(1,3,false,false);
  public final static PieceType P1x3r = new PieceType(1,3,true,false);
  
  public final static PieceType P4x1 = new PieceType(4,1,false,false);
  public final static PieceType P4x1r = new PieceType(4,1,true,false);
  public final static PieceType P1x4 = new PieceType(1,4,false,false);
  public final static PieceType P1x4r = new PieceType(1,4,true,false);
  
  public final static PieceType P2x2 = new PieceType(2,2,false,false);
  
  public final static PieceType P4x2 = new PieceType(4,2,false,false);
  public final static PieceType P2x4 = new PieceType(2,4,false,false);
  
  public final static PieceType P8x2 = new PieceType(8,2,false,false);
  public final static PieceType P2x8 = new PieceType(2,8,false,false);
  
  public final static PieceType[] pieces = new PieceType[] {
    P1x1, P1x1r, P2x1, P1x2, P2x1r, P2x1c, P1x2c, P2x2, P3x1, P1x3, P4x1, P1x4, P4x2, P2x4, P8x2, P2x8
  };
  
  public final static PieceType[] spieces = new PieceType[] {
    P1x1, P1x1r, P2x1, P2x1r, P2x1c, P2x2, P3x1, P4x1, P4x2, P8x2
  };
  
  public static int count() { return pieces.length; }
  public static PieceType at(int index) { return pieces[index]; }
  
  private static final Map<String, PieceType> mapping = new HashMap<String, PieceType>();
  private static final Map<PieceType, String> mapping2 = new HashMap<PieceType, String>();
  private static final Map<PieceType, PieceType> rotations = new HashMap<PieceType, PieceType>();

  public final String toString() { return ""+width+"x"+height+(rounded?"r":"")+(this == CAP?"c":""); }
  
  public static PieceType getRotation(PieceType piece)
  { 
    PieceType pt = rotations.get(piece);
    return pt != null ? pt : piece;
  }
  private static void addRotation(PieceType p1, PieceType p2)
  {
    rotations.put(p1, p2);
    rotations.put(p2, p1);
  }
  
  public static void initMapping()
  {
    mapping.put("cap", CAP);

    mapping.put("1x1", P1x1);
    mapping.put("1x1r", P1x1r);
    
    mapping.put("2x1", P2x1);
    mapping.put("1x2", P1x2);
    addRotation(P2x1, P1x2);
    
    mapping.put("2x1c", P2x1c);
    mapping.put("1x2c", P1x2c);
    addRotation(P2x1c, P1x2c);
    
    mapping.put("2x1r", P2x1r);
    
    mapping.put("3x1", P3x1);
    mapping.put("1x3", P1x3);
    mapping.put("3x1r", P3x1r);
    mapping.put("1x3r", P1x3r);
    addRotation(P3x1r, P1x3r);
    addRotation(P3x1, P1x3);
    
    mapping.put("4x1", P4x1);
    mapping.put("1x4", P1x4);
    mapping.put("4x1r", P4x1r);
    mapping.put("1x4r", P1x4r);
    addRotation(P4x1r, P1x4r);
    addRotation(P4x1, P1x4);
    
    mapping.put("2x2", P2x2);
    
    mapping.put("4x2", P4x2);
    mapping.put("2x4", P2x4);
    addRotation(P4x2, P2x4);
    
    mapping.put("8x2", P8x2);
    mapping.put("2x8", P2x8);
    addRotation(P8x2, P2x8);
    
    for (String s : mapping.keySet())
      mapping2.put(mapping.get(s), s);
  }
  
  public static PieceType forName(String name) { return mapping.get(name); }
  public static String forPiece(PieceType type) { return mapping2.get(type); }
}
