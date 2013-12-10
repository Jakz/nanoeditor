package pixbits.nanoblock.gui;

import pixbits.nanoblock.data.*;

import processing.core.*;

public class LevelView extends Drawable
{
  private final int width, height;
  private final int cellSize;
  private final Level level;
  
  LevelView(Level level, int ox, int oy, int width, int height, int cellSize)
  {
    super(ox, oy);
    this.width = width;
    this.height = height;
    this.cellSize = cellSize;
    this.level = level;
  }
  
  public boolean isInside(int x, int y)
  {
    int rx = x - ox;
    int ry = y - oy;
    
    return rx >= 0 && rx < width*cellSize && ry >= 0 && ry < height*cellSize;
  }
  
  public void mouseClicked(int x, int y)
  {
    x -= ox;
    y -= oy;
    
    x /= cellSize;
    y /= cellSize;
    
    Piece piece = level.pieceAt(x,y);
    
    if (piece == null)
      level.addPiece(new Piece(PieceType.P1x1,x,y));
    else
      level.removePiece(piece);
  }
  
  public void draw(PApplet p)
  {
    p.noStroke();
    p.fill(255.0f,0f,0f);
    for (int x = 0; x < width; ++x)
      for (int y = 0; y < height; ++y)
      {
        if (level.pieceAt(x, y) != null)
          p.rect(ox+x*cellSize, oy+y*cellSize, cellSize, cellSize);
      }

    p.stroke(0.0f);

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
    
   

    
  }
}
