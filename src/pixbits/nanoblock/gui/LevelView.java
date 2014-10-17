package pixbits.nanoblock.gui;

import java.awt.Rectangle;
import java.util.Iterator;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.files.Log;
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

  
  LevelView(LevelStackView parent, Sketch p, Model model, Level level, int index, int ox, int oy, float cellSize)
  {
    super(p, ox, oy);
    this.cellSize = cellSize;
    this.level = level;
    this.model = model;
    this.parent = parent;
    this.index = index;
  }
  
  public boolean isInside(int x, int y)
  {
    int rx = x - ox;
    int ry = y - oy;
    
    return rx >= 0 && rx < model.getWidth()*cellSize && ry >= 0 && ry < model.getHeight()*cellSize;
  }
  
  public void mouseMoved(int x, int y)
  {
    x -= ox;
    y -= oy;
    
    boolean halfSteps = Settings.values.get(Setting.HALF_STEPS_ENABLED);
    
    float realCellSize = cellSize/2.0f;
    
    x = Math.round((x - realCellSize/2)/realCellSize);
    y = Math.round((y - realCellSize/2)/realCellSize);
    
    float realWidth =  model.getWidth()*2.0f;
    float realHeight =  model.getHeight()*2.0f;
    
    float realPieceWidth =  Brush.type.width*2.0f;
    float realPieceHeight =  Brush.type.height*2.0f;
    
    int rx = x;
    int ry = y;
    
    x -= Math.floor(realPieceWidth / 2.0f);
    y -= Math.floor(realPieceHeight / 2.0f);

    /*x = Math.max(0, x);
    y = Math.max(0, y);
    
    x = Math.min(x, halfSteps ? model.getWidth()*2)*/
    
    if (x < 0) x = 0;
    else if (x > realWidth - realPieceWidth)
      x = (int)(realWidth - realPieceWidth);
    
    if (y < 0) y = 0;
    else if (y > realHeight - realPieceHeight)
      y = (int)(realHeight - realPieceHeight);
    
    
    if (!halfSteps)
    {
      if (x % 2 != 0)
        --x;
      if (y % 2 != 0)
        --y;
    }
    
    if (x != hx || y != hy)
    {
      if (x + realPieceWidth <= realWidth && y + realPieceHeight <= realHeight)
      {
        hx = x;
        hy = y;
        rhx = rx;
        rhy = ry;
        parent.setHover(new Rectangle(hx, hy, Brush.type.width, Brush.type.height));
      }
      else
      {
        parent.setHover(null);
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
          model.removePiece(level, piece);
        else if (level.canPlace(Brush.type, hx, hy))
          model.addPiece(level,Brush.type,Brush.color,hx,hy);
        
        Main.sketch.redraw();
      }
    }
    else if (b == PApplet.RIGHT)
    {
      Level locked = parent.getLocked();
      
      if (locked == null || locked != level)
        parent.setLocked(level);
      else if (locked == level)
        parent.setLocked(null);
      
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
    
    /*evel prev = level.previous();
    
    if (prev != null)
    {
      Iterator<Piece> pieces = prev.iterator();
      while (pieces.hasNext())
      {
        Piece piece = pieces.next();
        
        if (piece.type != PieceType.CAP)
        {
          p.fill(piece.color.fillColor);
          p.stroke(piece.color.strokeColor);
          
          p.rect(ox+piece.x*cellSize+1, oy+piece.y*cellSize+1, piece.type.width*cellSize-2, piece.type.height*cellSize-2);
          
        }
      }
    }*/
    
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
        java.awt.Color f = piece.color.fillColor, s = piece.color.strokeColor;
        
        p.fill(f.getRed(),f.getGreen(),f.getBlue(),80);
        p.stroke(s.getRed(),s.getGreen(),s.getBlue(),80);

      }
        
      p.rect(ox+piece.x*cellSize/2+2, oy+piece.y*cellSize/2+2, piece.type.width*cellSize-3, piece.type.height*cellSize-3);
      
      //p.fill(0);
      //p.text(""+order++, ox+piece.x*cellSize+1, oy+(piece.y+1)*cellSize+1);
    }
    
    Rectangle h = parent.hover();
    if (h != null)
    {
      p.noFill();
      p.strokeWeight(2.0f);
      p.stroke(220,0,0);
      
      float realCellSize = cellSize/2.0f;

      
      p.rect(ox+h.x*realCellSize+1, oy+h.y*realCellSize+1, h.width*cellSize-1, h.height*cellSize-1);
    }
    
    p.fill(0);
    p.textFont(Main.sketch.font);
    p.text(""+index, ox+5, oy+cellSize*model.getHeight()+15);
  }
  
  void moveToNext()
  {
    level = level.next();
    this.index = model.indexOfLevel(level);
  }
  
  void moveToPrev()
  {
    level = level.previous();
    this.index = model.indexOfLevel(level);

  }
  
  public void moveToLevel(int index)
  {
    level = model.levelAt(index);
    this.index = model.indexOfLevel(level);

  }
  
  Level level() { return level; }
}
