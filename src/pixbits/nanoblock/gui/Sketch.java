package pixbits.nanoblock.gui;


import pixbits.nanoblock.*;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.files.ModelLoader;
import pixbits.nanoblock.files.TileSetLoader;

import java.io.*;
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
    
    Piece pieces[] = new Piece[] {
       //new Piece(PieceType.P2x1, PieceColor.WHITE, 2, 0),
       new Piece(PieceType.P2x1, PieceColor.YELLOW, 6, 0),
       //new Piece(PieceType.P2x1, PieceColor.GREEN_LIME, 0, 2),
       //new Piece(PieceType.P2x1, PieceColor.ORANGE, 4, 2),
       new Piece(PieceType.P2x1, PieceColor.PINK, 2, 4),
       //new Piece(PieceType.P2x1, PieceColor.WHITE, 6, 4)
    };
    
    Level.PieceComparator p = new Level.PieceComparator();
    
    /*System.out.println("Real Test");
    System.out.println(p.compare(pieces[0], pieces[1])+" "+pieces[0].color+", "+pieces[1].color);
    System.out.println(p.compare(pieces[1], pieces[0])+" "+pieces[1].color+", "+pieces[0].color);*/
    
    model = ModelLoader.loadModel(new File("model.nblock"));
    
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

        if (piece.type != PieceType.CAP || Settings.values.get(Setting.DRAW_CAPS))
        drawPiece(piece, piece.x, piece.y, l, level);
      }
  	}
  	
  	for (Drawable d : drawables)
  	  d.draw();
  }
  
  public int baseX = 800;
  public int baseY = 260;
  
  public void drawPiece(Piece piece, int x, int y, int l, Level level)
  {    
    int fx = (int)(baseX+Brush.tileset.xOffset*x/2.0f-Brush.tileset.yOffset*y/2.0f);
    int fy = (int)(baseY+Brush.tileset.hOffset*(x/2.0f+y/2.0f-l*2));
    
    //if (piece.type != PieceType.CAP)
      fy += Brush.tileset.hAdjust*l;
    
    Tileset.PieceSpec spec = Brush.tileset.spec(piece.type);
    PImage texture = Brush.tileset.imageForColor(piece.color);
    this.fill(0);
    this.blend(texture, spec.x, spec.y, spec.w, spec.h, fx+spec.ox, fy+spec.oy, spec.w, spec.h, BLEND);
    
    
    if (Settings.values.get(Setting.SHOW_PIECE_ORDER))
      this.text(""+level.indexOfPiece(piece),fx+spec.ox+spec.w/2,fy+spec.oy+spec.h/2);
  }
  
  public Level hoveredLevel()
  {
    for (Drawable d : drawables)
    {
      if (d.isInside(mouseX, mouseY) && d instanceof LevelView)
      {
        return ((LevelView)d).level();
      }
    }
    
    return null;
  }

  public void keyPressed()
  {
    if (this.key == 'r')
    {
      model.clear();
    }
    else if (this.key == '-')
    {
      Level hovered = hoveredLevel();
      
      if (hovered != null)
        model.insertBelow(hovered);
      
      /*boolean wentOver = false;
      
      for (LevelView v : levelStackView.views)
      {
        if (v.level() == hovered)
          wentOver = true;
        else if (wentOver)
          
      }*/
      
      // TODO: manage refresh of the grid
      
    }
    else if (this.key == '+')
    {
      Level hovered = hoveredLevel();
      
      if (hovered != null)
        model.insertAbove(hovered);
      
      // TODO: manage refresh of the grid
    }
    else if (this.key == CODED)
    {
      switch (this.keyCode)
      {
        case UP: if (model.canShift(Direction.NORTH)) { model.shift(Direction.NORTH); redraw(); } break;
        case DOWN: if (model.canShift(Direction.SOUTH)) { model.shift(Direction.SOUTH); redraw(); } break;
        case LEFT: if (model.canShift(Direction.EAST)) { model.shift(Direction.EAST); redraw(); } break;
        case RIGHT: if (model.canShift(Direction.WEST)) { model.shift(Direction.WEST); redraw(); } break;
        
      }
    }

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
