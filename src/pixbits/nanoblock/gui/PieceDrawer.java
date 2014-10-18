package pixbits.nanoblock.gui;

import java.awt.Rectangle;
import java.util.Iterator;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

public class PieceDrawer
{
  public static void drawPiece(PApplet gfx, int baseX, int baseY, Piece piece, int x, int y, int l, Level level)
  {    
    Tileset ts = Brush.tileset;
    
    int fx = (int) (baseX + (x - y)/2.0f * ts.xOffset);
    int fy = (int) (baseY + (x + y)/2.0f * ts.yOffset);
    
    fy -= ts.hOffset * l;
    
    Tileset.PieceSpec spec = ts.spec(piece.type);
    PImage texture = ts.imageForColor(piece.color);
    gfx.fill(0);
    gfx.blend(texture, spec.x, spec.y, spec.w, spec.h, fx+spec.ox, fy+spec.oy, spec.w, spec.h, PApplet.BLEND);
    
    if (Settings.values.get(Setting.SHOW_PIECE_ORDER))
      gfx.text(""+level.indexOfPiece(piece),fx+spec.ox+spec.w/2,fy+spec.oy+spec.h/2);
  }
  
  public static void drawPiece(PImage gfx, int baseX, int baseY, Piece piece, int x, int y, int l, Level level)
  {    
    Tileset ts = Brush.tileset;
    
    int fx = (int) (baseX + (x - y)/2.0f * ts.xOffset);
    int fy = (int) (baseY + (x + y)/2.0f * ts.yOffset);
    
    fy -= ts.hOffset * l;
    
    Tileset.PieceSpec spec = ts.spec(piece.type);
    PImage texture = ts.imageForColor(piece.color);
    gfx.blend(texture, spec.x, spec.y, spec.w, spec.h, fx+spec.ox, fy+spec.oy, spec.w, spec.h, PApplet.BLEND);
  }
  
  public static void drawModelOnImage(PImage gfx, int baseX, int baseY, Model model, boolean showCaps)
  {
    for (int l = 0; l < model.levelCount(); ++l)
    {
      Level level = model.levelAt(l);
      Iterator<Piece> pieces = level.iterator();
      
      while (pieces.hasNext())
      {
        Piece piece = pieces.next();

        if (piece.type != PieceType.CAP || showCaps)
        PieceDrawer.drawPiece(gfx, baseX, baseY, piece, piece.x, piece.y, l, level);
      }
    }
  }
  
  public static Rectangle computeBoundsForModel(Model model)
  {
    int x = 0;
    int y = 0;
    
    return new Rectangle(512,384,1024,768);
  }
}
