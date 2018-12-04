package pixbits.nanoblock.data;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.Log;

public class ModelInfo
{
  public String author;
  public String name;
  public String source;
  public String modelNumber;
  public int width;
  public int height;
  public int levels;
  public String hashCode;
  
  public ModelInfo dupe()
  {
    ModelInfo mi = new ModelInfo();
    
    mi.author = author;
    mi.name = new String(name);
    mi.source = new String(source);
    mi.modelNumber = new String(modelNumber);
    
    mi.width = width;
    mi.height = height;
    mi.levels = levels;
    mi.generateRandomHash();
    return mi;
  }
  
  public void initialize(int w, int h, int l)
  {
    name = "Name";
    author = "Author";
    source = "";
    modelNumber = "";
    width = w;
    height = h;
    levels = l;
    
    while (hashCode == null || !Library.i().isHashUnique(hashCode))
      generateRandomHash();
  }
  
  public void generateRandomHash()
  {
    try
    {
      //TODO: check for unique in library?
      
      MessageDigest digest = MessageDigest.getInstance("MD5");
      Random r = new Random();
      
      byte[] bytes = new byte[32];
      r.nextBytes(bytes);
      byte[] hashCode = digest.digest(bytes);
      
      BigInteger bi = new BigInteger(1,hashCode);
      this.hashCode = String.format("%0" + (hashCode.length << 1) + "X", bi);
    }
    catch (Exception e)
    {
      Log.e(e);
    }
  }
}
