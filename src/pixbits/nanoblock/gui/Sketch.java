package pixbits.nanoblock.gui;


import pixbits.nanoblock.*;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;
import pixbits.nanoblock.files.ModelLoader;
import pixbits.nanoblock.files.TileSetLoader;

import java.awt.Color;
import java.util.*;
import java.awt.event.*;
import javax.swing.event.*;
import processing.core.*;

/*
 * 
 * Ê1,1,44,48 Ê 46,1,66,59 Ê Ê113,1,66,59 Ê Ê180,1,88,70
 *  1,72,44,48
 */

public class Sketch extends PApplet implements ChangeListener
{
  private static final long serialVersionUID = 1L;

  final List<Drawable> drawables = new ArrayList<Drawable>();
  
	public Model model;
	
	public LevelStackView levelStackView;
	
	public PFont font;
  
  public void setup()
  {
    //smooth();
    size(Main.SW, Main.SH, Sketch.P2D);
    
    PieceType.initMapping();
    Brush.tileset = TileSetLoader.loadAndBuild("tileset.json");

    font = createFont("Helvetica", 16);
    
    model = ModelLoader.loadModel("model.nblock");
    
    if (model == null)
    { 
      model = new Model(20,20);
      model.allocateLevels(12);
    }

    levelStackView = new LevelStackView(this, 3, 0, 0, 14, 10, model);
    
    ColorPaletteView paletteView = new ColorPaletteView(this, 320,700,30,6);
    drawables.add(paletteView);
    
    PiecePaletteView pieceView = new PiecePaletteView(this, 320,760,100,10);
    drawables.add(pieceView);
    
    UICheckBox checkBox = new UICheckBox(this, 400,20,20,"Antani");
    addDrawable(checkBox);
    
    addDrawable(new UIButton(this,500,20,20,20));

    noLoop();
  }
  
  public void addDrawable(Drawable d)
  {
    drawables.add(d);
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
  	  d.draw();
  }
  
  public int baseX = 800;
  public int baseY = 260;
  
  public void drawPiece(Piece piece, int x, int y, int l)
  {
    int fx = baseX+Brush.tileset.xOffset*x-Brush.tileset.yOffset*y;
    int fy = baseY+Brush.tileset.hOffset*(x+y-l*2);
    java.awt.Point c = Brush.tileset.color(piece.color);
    
    //if (piece.type != PieceType.CAP)
      fy += Brush.tileset.hAdjust*l;
    
    Tileset.PieceSpec spec = Brush.tileset.spec(piece.type);
    this.blend(Brush.tileset.image, spec.x + c.x, spec.y + c.y, spec.w, spec.h, fx+spec.ox, fy+spec.oy, spec.w, spec.h, BLEND);

  }

  public void keyPressed()
  {
    if (this.key == 'r')
    {
      model.clear();
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
      d.draggingReset();
      if (d.isInside(x, y))
      {
        d.mouseReleased(x, y);
        return;
      }
    }
  }
  
  public void mousePressed()
  {    	

    
  }
  
  public void mouseDragged()
  {
    int x = mouseX;
    int y = mouseY;
    
    for (Drawable d : drawables)
      if (d.draggingLock())
      {
        d.mouseDragged(x, y);
        return;
      }
    
    for (Drawable d : drawables)
    {
      if (d.isInside(x, y))
      {
        d.mouseDragged(x, y);
        return;
      }
    }
  }
  
  public void mouseMoved()
  { 
    int x = mouseX;
    int y = mouseY;
    
    for (Drawable d : drawables)
    {
      if (d.isInside(x, y))
        d.mouseMoved(x, y);
      else
        d.mouseExited();
    }
  }
  
  public void mouseWheelMoved(int amount)
  {
    int x = mouseX;
    int y = mouseY;
    
    for (Drawable d : drawables)
    {
      if (d.isInside(x, y))
        d.mouseWheelMoved(amount);
    }
  }
  
  int lx = -1, ly = -1;

  public void stateChanged(ChangeEvent e)
  {
  	
  }
  
  void reset()
  {

  }
  
  public void fill(Color c)
  {
  	fill(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
  }
  
  public void stroke(Color c)
  {
    stroke(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha());
  }

}
