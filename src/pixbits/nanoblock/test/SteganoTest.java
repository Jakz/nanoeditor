package pixbits.nanoblock.test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import org.junit.Test;

import pixbits.nanoblock.files.Stegano;

public class SteganoTest
{
  private int[] buildRandomImageData(int length)
  {
    return new int[length];
  }
  
  private class IA
  {
    public final int[] data;
    
    public IA(int[] data) { this.data = data; }
    @Override public boolean equals(Object other) { return other instanceof IA && ((IA)other).data.equals(data); }
    @Override public int hashCode() { return Arrays.hashCode(data); }
    public String toString() { return Arrays.toString(data); }
  }
  
  private IA ia(int... data) { return new IA(data); }
  
  @Test
  public void testUpToThreeInAlpha()
  {
    Stegano stegano = new Stegano(2);
    int[] data = buildRandomImageData(1);
    
    assertThat(stegano.embed(data, new byte[] {0x12} ), is(new int[] { 0x00000012 }));
    assertThat(stegano.embed(data, new byte[] {0x12, 0x34} ), is(new int[] { 0x00003412 }));
    assertThat(stegano.embed(data, new byte[] {0x12, 0x34, 0x56} ), is(new int[] { 0x00563412 }));
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void testExceptionThrownWhenStoring4InSingleAlpha()
  {
    Stegano stegano = new Stegano(2);
    int[] data = buildRandomImageData(1);
    assertThat(stegano.embed(data, new byte[] {0x12, 0x34, 0x56, 0x78} ), is(new int[] { 0x00563412 }));
  }
}
