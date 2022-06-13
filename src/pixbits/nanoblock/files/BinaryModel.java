package pixbits.nanoblock.files;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import pixbits.nanoblock.data.Level;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.data.Piece;
import pixbits.nanoblock.data.PieceType;

public class BinaryModel
{
  private final Model model;
  
  private int bytesPerX, bytesPerY, bytesPerZ;
  private int bytesPerPieceType;
    
  private int bytesRequiredFor(int value)
  {
    if (value < 1<<8) return 1;
    else if (value < 1<<16) return 2;
    else if (value < 1<<24) return 3;
    else return 4;
  }
  
  public BinaryModel(Model model)
  {
    this.model = model;
    
    bytesPerX = bytesRequiredFor(model.width());
    bytesPerY = bytesRequiredFor(model.height());
    bytesPerZ = bytesRequiredFor(model.levelCount());
    bytesPerPieceType = bytesRequiredFor(PieceType.count());
  }
  
  public byte[] write()
  {
    BinaryString blob = new BinaryString();

    /* write data */
    blob.write(bytesPerX, 1);
    blob.write(bytesPerY, 1);
    blob.write(bytesPerZ, 1);
    
    blob.write(model.width(), bytesPerX);
    blob.write(model.height(), bytesPerY);
    blob.write(model.levelCount(), bytesPerZ);
    
    for (Level level : model)
    {
      blob.writeWord(level.count());
      
      for (Piece piece : level)
      {
        blob.write(piece.x, bytesPerX);
        blob.write(piece.y, bytesPerY);
        blob.writeByte(piece.color.ordinal());
        blob.writeByte(0x00/*piece.type*/);
      }
    }
    
    /* calculate and write CRC32 */
    long crc = com.pixbits.lib.io.FileUtils.calculateCRC32(blob.data(), 0, blob.size());
    
    System.out.println("Binary length: "+blob.size());
    
    /* compress with deflate */
    byte[] output = new byte[blob.size()];
    
    Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, false);
    compressor.setInput(blob.data());
    compressor.finish();
    int clength = compressor.deflate(output);
    
    System.out.println("Compressed length: "+clength+ " crc: "+Long.toHexString(crc));
    
    /* put CRC32 and uncompressed length at beginning */
    ByteBuffer fdata = ByteBuffer.allocate(clength + 4 + 4);
    fdata.putInt(blob.size());
    fdata.putInt((int)(crc & 0xFFFFFFFFL));
    fdata.put(output, 0, clength);
   
    return fdata.array();
  }
  
  public void load(byte[] data)
  {
    try
    {
      ByteBuffer header = ByteBuffer.wrap(data, 0, 8);
      int size = header.getInt();
      long crc = header.getInt() & 0xFFFFFFFFL;
      
      Inflater decompressor = new Inflater(false);
      decompressor.setInput(data, 8, data.length - 8);
      
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      byte[] buffer = new byte[data.length];
      
      while (!decompressor.finished())
      {
        int c = decompressor.inflate(buffer); 
        System.out.println("decompressed "+c);
        bos.write(buffer, 0, c);
      }
      
      decompressor.end();
      
      int totalSize = bos.size();
      byte[] fdata = Arrays.copyOf(bos.toByteArray(), size);

      long vcrc = com.pixbits.lib.io.FileUtils.calculateCRC32(fdata, 0, fdata.length);
      
      System.out.println("Uncompressed length: "+data.length+"/"+size+ " crc: "+Long.toHexString(crc)+" == "+Long.toHexString(vcrc));       
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
