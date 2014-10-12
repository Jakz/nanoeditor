package pixbits.nanoblock.files;

import pixbits.nanoblock.data.*;

import java.util.*;
import java.io.*;
import com.google.gson.*;

public class ModelLoader
{
  private static class JsonPiece
  {
    String type;
    String color;
    int x;
    int y;
  }
  
  private static class JsonModel
  {
    public ModelInfo info;
    JsonPiece[][] pieces;
  }
  
  public static void saveModel(Model model, File file)
  {
    try
    {
      BufferedWriter wrt = new BufferedWriter(new FileWriter(file));
      
      JsonModel jmodel = new JsonModel();
      jmodel.info = model.getInfo();
      
      List<ArrayList<JsonPiece>> pieces = new ArrayList<ArrayList<JsonPiece>>();
      ArrayList<JsonPiece> current = null;
      for (Level l : model)
      {
        current = new ArrayList<JsonPiece>();
        pieces.add(current);
        for (Piece piece : l)
        {
          if (piece.type != PieceType.CAP)
          {
            JsonPiece jpiece = new JsonPiece();
            jpiece.type = PieceType.forPiece(piece.type);
            jpiece.color = PieceColor.forColor(piece.color);
            jpiece.x = piece.x;
            jpiece.y = piece.y;
            current.add(jpiece);
          }
        }
      }
      
      jmodel.pieces = new JsonPiece[pieces.size()][];
      for (int i = 0; i < pieces.size(); ++i)
      {
        jmodel.pieces[i] = new JsonPiece[pieces.get(i).size()];
        pieces.get(i).toArray(jmodel.pieces[i]);
      }
      
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      Gson gson = builder.create();
      wrt.write(gson.toJson(jmodel, JsonModel.class));
      wrt.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static ModelInfo loadInfo(File file) throws FileNotFoundException, IOException
  {
    BufferedReader rdr = new BufferedReader(new FileReader(file));
    
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    
    JsonModel jmodel = gson.fromJson(rdr, JsonModel.class);
    
    rdr.close();
    
    return jmodel.info;
  }
  
  public static Model loadModel(File file)
  {
    try
    {    
      if (file.exists())
      {
        BufferedReader rdr = new BufferedReader(new FileReader(file));
        
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        
        JsonModel jmodel = gson.fromJson(rdr, JsonModel.class);
        
        Model model = new Model(jmodel.info);
        model.allocateLevels(jmodel.info.levels+1);
        
        JsonPiece[][] pieces = jmodel.pieces;
        
        for (int i = 0; i < pieces.length; ++i)
        {
          JsonPiece[] level = pieces[i];
          for (int j = 0; j < level.length; ++j)
          {
            JsonPiece piece = level[j];
            model.addPiece(model.levelAt(i), PieceType.forName(piece.type), PieceColor.forName(piece.color), piece.x, piece.y);
          }
        }
        
        rdr.close();
        
        return model;
      }
      else
        return null;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
}
