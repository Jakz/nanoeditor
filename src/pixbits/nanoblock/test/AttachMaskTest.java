package pixbits.nanoblock.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pixbits.nanoblock.data.Direction;
import pixbits.nanoblock.data.PieceType;

public class AttachMaskTest
{
  @Test
  public void testNorthCorner()
  {
    for (PieceType type : PieceType.pieces())
    {
      if (type.width > 1 && type.height > 1)
        assertEquals(Direction.EAST.mask | Direction.SOUTH.mask, type.mask(0, 0));      
    }
  }
  
  @Test
  public void testSouthCorner()
  {
    for (PieceType type : PieceType.pieces())
    { 
      if (type.width > 1 && type.height > 1)
        assertEquals(Direction.WEST.mask | Direction.NORTH.mask, type.mask(type.width-1, type.height-1));      
    }
  }
  
  @Test
  public void testWestCorner()
  {
    for (PieceType type : PieceType.pieces())
    { 
      if (type.width > 1 && type.height > 1)
        assertEquals(Direction.EAST.mask | Direction.NORTH.mask, type.mask(0, type.height-1));      
    }
  }
  
  @Test
  public void testEastCorner()
  {
    for (PieceType type : PieceType.pieces())
    { 
      if (type.width > 1 && type.height > 1)
        assertEquals(Direction.SOUTH.mask | Direction.WEST.mask, type.mask(type.width-1, 0));      
    }
  }
  
  @Test
  public void testSingle()
  {
    for (PieceType type : PieceType.pieces())
      if (type.width == 1 && type.height == 1)
        assertEquals(0, type.mask(0, 0));
  }
}
