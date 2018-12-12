package pixbits.nanoblock.files;

import java.util.Arrays;

public class BinaryString
{
  private byte[] buffer;
  private int position;
  
  public BinaryString()
  {
    buffer = new byte[256];
    position = 0;
  }
  
  public BinaryString(byte[] buffer)
  {
    this.buffer = buffer;
    this.position = 0;
  }

  private void ensureCapacity()
  {
    if (position >= buffer.length)
      buffer = Arrays.copyOf(buffer, buffer.length + buffer.length/2);
  }

  public int tell() { return position; }
  public void seek(int position) { this.position = position; }
  
  public void reserve(int size)
  {
    write(0, size);
  }
  
  public void write(long value, int size)
  {
    ensureCapacity();
    for (int i = 0; i < size; ++i)
    {
      buffer[position++] = (byte)(value & 0xFF);
      value >>>= 8;
    }
  }
  
  public void write(int value, int size)
  {
    write((long)value, size);
  }
  
  public void writeByte(int value)
  {
    ensureCapacity();
    buffer[position] = (byte)(value & 0xFF);
    ++position;
  }
  
  public void writeWord(int value)
  {
    ensureCapacity();
    buffer[position++] = (byte)(value & 0xFF);
    buffer[position++] = (byte)((value >> 8) & 0xFF);
  }
  
  public void writeWordHalf(int value)
  {
    ensureCapacity();
    buffer[position++] = (byte)(value & 0xFF);
    buffer[position++] = (byte)((value >> 8) & 0xFF);
    buffer[position++] = (byte)((value >> 16) & 0xFF);
  }
  
  public void writeDWord(int value)
  {
    ensureCapacity();
    buffer[position++] = (byte)(value & 0xFF);
    buffer[position++] = (byte)((value >> 8) & 0xFF);
    buffer[position++] = (byte)((value >> 16) & 0xFF);
    buffer[position++] = (byte)((value >> 24) & 0xFF);
  }
  
  public int size() { return position; }
  public byte[] data() { return buffer; }
  
  public String toString()
  {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < position; ++i)
    {
      str.append(Integer.toHexString(buffer[i] & 0xFF));
    }
    
    return str.toString();
  }
}
