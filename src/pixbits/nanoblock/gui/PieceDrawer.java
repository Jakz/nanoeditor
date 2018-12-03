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
    SpriteBatch batch = new SpriteBatch();

    for (int l = 0; l < model.levelCount(); ++l)
    {
      Level level = model.levelAt(l);
      
      batch.clear();
      
      for (Piece piece : level)
      {
        if (showCaps || piece.type != PieceType.CAP)
          generateSprites(piece, batch);
      }
      
      batch.setPosition(baseX, baseY - l*Brush.tileset.hOffset);
      batch.draw(gfx);
      batch.sortIfNeeded();
    }
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
  
  
  final static Rectangle rectCap = new Rectangle(1, 121, 44, 27);
  
  public static void generateSprites(Piece piece, SpriteBatch batch) { generateSprites(piece, batch, 0); }
  public static void generateSprites(Piece piece, SpriteBatch batch, int l)
  {
    Tileset ts = Brush.tileset;
    PImage texture = ts.imageForTypeAndColor(piece.type, piece.color);


      if (piece.type == PieceType.CAP)
      {
        Point position = PieceDrawer.isometricPositionForCoordinate(piece.x, piece.y, l);
      
        batch.add(new Sprite(
            new Sprite.Key(piece, piece.x, piece.y, l, Sprite.Type.TOP), 
            texture,
            new Point(position.x - ts.xOffset, position.y - 6),
            rectCap
            )
        );
      }
    /*  else if (piece.type == PieceType.P1x1r)
      {
        Point position = PieceDrawer.isometricPositionForCoordinate(piece.x, piece.y, l);

        batch.add(new Sprite(
            new Sprite.Key(piece, piece.x, piece.y, l, Sprite.Type.TOP), 
            new Point(position.x - ts.xOffset, position.y - ts.yOffset*2),
            rect1x1r
            )
        );
      }*/
      else
      {
        {
          Atlas atlas = new Atlas(1, 149, 45, 24, 8);

          for (int yy = 0; yy < piece.type.height; ++yy)
            for (int xx = 0; xx < piece.type.width; ++xx)
            {
              final int isoX = piece.x + xx*2;
              final int isoY = piece.y + yy*2; 
              Point position = PieceDrawer.isometricPositionForCoordinate(isoX, isoY, l);
                   
              int mask = piece.type.mask(xx, yy);    
              batch.add(new Sprite(
                  new Sprite.Key(piece, isoX, isoY, l, Sprite.Type.TOP), 
                  texture,
                  new Point(position.x - ts.xOffset, position.y - ts.yOffset*2),
                  atlas.get(mask)
                  )
              );
            }
        }
        
        {
          Atlas atlas = new Atlas(1, 197 + 48,  22, 32,  45, 33);
          for (int xx = 0; xx < piece.type.width; ++xx)
          {
            final int isoX = piece.x + xx*2;
            final int isoY = piece.y + (piece.type.height - 1)*2; 
            
            Point position = PieceDrawer.isometricPositionForCoordinate(isoX, isoY + 2, l); //TODO: +2 is an hack to adjust the value directly as coordinate
            int mask = piece.type.maskSouth(xx, piece.type.height - 1); 
            
            batch.add(new Sprite(
                new Sprite.Key(piece, isoX, isoY, l, Sprite.Type.WALL), 
                texture,
                new Point(position.x, position.y - ts.yOffset*2),
                atlas.get(mask)
                )
            );
          }
        }

        {
          Atlas atlas = new Atlas(1, 197 + 48,  45, 32,  45, 33);
          for (int yy = 0; yy < piece.type.height; ++yy)
          {
            final int isoX = piece.x + (piece.type.width - 1)*2;
            final int isoY = piece.y + yy*2;
            
            Point position = PieceDrawer.isometricPositionForCoordinate(isoX, isoY, l);
            int mask = piece.type.maskEast(piece.type.width - 1, yy);   

            batch.add(new Sprite(
                new Sprite.Key(piece, isoX, isoY, l, Sprite.Type.WALL), 
                texture,
                new java.awt.Point(position.x - ts.xOffset, position.y - ts.yOffset*1),
                atlas.get(mask)
                )
            );
          }
        }
      }
      
      /*piece.type.forEachCap((xx, yy) -> {
        final int isoX = piece.x + xx;
        final int isoY = piece.y + yy; 
        java.awt.Point position = PieceDrawer.isometricPositionForCoordinate(isoX, isoY, l+1);
      
        sprites.add(new Sprite(
            new Sprite.Key(isoX, isoY, l+1, Sprite.Type.TOP), 
            new java.awt.Point(getWidth()/2 + position.x - ts.xOffset, getHeight()/2 + position.y - ts.yOffset + 4),
            new Rectangle(1, 121, 44, 27)
            )
        );
      });*/
  } 
}
