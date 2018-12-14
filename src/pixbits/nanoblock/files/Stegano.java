package pixbits.nanoblock.files;

public class Stegano
{
  public static enum AlphaMode { DONT_USE, USE, USE_BEFORE_COLOR };
  
  private AlphaMode mode;
  private int bpc;
    
  private final int ALPHA_MASK = 0xFF000000;
  
  
  public Stegano(AlphaMode mode)
  {
    this.mode = mode;
    this.bpc = -1;
  }
  
  public Stegano(AlphaMode mode, int bitsPerChannel)
  {
    this.mode = mode;
    this.bpc = bitsPerChannel;
  }
  
  public Stegano(int bitsPerChannel)
  {
    this(AlphaMode.USE_BEFORE_COLOR, bitsPerChannel);
  }
  
  public int countUsableSpace(int[] container)
  {
    int countInBits = 0;
    for (int pixel : container)
    {
      /* alpha is zero, we can use whole color channels */
      if ((pixel & ALPHA_MASK) == 0)
        countInBits += 24;
      else
        countInBits += bpc*3;
    }
    
    return countInBits / 8;
  }
  
  private void storeInAlpha(int[] container, int containerOffset, byte[] data, int dataOffset)
  {
    int pixel = container[containerOffset];
    
    /* reset other channels, alpha is 0 so who cares */
      pixel = 0;
    
    /* store up to 3 bytes */
    for (int i = 0; i < 3 && dataOffset < data.length; ++i, ++dataOffset)
      pixel |= data[dataOffset] << (8*i);
    
    container[containerOffset] = pixel;
  }
    
  public int[] embed(int[] container, byte[] data)
  {
    int c = 0;
    
    /* first we need to fill all the alpha data */
    //if (mode == AlphaMode.USE_BEFORE_COLOR)
    {
      for (int p = 0; p < container.length && c < data.length; ++p)
      {
        int pixel = container[p];
        if ((pixel & ALPHA_MASK) == 0)
        {
          storeInAlpha(container, p, data, c);
          c += 3;
        }
      }

    }
    
    if (c < data.length)
      throw new IllegalArgumentException("Not enough space to store data");
    
    return container;
  }
}
