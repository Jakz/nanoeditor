package pixbits.nanoblock.gui;


import pixbits.nanoblock.*;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.json.TileSetLoader;

import java.awt.Color;
import java.util.*;
import javax.swing.event.*;
import processing.core.*;

/*
 * 
 * �1,1,44,48 � 46,1,66,59 � �113,1,66,59 � �180,1,88,70
 *  1,72,44,48
 */

public class Sketch extends PApplet implements ChangeListener
{
	final List<Drawable> drawables = new ArrayList<Drawable>();
  
	Model model;
  
  public void setup()
  {
    smooth();
    size(Main.SW, Main.SH, P2D);
    
    PieceType.initMapping();
    Brush.tileset = TileSetLoader.loadAndBuild("tileset.json");

    
    model = new Model(20,20);
    model.allocateLevels(5);
    
    LevelView levelView = new LevelView(model, model.levelAt(2),10,10,14);
    drawables.add(levelView);
    
    levelView = new LevelView(model, model.levelAt(1),10,300,14);
    drawables.add(levelView);
    
    levelView = new LevelView(model, model.levelAt(0),10,590,14);
    drawables.add(levelView);
    
    ColorPaletteView paletteView = new ColorPaletteView(300,700,30,5);
    drawables.add(paletteView);
    
    PiecePaletteView pieceView = new PiecePaletteView(300,760,100,5);
    drawables.add(pieceView);
    
    noLoop();
  }
  
  public void draw()
  {
  	background(220); 

  	for (int l = 0; l < model.levelCount(); ++l)
  	{
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
  
  public int baseX = 800;
  public int baseY = 200;
  
  public void drawPiece(Piece piece, int x, int y, int l)
  {
    int fx = baseX+Brush.tileset.xOffset*x-Brush.tileset.yOffset*y;
    int fy = baseY+Brush.tileset.hOffset*(x+y-l*2)+2*l;
    java.awt.Point c = Brush.tileset.color(piece.color);
    
    Tileset.PieceSpec spec = Brush.tileset.spec(piece.type);
    this.blend(Brush.tileset.image, spec.x + c.x, spec.y + c.y, spec.w, spec.h, fx+spec.ox, fy+spec.oy, spec.w, spec.h, BLEND);

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
  
  void stroke(Color c)
  {
    stroke(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
  }

}
