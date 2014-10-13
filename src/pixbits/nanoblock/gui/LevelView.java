package pixbits.nanoblock.gui;

import java.awt.Rectangle;
import java.util.Iterator;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
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
    
    float realCellSize = halfSteps ? cellSize/2.0f : cellSize;
    
    x /= realCellSize;
    y /= realCellSize;
    
    float realWidth = halfSteps ? model.getWidth()*2.0f : model.getWidth();
    float realHeight = halfSteps ? model.getHeight()*2.0f : model.getHeight();
    
    float realPieceWidth = halfSteps ? Brush.type.width*2.0f : Brush.type.width;
    float realPieceHeight = halfSteps ? Brush.type.height*2.0f : Brush.type.height;
    
    if (x != hx || y != hy)
    {
      if (x + realPieceWidth <= realWidth && y + realPieceHeight <= realHeight)
      {
        hx = x;
        hy = y;
        parent.setHover(new Rectangle(hx, hy, Brush.type.width, Brush.type.height));
      }
      else
      {
        parent.setHover(null);
        hx = -1;
        hy = -1;
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
      parent.setHover(null);
    }
    
    Main.sketch.redraw();
  }
  
  public void mouseDragged(int x, int y) { }
  
  public void mouseReleased(int x, int y)
  {
    if (parent.hover() != null)
    {   
      x -= ox;
      y -= oy;
      
      float realCellSize = Settings.values.get(Setting.HALF_STEPS_ENABLED) ? cellSize/2.0f : cellSize;
      
      x /= realCellSize;
      y /= realCellSize;
      Piece piece = level.pieceAt(x,y);
      
  
      if (level.canPlace(Brush.type, x, y))
        model.addPiece(level,Brush.type,Brush.color,x,y);
      else if (!level.isFreeAt(x,y))
        model.removePiece(level, piece);
      
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

    // draw grid
    for (int x = 0; x < model.getWidth()*2+1; ++x)
      for (int y = 0; y < model.getHeight()*2+1; ++y)
      {
        if (x%2 != 0 && y%2 != 0)
          p.stroke(50.0f);
        else
          p.stroke(0.0f);
          
        p.point(ox+cellSize/2*x, oy+cellSize/2*y);
      }
      

    for (int x = 0; x < model.getWidth()+1; ++x)
    {
      if (x == model.getWidth()/2)
        p.strokeWeight(2.0f);
      else
        p.strokeWeight(1.0f);
      
      p.line(ox+cellSize*x, oy, ox+cellSize*x, oy+model.getHeight()*cellSize);
      
    }
    
    for (int y = 0; y < model.getHeight()+1; ++y)
    {
      if (y == model.getHeight()/2)
        p.strokeWeight(2.0f);
      else
        p.strokeWeight(1.0f);
      
      
      p.line(ox, oy+cellSize*y, ox+model.getWidth()*cellSize, oy+cellSize*y);
    }
    
    // draw pieces
    p.noStroke();
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
    
    int order = 0;
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
        
        p.fill(f.getRed(),f.getGreen(),f.getBlue(),128);
        p.stroke(s.getRed(),s.getGreen(),s.getBlue(),128);

      }
        
      p.rect(ox+piece.x*cellSize/2+1, oy+piece.y*cellSize/2+1, piece.type.width*cellSize-2, piece.type.height*cellSize-2);
      
      //p.fill(0);
      //p.text(""+order++, ox+piece.x*cellSize+1, oy+(piece.y+1)*cellSize+1);
    }
    
    Rectangle h = parent.hover();
    if (h != null)
    {
      p.noFill();
      p.strokeWeight(2.0f);
      p.stroke(220,0,0);
      
      float realCellSize = Settings.values.get(Setting.HALF_STEPS_ENABLED) ? cellSize/2.0f : cellSize;

      
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
