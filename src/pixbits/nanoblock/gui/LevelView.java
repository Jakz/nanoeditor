package pixbits.nanoblock.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;

import com.pixbits.lib.ui.color.Color;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.menus.Item;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.ModelOperations;
import processing.core.*;

public class LevelView extends Node
{  
  private final float cellSize;
  private Level level;
  private int index;
  private final Model model;
  
  public int hx = -1, hy = -1;
  public int rhx = -1, rhy = -1;
  
  Piece wouldBeRemovedPiece;

  
  LevelView(Sketch p, Model model, Level level, int index, int ox, int oy, float cellSize)
  {
    super(p, ox, oy);
    this.cellSize = cellSize;
    this.level = level;
    this.model = model;
    this.index = index;
    
    this.wouldBeRemovedPiece = null;
  }
  
  public boolean isInside(int x, int y)
  {
    int rx = x - this.x;
    int ry = y - this.y;
    
    return rx >= 0 && rx < model.getWidth()*cellSize && ry >= 0 && ry < model.getHeight()*cellSize;
  }
  
  public void mouseMoved(int x, int y)
  {
    final PieceType type = Brush.type();
    
    final float fx = x - this.x;
    final float fy = y - this.y;
    
    final float quarterSize = cellSize / 4.0f;
    final float halfSize = cellSize / 2.0f;
     
    boolean halfSteps = Settings.values.get(Setting.HALF_STEPS_ENABLED);

    final int pieceWidth = type.maxWidth();
    final int pieceHeight = type.maxHeight();
        
    final int tx = (int)(fx / quarterSize), ty = (int)(fy / quarterSize);
    final int rx = tx/2, ry = ty/2;
    int vx = 0, vy = 0;
    
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
      
      vx -= pieceWidth;
      vy -= pieceHeight;
    }
    else
    {
      if (pieceWidth % 2 == 1)
        vx = tx / 4 - pieceWidth/2;
      else
      {
        if (tx % 4 >= 2)
          vx = (tx + (4 - tx%4)) / 4 - pieceWidth/2;
        else
          vx = tx / 4 - pieceWidth/2;
      }
      
      if (pieceHeight % 2 == 1)
        vy = ty / 4 - pieceHeight/2;
      else
      {
        if (ty % 4 >= 2)
          vy = (ty + (4 - ty%4)) / 4 - pieceHeight/2;
 
        else
          vy = ty / 4 - pieceHeight/2;
      }

      vx *= 2;
      vy *= 2;
    }
    
    //System.out.printf("t: %d,%d, v: %d,%d, r: %d,%d\n", tx, ty, vx, vy, rx, ry);
    
    /* if base is < 0 adjust to 0 */
    vx = Math.max(vx, 0);
    vy = Math.max(vy, 0);
    
    /* if base would place piece outside adjust it to max possible value */  
    final int maxAllowedX = model.getWidth()*2 - pieceWidth*2;
    final int maxAllowedY = model.getHeight()*2 - pieceHeight*2;
    
    vx = Math.min(vx, maxAllowedX);
    vy = Math.min(vy, maxAllowedY);

    if (Settings.values.get(Setting.VIEW_MARK_DELETED_PIECE_ON_LAYER))
    {
      Piece dpiece = level.pieceAt(rx, ry);
      if (dpiece != null && dpiece.type != PieceType.CAP)
        wouldBeRemovedPiece = dpiece;
      else
        wouldBeRemovedPiece = null;
    }
    
    PieceHover hover = new PieceHover(hx, hy, type.outline());
    
    LevelStackView parent = this.parent();
    
    //TODO: verify if correct equals is called
    if (vx != hx || vy != hy || parent.getHoveredLevel() != level || !parent.hover().equals(hover))
    {
      hx = vx;
      hy = vy;
      rhx = rx;
      rhy = ry;
      parent.setHover(hover);
      parent.setHoveredLevel(level);

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
      
      LevelStackView parent = this.parent();
      parent.setHover(null);
      parent.setHoveredLevel(null);
      wouldBeRemovedPiece = null;
    }
    
    Main.sketch.redraw();
  }
  
  public void mouseDragged(int x, int y, int b) { }
  
  public void mouseReleased(int x, int y, int b)
  {
    LevelStackView parent = this.parent();

    if (b == PApplet.LEFT)
    {
      if (parent.hover() != null)
      {   
        if (!level.isFreeAt(rhx, rhy))
        {
          new ModelOperations.Remove(model, index, rhx, rhy).execute();
          wouldBeRemovedPiece = null;
        }
        else if (level.canPlace(Brush.type(), hx, hy))
          new ModelOperations.Place(model, index, Brush.type(), Brush.color, hx, hy).execute();

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
  
  public void mouseWheelMoved(int x, int y, int v)
  { 
    LevelStackView parent = this.parent();
    parent.scrollbar().mouseWheelMoved(x, y, v);
  }
    
  public void draw()
  {
    p.strokeWeight(1.0f);
    p.stroke(0);

    /* draw point grid */
    if (Settings.values.get(Setting.VIEW_SHOW_HALF_GRID_POINTS))
    {
      for (int x = 0; x < model.getWidth()*2+1; ++x)
        for (int y = 0; y < model.getHeight()*2+1; ++y)
        {
          p.point(this.x+cellSize/2*x, this.y+cellSize/2*y);
        }
    }
      
    /* draw vertical line grid */
    if (Settings.values.get(Setting.VIEW_SHOW_GRID_LINES))
    {
      for (int x = 0; x < model.getWidth()+1; ++x)
      {
        if (x == model.getWidth()/2)
          p.strokeWeight(2.0f);
        else
          p.strokeWeight(1.0f);
        
        p.line(this.x+cellSize*x, this.y, this.x+cellSize*x, this.y+model.getHeight()*cellSize);
        
      }
    }
    else
    {
      int x = model.getWidth()/2;
      p.line(this.x+cellSize*x, this.y, this.x+cellSize*x, this.y+model.getHeight()*cellSize);
    }

    /* draw horizontal line grid */
    if (Settings.values.get(Setting.VIEW_SHOW_GRID_LINES))
    {
      for (int y = 0; y < model.getHeight()+1; ++y)
      {
        if (y == model.getHeight()/2)
          p.strokeWeight(2.0f);
        else
          p.strokeWeight(1.0f);
        
        
        p.line(this.x, this.y+cellSize*y, this.x+model.getWidth()*cellSize, this.y+cellSize*y);
    }
    }
    else
    {
      int y = model.getHeight()/2;
      p.line(this.x, this.y+cellSize*y, this.x+model.getWidth()*cellSize, this.y+cellSize*y);
    }
    
    
    LevelStackView parent = parent();
    Level locked = parent.getLocked();
    
    /* draw border of locked level */
    if (locked == level)
    {
      p.strokeWeight(3.0f);
      p.stroke(220,0,0);
      p.noFill();
      p.rect(this.x, this.y+cellSize*0, cellSize*model.getWidth(), model.getHeight()*cellSize);
    }
    
    /* draw pieces */
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
          Color f = piece.color.fillColor, s = piece.color.strokeColor;
          
          p.fill(f.withAlpha(100));
          p.stroke(s.withAlpha(150));
          
          drawOutline(piece.x, piece.y, piece.type.outline());  
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
      
      drawOutline(piece.x, piece.y, piece.type.outline());
    }
    
    
    PieceHover h = parent.hover();
    if (wouldBeRemovedPiece == null && h != null)
    {
      p.noFill();
      p.strokeWeight(2.0f);
      p.stroke(220,0,0);
      drawOutline(h.x, h.y, h.outline);
    }
    else if (wouldBeRemovedPiece != null)
    {
      p.noFill();
      p.strokeWeight(2.0f);
      p.stroke(220,0,0);
      drawOutline(wouldBeRemovedPiece.x, wouldBeRemovedPiece.y, wouldBeRemovedPiece.type.outline());

      //TODO: we had this but can't use anymore
      /*
      p.line(ox+rx*realCellSize+1, oy+ry*realCellSize+1, ox+rx*realCellSize+1 + rw*cellSize-1, oy+ry*realCellSize+1 + rh*cellSize-1);
      p.line(ox+rx*realCellSize+1 + rw*cellSize-1, oy+ry*realCellSize+1, ox+rx*realCellSize+1, oy+ry*realCellSize+1 + rh*cellSize-1);
       */
    }
    
    p.fill(0);
    p.textFont(Main.sketch.font);
    p.text(""+index, x + 5, y + cellSize*model.getHeight()+15);
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
  
  public void drawOutline(int x, int y, PieceOutline outline)
  {    
    outline.render(p, x, y, this.x, this.y, (int)(cellSize / 2.0f));
  }
}
