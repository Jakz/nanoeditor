package pixbits.nanoblock.data;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.pixbits.lib.functional.IntBiConsumer;

public class PieceType
{
  public final int width, height;
  public final boolean rounded;
  private final int[][] caps;
  private final PiecePart[] parts;
  private final int[][][] masks;

  PieceType(int width, int height, boolean rounded, int[][] parts, int[][] caps)
  {
    this.width = width;
    this.height = height;
    this.rounded = rounded;
    this.caps = caps;
    
    if (parts != null)
    {
      this.parts = new PiecePart[parts.length];
      
      int[][][] partsGrid = new int[width][height][];
      
      for (int[] part : parts)
        partsGrid[part[0]][part[1]] = part;
      
      for (int i = 0; i < parts.length; ++i)
      {
        final int x = parts[i][0], y = parts[i][1];
        PiecePart part = new PiecePart(x, y, 
            y >= height - 1 || partsGrid[x][y+1] == null, 
            x >= width - 1 || partsGrid[x+1][y] == null
        );
        
        this.parts[i] = part;
      }
    }
    else
      this.parts = null;
    
    this.masks = new int[3][width][height];
    

    for (int i = 0; i < width; ++i)
      for (int j = 0; j < height; ++j)
      {      
        masks[0][i][j] = 0;
        masks[0][i][j] |= j > 0 ? Direction.NORTH.mask : 0;
        masks[0][i][j] |= i > 0 ? Direction.WEST.mask : 0;
        masks[0][i][j] |= j < height - 1 ? Direction.SOUTH.mask : 0;
        masks[0][i][j] |= i < width - 1 ? Direction.EAST.mask : 0;
        
        masks[1][i][j] = (i > 0 ? 1 : 0) | (i < width - 1 ? 2 : 0);
        masks[2][i][j] = 4 + (j > 0 ? 2 : 0) | (j < height - 1 ? 1 : 0);
        
        /* adjustments of tiles for rounded pieces */
        if (rounded)
        {
          if (width == 1 && height == 1)
          {
            masks[0][i][j] = 20;
            masks[1][i][j] = 10;
            masks[2][i][j] = 12;
          }
          else
          {
            if (masks[0][i][j] == Direction.WEST.mask)
            {
              masks[0][i][j] = 16;
              
              masks[1][i][j] = 9;
              masks[2][i][j] = 12;
            }           
            else if (masks[0][i][j] == Direction.NORTH.mask)
            {
              masks[0][i][j] = 17;
              
              masks[1][i][j] = 10;
              masks[2][i][j] = 13;

            }
            else if (masks[0][i][j] == Direction.EAST.mask)
            {
              masks[0][i][j] = 18;
              masks[1][i][j] = 8;
            }
            else if (masks[0][i][j] == Direction.SOUTH.mask) 
            {
              masks[0][i][j] = 19;
              masks[2][i][j] = 11;
            }
          }
        }
      }
  }
  
  PieceType(int width, int height, boolean rounded)
  {
    this(width, height, rounded, null, null);
  }
  
  public int maxWidth() { return width; }
  public int maxHeight() { return height; }
  
  public boolean isConvex() { return true; }
  
  public void forEachCap(IntBiConsumer consumer)
  {
    if (caps != null)
    {
      for (int[] coords : caps)
        consumer.accept(coords[0], coords[1]);
    }
    else
    {
      forEachPart(c -> consumer.accept(c.x*2, c.y*2));
    }
  }
  
  public boolean hasPartAt(int x, int y)
  {
    return x >= 0 && y >= 0 && x < width && y < height;
  }
  
  public void forEachPart(Consumer<PiecePart> consumer)
  {
    if (parts == null)
    {
      for (int i = 0; i < width; ++i)
        for (int j = 0; j < height; ++j)
        {
          consumer.accept(new PiecePart(i, j, j >= height - 1, i >= width - 1));
        }
    }
    else
      for (PiecePart part : parts)
        consumer.accept(part);
  }
  
  public int mask(int ox, int oy) { return masks[0][ox][oy]; }
  public int maskSouth(int ox, int oy) { return masks[1][ox][oy]; }
  public int maskEast(int ox, int oy) { return masks[2][ox][oy]; }
  
  public final static PieceType CAP    = new PieceType(1, 1, false);
  
  public final static PieceType P1x1   = new PieceType(1, 1, false);
  public final static PieceType P1x1r  = new PieceType(1, 1, true);
  
  public final static PieceType P2x1   = new PieceType(2, 1, false);
  public final static PieceType P1x2   = new PieceType(1, 2, false);
  
  public final static PieceType P2x1c  = new PieceType(2, 1, false, null, new int[][] { { 1, 0 } });
  public final static PieceType P1x2c  = new PieceType(1, 2, false, null, new int[][] { { 0, 1 } });
  
  public final static PieceType P2x1r  = new PieceType(2, 1, true);
  public final static PieceType P1x2r  = new PieceType(1, 2, true);
  
  public final static PieceType P3x1   = new PieceType(3, 1, false);
  public final static PieceType P1x3   = new PieceType(1, 3, false);
  
  public final static PieceType P3x1r  = new PieceType(3, 1, true);
  public final static PieceType P1x3r  = new PieceType(1, 3, true);

  
  public final static PieceType P4x1   = new PieceType(4, 1, false);
  public final static PieceType P4x1r  = new PieceType(4, 1, true);
  public final static PieceType P1x4   = new PieceType(1, 4, false);
  public final static PieceType P1x4r  = new PieceType(1, 4, true);
  
  public final static PieceType P6x1   = new PieceType(6, 1, false);
  public final static PieceType P1x6  = new PieceType(1, 6, false);
  
  public final static PieceType P2x2   = new PieceType(2, 2, false);
  public final static PieceType P2x2c  = new PieceType(2, 2, false, null, new int[][] { { 1, 1 } });
  
  public final static PieceType P2x2lt = new PieceType(2, 2, false, new int[][] { { 0, 0 }, { 1, 0 }, { 0, 1 } }, null);
  
  public final static PieceType P4x2   = new PieceType(4, 2, false);
  public final static PieceType P2x4   = new PieceType(2, 4, false);
  
  public final static PieceType P8x2   = new PieceType(8, 2, false);
  public final static PieceType P2x8   = new PieceType(2, 8, false);
  
  private final static PieceType[] pieces = new PieceType[] {
    P1x1, P1x1r, P2x1, P1x2, P2x1r, P1x2r, P2x1c, P1x2c, P2x2, P2x2c, P2x2lt, P3x1, P1x3, P3x1r, P1x3r, P4x1, P1x4, P6x1, P1x6, P4x2, P2x4, P8x2, P2x8
  };
  
  public final static PieceType[] spieces = new PieceType[] {
    P1x1, P1x1r, P2x1, P2x1r, P2x1c, P2x2, P2x2c, P2x2lt, P3x1, P3x1r, P4x1, P6x1, P4x2, P8x2
  };
  
  public static int count() { return pieces.length; }
  public static PieceType at(int index) { return pieces[index]; }
  public static Iterable<PieceType> pieces() { return Arrays.asList(pieces); }
  
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
    mapping.put("1x2r", P1x2r);
    addRotation(P2x1r, P1x2r);
    
    mapping.put("3x1", P3x1);
    mapping.put("1x3", P1x3);
    addRotation(P3x1, P1x3);
    
    mapping.put("3x1r", P3x1r);
    mapping.put("1x3r", P1x3r);
    addRotation(P3x1r, P1x3r);
    
    mapping.put("4x1", P4x1);
    mapping.put("1x4", P1x4);
    mapping.put("4x1r", P4x1r);
    mapping.put("1x4r", P1x4r);
    addRotation(P4x1r, P1x4r);
    addRotation(P4x1, P1x4);
    
    mapping.put("6x1", P6x1);
    mapping.put("1x6", P1x6);
    addRotation(P6x1, P1x6);
    
    mapping.put("2x2", P2x2);
    mapping.put("2x2c", P2x2c);
    
    mapping.put("2x2lt", P2x2lt);
    
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
