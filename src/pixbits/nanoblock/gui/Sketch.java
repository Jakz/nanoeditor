package pixbits.nanoblock.gui;


import pixbits.nanoblock.*;
import pixbits.nanoblock.data.*;

import java.awt.Color;
import java.util.*;
import javax.swing.event.*;
import processing.core.*;

/*
 * 
 * Ê1,1,44,48 Ê 46,1,66,59 Ê Ê113,1,66,59 Ê Ê180,1,88,70
 *  1,72,44,48
 */

public class Sketch extends PApplet implements ChangeListener
{
	final List<Drawable> drawables = new ArrayList<Drawable>();
  
  
  Tileset tileset;
	Model model;
  
  public void setup()
  {
    smooth();
    size(Main.SW, Main.SH, P2D);
    tileset = new Tileset("tileset.png", 11, 22, 22);
    
    tileset.addSpec(PieceType.P1x1, 1, 6, 44, 43, -22, -33);
    tileset.addSpec(PieceType.CAP, 269, 1, 20, 21, -10, -16);
    
    model = new Model(10,10);
    model.allocateLevels(3);
    
    LevelView levelView = new LevelView(model, model.levelAt(2),10,50,10,10,10);
    drawables.add(levelView);
    
    levelView = new LevelView(model, model.levelAt(1),10,190,10,10,10);
    drawables.add(levelView);
    
    levelView = new LevelView(model, model.levelAt(0),10,330,10,10,10);
    drawables.add(levelView);
    
    model.addPiece(0, PieceType.P1x1, 0, 0);
    model.addPiece(0, PieceType.P1x1, 0, 1);
    model.addPiece(0, PieceType.P1x1, 0, 2);
    model.addPiece(0, PieceType.P1x1, 2, 0);
    model.addPiece(0, PieceType.P1x1, 2, 1);
    model.addPiece(0, PieceType.P1x1, 2, 2);
    model.addPiece(0, PieceType.P1x1, 1, 0);
    model.addPiece(0, PieceType.P1x1, 1, 2);
    model.addPiece(1, PieceType.P1x1, 0, 0);
    model.addPiece(1, PieceType.P1x1, 2, 2);
    model.addPiece(1, PieceType.P1x1, 2, 0);
    model.addPiece(1, PieceType.P1x1, 0, 2);

    System.out.println("COUNT: "+model.levelAt(0).count());
    
    noLoop();
  }
  
  public void draw()
  {
  	background(220); 

  	for (int l = 0; l < model.levelCount(); ++l)
  	{
  	  System.out.println("Drawing level "+l);
  	  Level level = model.levelAt(l);
  	  Iterator<Piece> pieces = level.iterator();

  	  while (pieces.hasNext())
      {
        Piece piece = pieces.next();

        drawPiece(piece, piece.x, piece.y, l);
      }
  	}
  	
  	for (Drawable d : drawables)
  	  d.draw(this);
  }
  
  public int baseX = 500;
  public int baseY = 200;
  
  public void drawPiece(Piece piece, int x, int y, int l)
  {
    int fx = baseX+tileset.xOffset*x-tileset.yOffset*y;
    int fy = baseY+tileset.hOffset*(x+y-l*2)+2*l;
    
    Tileset.PieceSpec spec = tileset.spec(piece.type);
    this.blend(tileset.image, spec.x, spec.y, spec.w, spec.h, fx+spec.ox, fy+spec.oy, spec.w, spec.h, BLEND);

  }
  
  public void drawCap(int x, int y, int l)
  {
    int fx = baseX+tileset.xOffset*x-tileset.yOffset*y;
    int fy = baseY+tileset.hOffset*(x+y-l*2)+2*l;
    Tileset.PieceSpec cap = tileset.spec(PieceType.CAP);
    this.blend(tileset.image, cap.x, cap.y, cap.w, cap.h, fx+cap.ox, fy+cap.oy-tileset.hOffset*2, cap.w, cap.h, BLEND);
  }
  
 
  public void keyPressed()
  {
    if (this.key == 't')
    {
      
    }
    else
    {

    }

    
    /*for (int j = 0; j < 30; ++j)
      Dungeon.i.separationStep();*/
    redraw();
  }

  public void mouseReleased()
  {    	
    int x = mouseX;
    int y = mouseY;
    
    for (Drawable d : drawables)
    {
      if (d.isInside(x, y))
        d.mouseClicked(x, y);
    }
  }
  
  public void mousePressed()
  {    	

    
  }
  
  public void mouseMoved()
  { 
  	
  }
  
  int lx = -1, ly = -1;
  
  public void mouseDragged()
  { 	

    mousePressed();
  }
  
  public void stateChanged(ChangeEvent e)
  {
  	
  }
  
  void reset()
  {

  }
  
  void fill(Color c)
  {
  	fill(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
  }

}
