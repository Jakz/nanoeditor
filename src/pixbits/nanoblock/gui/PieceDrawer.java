package pixbits.nanoblock.gui;

import java.util.Iterator;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

public class PieceDrawer
{
  public static void drawPiece(PApplet gfx, int baseX, int baseY, Piece piece, int x, int y, int l, Level level)
  {    
    int fx = (int)(baseX+Brush.tileset.xOffset*x/2.0f-Brush.tileset.yOffset*y/2.0f);
    int fy = (int)(baseY+Brush.tileset.hOffset*(x/2.0f+y/2.0f-l*2));
    
    //if (piece.type != PieceType.CAP)
      fy += Brush.tileset.hAdjust*l;
    
    Tileset.PieceSpec spec = Brush.tileset.spec(piece.type);
    PImage texture = Brush.tileset.imageForColor(piece.color);
    gfx.fill(0);
    gfx.blend(texture, spec.x, spec.y, spec.w, spec.h, fx+spec.ox, fy+spec.oy, spec.w, spec.h, PApplet.BLEND);
    
    if (Settings.values.get(Setting.SHOW_PIECE_ORDER))
      gfx.text(""+level.indexOfPiece(piece),fx+spec.ox+spec.w/2,fy+spec.oy+spec.h/2);
  }
  
  public static void drawPiece(PImage gfx, int baseX, int baseY, Piece piece, int x, int y, int l, Level level)
  {    
    int fx = (int)(baseX+Brush.tileset.xOffset*x/2.0f-Brush.tileset.yOffset*y/2.0f);
    int fy = (int)(baseY+Brush.tileset.hOffset*(x/2.0f+y/2.0f-l*2));
    
    //if (piece.type != PieceType.CAP)
      fy += Brush.tileset.hAdjust*l;
    
    Tileset.PieceSpec spec = Brush.tileset.spec(piece.type);
    PImage texture = Brush.tileset.imageForColor(piece.color);
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
}
