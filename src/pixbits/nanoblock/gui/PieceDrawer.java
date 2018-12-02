package pixbits.nanoblock.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

import pixbits.nanoblock.data.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

public class PieceDrawer
{  
  public static Point isometricPositionForCoordinate(int x, int y, int l)
  {
    Tileset ts = Brush.tileset;

    int fx = (int) ((x - y)/2.0f * ts.xOffset);
    int fy = (int) ((x + y)/2.0f * ts.yOffset);
        
    fy -= ts.hOffset * l;

    return new Point(fx, fy);
  }

  public static void drawPiece(PGfx gfx, int baseX, int baseY, PieceType type, PieceColor color, int x, int y, int l)
  {    
    Tileset ts = Brush.tileset;
    Tileset.PieceSpec spec = ts.spec(type);
    Point p = isometricPositionForCoordinate(x, y, l);
    p.translate(baseX, baseY);
    
    PImage texture = ts.imageForTypeAndColor(type, color);
    gfx.blend(texture, 0, 0, spec.w, spec.h, p.x+spec.ox, p.y+spec.oy+ts.yOffset, spec.w, spec.h, PApplet.BLEND);
    
    //TODO: move, no sense here and it's called when drawing on image from drawModelOnImage which is wrong
    //if (Settings.values.get(Setting.SHOW_PIECE_ORDER))
    //  gfx.text(""+level.indexOfPiece(piece), p.x+spec.w/2, p.y+spec.oy+spec.h/2);
  }
  
  public static void drawPiece(PGfx gfx, int baseX, int baseY, Piece piece, int l, Level level)
  {    
    drawPiece(gfx, baseX, baseY, piece.type, piece.color, piece.x, piece.y, l);
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
        PieceDrawer.drawPiece(gfx, baseX, baseY, piece.type, piece.color, piece.x, piece.y, l);
      }
    }
  }
  
  
  
  public static Rectangle computeBoundsForModel(Model model)
  {
    int x = 0;
    int y = 0;
    
    return new Rectangle(512,384,1024,768);
  }
  
  public static Rectangle computeLayerBounds(Model model, int l)
  {
    /* total width and height is offset*2 but we need to offset x by half width */
    int sx = -model.getWidth()*Brush.tileset.xOffset, sw = model.getWidth()*Brush.tileset.xOffset*2;
    int sh = model.getHeight()*Brush.tileset.yOffset*2;

    Point base = isometricPositionForCoordinate(0,0,l);
    
    int sy = base.y - 2;
    
    return new Rectangle(sx+base.x,sy,sw,sh);
  }
  
  public static Rectangle computeLayerBoundsWithPiece(Model model, int l)
  {    
    Rectangle bounds = computeLayerBounds(model, l);
    
    /* add spacing for a layer to take piece into account */
    return new Rectangle(bounds.x, bounds.y - Brush.tileset.hOffset, bounds.width, bounds.height + Brush.tileset.hOffset);
  }
  
  public static Rectangle computeRealBounds(Model model, boolean withCaps)
  {
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
    boolean found = false;

    for (int i = 0; i < model.levelCount(); ++i)
    {
      Rectangle lbounds = computeRealBounds(model, i, withCaps);
      
      if (lbounds == null)
        continue;
            
      found = true;
      
      if (lbounds.x < minX) minX = lbounds.x;
      if (lbounds.y < minY) minY = lbounds.y;
      if (maxX < lbounds.x + lbounds.width) maxX = lbounds.x + lbounds.width;
      if (maxY < lbounds.y + lbounds.height) maxY = lbounds.y + lbounds.height;
    }
        
    return found ? new Rectangle(minX, minY, maxX-minX, maxY-minY) : null;
  }
  
  public static Rectangle computeRealBounds(Model model, int l, boolean withCaps)
  {
    Piece leftMost = null, rightMost = null, topMost = null, bottomMost = null;
    Level level = model.levelAt(l);
    int w = model.getWidth()*2, h = model.getHeight()*2;
    
    if (level.count() == 0) return null;
    
    for (Piece p : level)
    {
      if (!withCaps && p.type == PieceType.CAP)
        continue;
      
      if (topMost == null || p.x + p.y < topMost.x + topMost.y) topMost = p;
      if (bottomMost == null || p.x + (p.type.width-1)*2 + p.y + (p.type.height-1)*2 > bottomMost.x + (bottomMost.type.width-1)*2 + bottomMost.y + (bottomMost.type.height-1)*2) bottomMost = p;
      if (leftMost == null || p.x + (h - (p.y+(p.type.height-1)*2)) < leftMost.x + (h - (leftMost.y+(leftMost.type.height-1)*2))) leftMost = p;
      if (rightMost == null || (w - (p.x+(p.type.width-1)*2)) + p.y < (w - (rightMost.x+(rightMost.type.width-1)*2)) + rightMost.y) rightMost = p;
    }
    
    if (topMost == null || bottomMost == null || leftMost == null || rightMost == null)
      return null;
    
    Tileset ts = Brush.tileset;

    
    /*System.out.println("bounds");
    System.out.println("left: "+leftMost);
    System.out.println("right: "+rightMost);
    System.out.println("top: "+topMost);
    System.out.println("bottom: "+bottomMost);*/
    
    Point left = isometricPositionForCoordinate(leftMost.x, leftMost.y, l);
    left.x -= ts.xOffset*(leftMost.type.height-1);
    
    Point right = isometricPositionForCoordinate(rightMost.x, rightMost.y, l);
    right.x += ts.xOffset*(rightMost.type.width-1);
    
    Point top = isometricPositionForCoordinate(topMost.x, topMost.y, l);
    
    Point bottom = isometricPositionForCoordinate(bottomMost.x, bottomMost.y, l);
    bottom.y += ts.yOffset*(bottomMost.type.height-1 + bottomMost.type.width-1);

    return new Rectangle(
        left.x - ts.xOffset,
        top.y + Brush.tileset.yOffset - ts.yOffset*3,
        right.x-left.x + ts.xOffset*2,
        bottom.y-top.y + ts.yOffset*4
    );
  }
  
}
