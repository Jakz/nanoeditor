package pixbits.nanoblock.gui;

import java.util.Iterator;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;

import processing.core.*;

public class LevelView extends Drawable
{
  private final int width, height;
  private final float cellSize;
  private final Level level;
  private final Model model;
  
  public int hx = -1, hy = -1;

  
  LevelView(Model model, Level level, int ox, int oy, float cellSize)
  {
    super(ox, oy);
    this.width = model.width;
    this.height = model.height;
    this.cellSize = cellSize;
    this.level = level;
    this.model = model;
  }
  
  public boolean isInside(int x, int y)
  {
    int rx = x - ox;
    int ry = y - oy;
    
    return rx >= 0 && rx < width*cellSize && ry >= 0 && ry < height*cellSize;
  }
  
  public void mouseMoved(int x, int y)
  {
    x -= ox;
    y -= oy;
    
    x /= cellSize;
    y /= cellSize;
    
    if (x != hx || y != hy)
    {
      if (x + Brush.type.width <= width && y + Brush.type.height <= height)
      {
        hx = x;
        hy = y;
      }
      else
      {
        hx = -1;
        hy = -1;
      }
      Main.sketch.redraw();
    }
  }
  
  public void mouseExited()
  {
    hx = -1;
    hy = -1;
    Main.sketch.redraw();
  }
  
  public void mouseClicked(int x, int y)
  {
    x -= ox;
    y -= oy;
    
    x /= cellSize;
    y /= cellSize;
    
    Piece piece = level.pieceAt(x,y);

    if (level.canPlace(Brush.type, x, y))
      model.addPiece(level,Brush.type,Brush.color,x,y);
    else if (!level.isFreeAt(x,y))
      model.removePiece(level, piece);
    
    Main.sketch.redraw();
  }
  
  public void draw(Sketch p)
  {
    p.strokeWeight(1.0f);

    // draw grid
    for (int x = 0; x < width*2+1; ++x)
      for (int y = 0; y < height*2+1; ++y)
      {
        if (x%2 != 0 && y%2 != 0)
          p.stroke(50.0f);
        else
          p.stroke(0.0f);
          
        p.point(ox+cellSize/2*x, oy+cellSize/2*y);
      }
      

    for (int x = 0; x < width+1; ++x)
    {
      if (x == width/2)
        p.strokeWeight(2.0f);
      else
        p.strokeWeight(1.0f);
      
      p.line(ox+cellSize*x, oy, ox+cellSize*x, oy+height*cellSize);
      
    }
    
    for (int y = 0; y < height+1; ++y)
    {
      if (y == height/2)
        p.strokeWeight(2.0f);
      else
        p.strokeWeight(1.0f);
      
      
      p.line(ox, oy+cellSize*y, ox+width*cellSize, oy+cellSize*y);
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
    
    //int order = 0;
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
        
      p.rect(ox+piece.x*cellSize+1, oy+piece.y*cellSize+1, piece.type.width*cellSize-2, piece.type.height*cellSize-2);
      
      //p.fill(0);
      //p.text(""+order, ox+piece.x*cellSize+1, oy+(piece.y+1)*cellSize+1);
      
      //++order;
    }
    
    if (hx != -1)
    {
      p.noFill();
      p.strokeWeight(2.0f);
      p.stroke(220,0,0);
      p.rect(ox+hx*cellSize+1, oy+hy*cellSize+1, Brush.type.width*cellSize-2, Brush.type.height*cellSize-2);
    }
  }
}
