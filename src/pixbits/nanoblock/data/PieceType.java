package pixbits.nanoblock.data;

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
}
