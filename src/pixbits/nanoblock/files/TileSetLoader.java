package pixbits.nanoblock.files;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.*;

import java.io.*;
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
    int[] offset;
  }
  
  private static class JsonTilesetSpec
  {
    String name;
    String image;
    int hOffset;
    int xOffset;
    int yOffset;
    int hAdjust;
    JsonPieceSpec[] pieces;
    JsonColorSpec[] colors;
  }
  
  public static Tileset loadAndBuild(String filename)
  {
    JsonTilesetSpec spec = loadTileset(filename);
    return buildTileset(spec);
  }
  
  public static JsonTilesetSpec loadTileset(String fileName)
  {
    try
    {
      File file = new File(fileName);
      BufferedReader rdr = new BufferedReader(new FileReader(file));
      
      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      
      JsonTilesetSpec spec = gson.fromJson(rdr, JsonTilesetSpec.class);
      return spec;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
   
    return null;
  }
  
  public static Tileset buildTileset(JsonTilesetSpec json)
  {
    Tileset tileset = new Tileset(json.image, json.hOffset, json.hAdjust, json.xOffset, json.yOffset);
    
    for (JsonPieceSpec piece : json.pieces)
      tileset.addSpec(PieceType.forName(piece.name), piece.offset[0], piece.offset[1], piece.size[0], piece.size[1], piece.pivot[0], piece.pivot[1]);
    
    for (JsonColorSpec color : json.colors)
      tileset.addColor(PieceColor.forName(color.name), color.offset[0], color.offset[1]);
    
    return tileset;
  }
  
}
