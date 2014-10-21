package pixbits.nanoblock.gui;

import java.awt.Rectangle;
import java.util.Iterator;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.gui.menus.Item;
import pixbits.nanoblock.gui.menus.Menus;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import processing.core.*;

public class LevelView extends Drawable
{
  private final LevelStackView parent;
  
  //private final int width, height;
  private final float cellSize;
  private Level level;
  private int index;
  private final Model model;
  
  public int hx = -1, hy = -1;
  public int rhx = -1, rhy = -1;
  
  Piece wouldBeRemovedPiece;

  
  LevelView(LevelStackView parent, Sketch p, Model model, Level level, int index, int ox, int oy, float cellSize)
  {
    super(p, ox, oy);
    this.cellSize = cellSize;
    this.level = level;
    this.model = model;
    this.parent = parent;
    this.index = index;
    
    this.wouldBeRemovedPiece = null;
  }
  
  public boolean isInside(int x, int y)
  {
    int rx = x - ox;
    int ry = y - oy;
    
    return rx >= 0 && rx < model.getWidth()*cellSize && ry >= 0 && ry < model.getHeight()*cellSize;
  }
  
  public void mouseMoved(int x, int y)
  {
    float fx = x - ox;
    float fy = y - oy;
    
    float stepSize = cellSize / 4.0f;
    
    float cx = fx / stepSize;
    float cy = fy / stepSize;
    
    int rx = 0, ry = 0;
    int vx = 0, vy = 0;
    
    boolean halfSteps = Settings.values.get(Setting.HALF_STEPS_ENABLED);
    
    rx = Math.round((fx - cellSize/4.0f)/(cellSize/2.0f));
    ry = Math.round((fy - cellSize/4.0f)/(cellSize/2.0f));
    
    int bwidth = Brush.type().width;
    int bheight = Brush.type().height;
        
    int tx = (int)cx, ty = (int)cy;
    
    if (halfSteps)
    {
      vx = rx;
      vy = ry;
      
      if (tx % 2 == 1)
        vx = (tx + 1) / 2;
      else
        vx = tx / 2;
      
      if (ty % 2 == 1)
        vy = (ty + 1) / 2;
      else
        vy = ty / 2; 
      
      vx -= bwidth;
      vy -= bheight;
    }
    else
    {
      if (bwidth % 2 == 1)
        vx = tx / 4 - bwidth/2;
      else
      {
        if (tx % 4 >= 2)
          vx = (tx + (4 - tx%4)) / 4 - bwidth/2;
        else
          vx = tx / 4 - bwidth/2;
      }
      
      if (bheight % 2 == 1)
        vy = ty / 4 - bheight/2;
      else
      {
        if (ty % 4 >= 2)
          vy = (ty + (4 - ty%4)) / 4 - bheight/2;
 
        else
          vy = ty / 4 - bheight/2;
      }

      vx *= 2;
      vy *= 2;
    }
    
    if (vx < 0) vx = 0;
    else if (vx > model.getWidth()*2 - Brush.type().width*2) vx = model.getWidth()*2 - Brush.type().width*2;
    
    if (vy < 0) vy = 0;
    else if (vy > model.getHeight()*2 - Brush.type().height*2) vy = model.getHeight()*2 - Brush.type().height*2;

    
    if (Settings.values.get(Setting.VIEW_MARK_DELETED_PIECE_ON_LAYER))
    {
      Piece dpiece = level.pieceAt(rx, ry);
      if (dpiece != null && dpiece.type != PieceType.CAP)
        wouldBeRemovedPiece = dpiece;
      else
        wouldBeRemovedPiece = null;
    }
    
    if (vx != hx || vy != hy || parent.getHoveredLevel() != level || (parent.hover() != null && (parent.hover().width != Brush.type().width || parent.hover().height != Brush.type().height)))
    {
      if (vx + Brush.type().width <= model.getWidth()*2 && vy + Brush.type().height <= model.getHeight()*2)
      {
        hx = vx;
        hy = vy;
        rhx = rx;
        rhy = ry;
        parent.setHover(new Rectangle(hx, hy, Brush.type().width, Brush.type().height));
        parent.setHoveredLevel(level);
      }
      else
      {
        parent.setHover(null);
        parent.setHoveredLevel(null);

        hx = -1;
        hy = -1;
        rhx = -1;
        rhy = -1;
      }

      
      Main.sketch.redraw();
    }
  }
  
  public void mouseExited()
  {
    if (hx != -1 || hy != -1)
    {
      hx = -1;
      hy = -1;
      rhx = -1;
      rhy = -1;
      parent.setHover(null);
      parent.setHoveredLevel(null);
      wouldBeRemovedPiece = null;
    }
    
    Main.sketch.redraw();
  }
  
  public void mouseDragged(int x, int y) { }
  
  public void mouseReleased(int x, int y, int b)
  {
    if (b == PApplet.LEFT)
    {
      if (parent.hover() != null)
      {   
        Piece piece = level.pieceAt(rhx,rhy);
    
        if (!level.isFreeAt(rhx, rhy))
        {
          model.removePiece(level, piece);
          wouldBeRemovedPiece = null;
        }
        else if (level.canPlace(Brush.type(), hx, hy))
          model.addPiece(level,Brush.type(),Brush.color,hx,hy);
        
        Main.sketch.redraw();
      }
    }
    else if (b == PApplet.RIGHT)
    {
      Level locked = parent.getLocked();
      
      if (locked == null || locked != level)
      {
        if (locked == null)
          Item.setLevelOperationsEnabled(true);
        
        parent.setLocked(level);
      }
      else if (locked == level)
      {
        parent.setLocked(null);
        Item.setLevelOperationsEnabled(false);
      }
      
      Main.sketch.redraw();
    }
  }
  
  public void mouseWheelMoved(int x)
  { 
    parent.scrollbar.mouseWheelMoved(x);
  }
  
  public void draw()
  {
    p.strokeWeight(1.0f);
    p.stroke(0);

    // draw grid
    if (Settings.values.get(Setting.VIEW_SHOW_HALF_GRID_POINTS))
    {
      for (int x = 0; x < model.getWidth()*2+1; ++x)
        for (int y = 0; y < model.getHeight()*2+1; ++y)
        {
          p.point(ox+cellSize/2*x, oy+cellSize/2*y);
        }
    }
      
    if (Settings.values.get(Setting.VIEW_SHOW_GRID_LINES))
    {
      for (int x = 0; x < model.getWidth()+1; ++x)
      {
        if (x == model.getWidth()/2)
          p.strokeWeight(2.0f);
        else
          p.strokeWeight(1.0f);
        
        p.line(ox+cellSize*x, oy, ox+cellSize*x, oy+model.getHeight()*cellSize);
        
      }
    }
    else
    {
      int x = model.getWidth()/2;
      p.line(ox+cellSize*x, oy, ox+cellSize*x, oy+model.getHeight()*cellSize);
    }

    if (Settings.values.get(Setting.VIEW_SHOW_GRID_LINES))
    {
      for (int y = 0; y < model.getHeight()+1; ++y)
      {
        if (y == model.getHeight()/2)
          p.strokeWeight(2.0f);
        else
          p.strokeWeight(1.0f);
        
        
        p.line(ox, oy+cellSize*y, ox+model.getWidth()*cellSize, oy+cellSize*y);
    }
    }
    else
    {
      int y = model.getHeight()/2;
      p.line(ox, oy+cellSize*y, ox+model.getWidth()*cellSize, oy+cellSize*y);
    }
    
    Level locked = parent.getLocked();
    
    if (locked == level)
    {
      p.strokeWeight(3.0f);
      p.stroke(220,0,0);
      p.line(ox, oy+cellSize*0, ox+model.getWidth()*cellSize, oy+cellSize*0);
      p.line(ox, oy+cellSize*model.getHeight(), ox+model.getWidth()*cellSize, oy+cellSize*model.getHeight());
      p.line(ox+cellSize*0, oy, ox+cellSize*0, oy+model.getHeight()*cellSize);
      p.line(ox+cellSize*model.getWidth(), oy, ox+cellSize*model.getWidth(), oy+model.getHeight()*cellSize);
    }
    
    // draw pieces
    p.noStroke();
    p.strokeWeight(2.0f);
    p.fill(255.0f,0f,0f);
    p.rectMode(PApplet.CORNER);
    p.ellipseMode(PApplet.CENTER);
    
    Level prev = level.previous();
    
    if (prev != null)
    {
      Iterator<Piece> pieces = prev.iterator();
      while (pieces.hasNext())
      {
        Piece piece = pieces.next();
        
        if (piece.type != PieceType.CAP)
        {
          java.awt.Color f = piece.color.fillColor, s = piece.color.strokeColor;
          
          p.fill(f.getRed(),f.getGreen(),f.getBlue(),100);
          p.stroke(s.getRed(),s.getGreen(),s.getBlue(),150);
          
          p.rect(ox+piece.x*cellSize/2+2, oy+piece.y*cellSize/2+2, piece.type.width*cellSize-3, piece.type.height*cellSize-3);
          
        }
      }
    }
    
    Iterator<Piece> pieces = level.iterator();
    while (pieces.hasNext())
    {
      Piece piece = pieces.next();
      
      if (piece.type != PieceType.CAP)
      {
        p.fill(piece.color.fillColor);
        p.stroke(piece.color.strokeColor);
      }
      else
      {
        continue;
        
        /*java.awt.Color f = piece.color.fillColor, s = piece.color.strokeColor;
        
        p.fill(f.getRed(),f.getGreen(),f.getBlue(),80);
        p.stroke(s.getRed(),s.getGreen(),s.getBlue(),80);*/
      }
        
      p.rect(ox+piece.x*cellSize/2+2, oy+piece.y*cellSize/2+2, piece.type.width*cellSize-3, piece.type.height*cellSize-3);
    }
    
    
    Rectangle h = parent.hover();
    if (wouldBeRemovedPiece == null && h != null)
    {
      p.noFill();
      p.strokeWeight(2.0f);
      p.stroke(220,0,0);
      
      float realCellSize = cellSize/2.0f;

      
      p.rect(ox+h.x*realCellSize+1, oy+h.y*realCellSize+1, h.width*cellSize-1, h.height*cellSize-1);
    }
    else if (wouldBeRemovedPiece != null)
    {
      int rx = wouldBeRemovedPiece.x;
      int ry = wouldBeRemovedPiece.y;
      int rw = wouldBeRemovedPiece.type.width;
      int rh = wouldBeRemovedPiece.type.height;
      
      p.noFill();
      p.strokeWeight(2.0f);
      p.stroke(220,0,0);

      float realCellSize = cellSize/2.0f;
      p.rect(ox+rx*realCellSize+1, oy+ry*realCellSize+1, rw*cellSize-1, rh*cellSize-1);
      
      p.line(ox+rx*realCellSize+1, oy+ry*realCellSize+1, ox+rx*realCellSize+1 + rw*cellSize-1, oy+ry*realCellSize+1 + rh*cellSize-1);
      p.line(ox+rx*realCellSize+1 + rw*cellSize-1, oy+ry*realCellSize+1, ox+rx*realCellSize+1, oy+ry*realCellSize+1 + rh*cellSize-1);


    }
    
    p.fill(0);
    p.textFont(Main.sketch.font);
    p.text(""+index, ox+5, oy+cellSize*model.getHeight()+15);
  }
  
  /*void moveToNext()
  {
    level = level.next();
    this.index = model.indexOfLevel(level);
  }*/
  
  /*void moveToPrev()
  {
    level = level.previous();
    this.index = model.indexOfLevel(level);

  }*/
  
  public void moveToLevel(int index)
  {
    level = model.levelAt(index);
    this.index = model.indexOfLevel(level);
  }
  
  Level level() { return level; }
}
