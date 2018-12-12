package pixbits.nanoblock.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pixbits.nanoblock.files.BinaryString;

public class BinaryModelTest
{
  @Test
  public void testEmptyBinary()
  {
    BinaryString bs = new BinaryString();
    assertEquals("", bs.toString());
  }
  
  @Test
  public void testSingleByte()
  {
    BinaryString bs = new BinaryString();
    bs.writeByte(0x0c);
    assertEquals("c", bs.toString());
  }
  
  @Test
  public void testMultiByte1()
  {
    BinaryString bs = new BinaryString();
    bs.write(0x0c, 1);
    assertEquals("c", bs.toString());
  }
  
  @Test
  public void testMultiByte2()
  {
    BinaryString bs = new BinaryString();
    bs.write(0x3412, 2);
    assertEquals("1234", bs.toString());
  }
  
  @Test
  public void testMultiByte2unsigned()
  {
    BinaryString bs = new BinaryString();
    bs.write(0xFD12, 2);
    assertEquals("12fd", bs.toString());
  }
  
  @Test
  public void testMultiByte4unsigned()
  {
    BinaryString bs = new BinaryString();
    bs.write(0xFEDCBA90, 4);
    assertEquals("90badcfe", bs.toString());
  }
}
