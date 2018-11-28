package pixbits.nanoblock.files;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.*;
import processing.core.PImage;

import java.util.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.google.gson.*;

public class TileSetLoader
{
  private static class JsonPieceSpec
  {
    String name;
    int[] offset;
    int[] size;
    int[] pivot;
  }
  
  private static class JsonColorSpec
  {
    String name;
    ArrayList<Integer[]> colors;
  }
  
  private static class JsonTilesetSpec
  {
    String name;
    String image;
    int hOffset;
    int xOffset;
    int yOffset;
    int hAdjust;
    ArrayList<Integer[]> baseColors;
    JsonPieceSpec[] pieces;
    JsonColorSpec[] colors;
  }
    
  public static Tileset loadAndBuild(Path path)
  {
    JsonTilesetSpec spec = loadTileset(path);
    return buildTileset(path.toAbsolutePath().getParent(), spec);
  }
  
  public static JsonTilesetSpec loadTileset(Path path)
  {
    
    
    try
    {      
      Log.i("Loading tileset from "+path+".");
      
      BufferedReader rdr = Files.newBufferedReader(path); //new BufferedReader( new InputStreamReader( /*ClassLoader.getSystemResourceAsStream(TILESET_PATH+fileName))*/);
      
      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      
      JsonTilesetSpec spec = gson.fromJson(rdr, JsonTilesetSpec.class);
      
      rdr.close();
      
      return spec;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
   
    return null;
  }
  
  public static Tileset buildTileset(Path path, JsonTilesetSpec json)
  {
    try 
    {
      BufferedImage image = ImageIO.read(path.resolve(json.image).toFile());
    
      PImage pimage = new PImage(image.getWidth(),image.getHeight(), Sketch.ARGB);
      image.getRGB(0, 0, pimage.width, pimage.height, pimage.pixels, 0, pimage.width);
      pimage.updatePixels();
            
      Tileset tileset = new Tileset(pimage, json.hOffset, json.xOffset, json.yOffset, json.baseColors);
      
      for (JsonPieceSpec piece : json.pieces)
        tileset.addSpec(PieceType.forName(piece.name), piece.offset[0], piece.offset[1], piece.size[0], piece.size[1], piece.pivot[0], piece.pivot[1]);
      
      for (JsonColorSpec color : json.colors)
        tileset.addColor(PieceColor.forName(color.name), color.colors);
      
        return tileset;
    }
    catch (Exception e)
    {
      Log.e(e);
      return null;
    }
  }
  
}
