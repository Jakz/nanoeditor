package pixbits.nanoblock.gui;


import pixbits.nanoblock.*;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.Tasks;
import pixbits.nanoblock.files.ModelLoader;
import pixbits.nanoblock.files.TileSetLoader;
import pixbits.nanoblock.files.Library;

import java.io.*;
import java.awt.Color;
import java.awt.Rectangle;
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
    
    Library.model = ModelLoader.loadModel(new File("model.nblock"));
    
    if (Library.model == null)
    { 
      Library.model = new Model(20,20);
      Library.model.allocateLevels(12);
    }
    
    levelStackView = new LevelStackView(this, 3, 0, 0, 14, 20, Library.model);
    
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
  	
  	Level hovered = hoveredLevel();
  	int hoveredIndex = -1;
  	Rectangle hoverRect = levelStackView.hover();
  	
  	for (int l = 0; l < Library.model.levelCount(); ++l)
  	{
  	  Level level = Library.model.levelAt(l);
  	  Iterator<Piece> pieces = level.iterator();

  	  if (hovered == level)
  	  {
  	    hoveredIndex = l;
  	    
  	    if (Settings.values.get(Setting.VIEW_SHOW_LAYER_GRID))
  	    {
  	      if (Settings.values.get(Setting.VIEW_LAYER_GRID_ALPHA))
  	        drawFilledGrid(l);
  	      else
  	        drawGrid(l);
  	    }
  	     	    
  	    if (Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE_WITH_BACK_FILL)
  	    {
  	      drawGridHover(hoveredIndex, hoverRect);
  	      drawGridHoverFill(hoveredIndex, hoverRect);
  	    }
  	    else if (Settings.values.getHoverPiece() == Setting.HoverPiece.BACK_STROKE)
  	    {
          drawGridHover(hoveredIndex, hoverRect);
  	    }
  	    
  	  }

  	  
  	  while (pieces.hasNext())
      {
        Piece piece = pieces.next();

        if (piece.type != PieceType.CAP || Settings.values.get(Setting.DRAW_CAPS))
        drawPiece(piece, piece.x, piece.y, l, level);
      }
  	}
  	
  	for (Drawable d : drawables)
  	  d.draw();

    if (Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE_WITH_BACK_FILL || Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE)
    {
      if (hoveredIndex != -1)
        drawGridHover(hoveredIndex, hoverRect);
    }
  }
  
  public int baseX = 800;
  public int baseY = 260;
  
  public void drawGrid(int l)
  {
    this.strokeWeight(2.0f);
    this.stroke(40,40,40,128);
    
    int w = Library.model.getWidth(), h = Library.model.getHeight();
    
    for (int i = 0; i < w+1; ++i)
      drawGridLine(0,i*2,h*2,i*2,l);
    
    for (int i = 0; i < h+1; ++i)
      drawGridLine(i*2,0,i*2,w*2,l);
  }
  
  public void drawFilledGrid(int l)
  {
    this.strokeWeight(2.0f);
    this.fill(220,220,220,150);
    this.stroke(40,40,40,128);
    
    int w = Library.model.getWidth(), h = Library.model.getHeight();
    
    for (int i = 0; i < w; ++i)
      for (int j = 0; j < h; ++j)
      {
        drawIsoSquare(i*2,j*2, 2, 2, l);
      }
  }
  
  public void drawGridHoverFill(int h, Rectangle r)
  {    
    if (r != null)
    {
      this.fill(220,0,0,180);
      this.drawIsoSquare(r.x, r.y, r.width*2, r.height*2, h);
    }

  }
  
  public void drawGridHover(int h, Rectangle r)
  {    
    if (r != null)
    {
      this.strokeWeight(4.0f);
      this.stroke(180,0,0,220);
    
      drawGridLine(r.x, r.y, r.x+r.width*2, r.y, h);
      drawGridLine(r.x, r.y+r.height*2, r.x+r.width*2, r.y+r.height*2, h);
      drawGridLine(r.x, r.y, r.x, r.y+r.height*2, h);
      drawGridLine(r.x+r.width*2, r.y, r.x+r.width*2, r.y+r.height*2, h);
    }
  }
  
  public void drawIsoSquare(int x, int y, int w, int h, int l)
  {
    int fx1 = (int)(baseX+Brush.tileset.xOffset*x/2.0f-Brush.tileset.yOffset*y/2.0f); // UP
    int fy1 = (int)(baseY+Brush.tileset.hOffset*(x/2.0f+y/2.0f-l*2));
    
    int fx2 = (int)(baseX+Brush.tileset.xOffset*(x+w)/2.0f-Brush.tileset.yOffset*(y+h)/2.0f); // DOWN
    int fy2 = (int)(baseY+Brush.tileset.hOffset*((x+w)/2.0f+(y+h)/2.0f-l*2));
    
    int fx3 = (int)(baseX+Brush.tileset.xOffset*(x)/2.0f-Brush.tileset.yOffset*(y+h)/2.0f); // LEFT
    int fy3 = (int)(baseY+Brush.tileset.hOffset*((x)/2.0f+(y+h)/2.0f-l*2));
    
    int fx4 = (int)(baseX+Brush.tileset.xOffset*(x+w)/2.0f-Brush.tileset.yOffset*(y)/2.0f); // RIGHT
    int fy4 = (int)(baseY+Brush.tileset.hOffset*((x+w)/2.0f+(y)/2.0f-l*2));
    
    fy1 += Brush.tileset.hAdjust*l - 13;
    fy2 += Brush.tileset.hAdjust*l - 13;
    fy3 += Brush.tileset.hAdjust*l - 13;
    fy4 += Brush.tileset.hAdjust*l - 13;
    
    beginShape();
    vertex(fx1, fy1);
    vertex(fx3, fy3);
    vertex(fx2, fy2);
    vertex(fx4, fy4);
    vertex(fx1, fy1);
    endShape();
  }
  
  public void drawGridLine(int x1, int y1, int x2, int y2, int h)
  {
    int fx1 = (int)(baseX+Brush.tileset.xOffset*x1/2.0f-Brush.tileset.yOffset*y1/2.0f);
    int fy1 = (int)(baseY+Brush.tileset.hOffset*(x1/2.0f+y1/2.0f-h*2));
    int fx2 = (int)(baseX+Brush.tileset.xOffset*x2/2.0f-Brush.tileset.yOffset*y2/2.0f);
    int fy2 = (int)(baseY+Brush.tileset.hOffset*(x2/2.0f+y2/2.0f-h*2));
    
    fy1 += Brush.tileset.hAdjust*h - 13;
    fy2 += Brush.tileset.hAdjust*h - 13;

    this.line(fx1, fy1, fx2, fy2);
  }
  
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
      Tasks.MODEL_RESET.execute();
    }
    else if (this.key == '-')
    {
      Level hovered = hoveredLevel();
      
      if (hovered != null)
        Library.model.insertBelow(hovered);
      
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
        Library.model.insertAbove(hovered);
      
      // TODO: manage refresh of the grid
    }
    else if (this.key == CODED)
    {      
      switch (this.keyCode)
      {
        case UP: Tasks.MODEL_SHIFT_NORTH.execute(Library.model); break;
        case DOWN: Tasks.MODEL_SHIFT_SOUTH.execute(Library.model); break;
        case LEFT:
        {
          if ((keyEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0)
            Tasks.MODEL_ROTATE_WEST.execute();
          else
            Tasks.MODEL_SHIFT_WEST.execute();
          break;
        }
        case RIGHT: 
        {
          if ((keyEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0)
            Tasks.MODEL_ROTATE_EAST.execute();
          else
            Tasks.MODEL_SHIFT_EAST.execute();
          break;
        }
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
