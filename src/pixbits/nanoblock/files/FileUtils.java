package pixbits.nanoblock.files;

import java.io.*;
import com.google.gson.*;

public class FileUtils
{
  public static <T> T readJson(String filename, Class<T> clazz) throws FileNotFoundException, IOException
  {
    return readJson(new File(filename), clazz);
  }
  
  public static <T> T readJson(File file, Class<T> clazz) throws FileNotFoundException, IOException
  {
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    
    BufferedReader rdr = new BufferedReader(new FileReader(file));
    T obj = gson.fromJson(rdr, clazz);
    rdr.close();
    
    return obj;
  }
  
  public static <T> void writeJson(String filename, Class<T> clazz, T object) throws IOException
  {
    writeJson(filename, clazz, object, false);
  }
  
  public static <T> void writeJson(String filename, Class<T> clazz, T object, boolean prettyPrint) throws IOException
  {
    writeJson(new File(filename), clazz, object, prettyPrint);
  }
  
  public static <T> void writeJson(File file, Class<T> clazz, T object) throws IOException
  {
    writeJson(file, clazz, object, false);
  }
  
  public static <T> void writeJson(File file, Class<T> clazz, T object, boolean prettyPrint) throws IOException
  {
    GsonBuilder builder = new GsonBuilder();
    if (prettyPrint) builder.setPrettyPrinting();
    Gson gson = builder.create();
    
    BufferedWriter wrt = new BufferedWriter(new FileWriter(file));
    wrt.write(gson.toJson(object, clazz));
    wrt.close();
  }
  
  public static boolean deleteFile(File file)
  {
    if (file.exists())
    {
      file.delete();
      return true;
    }
    else
      return false;
  }
}
