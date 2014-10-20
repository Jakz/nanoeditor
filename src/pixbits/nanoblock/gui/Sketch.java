package pixbits.nanoblock.gui;


import pixbits.nanoblock.*;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.Tasks;
import pixbits.nanoblock.files.Log;
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
  
  public int baseX = 350;
  public int baseY = 300;
  
  public int hoveredIndex = -1;
  
  public void draw()
  {
  	background(220);
  	
  	Level hovered = levelStackView.getHoveredLevel();
  	hoveredIndex = -1;
  	Rectangle hoverRect = levelStackView.hover();
  	
  	if (levelStackView.getLocked() != null)
  	  hovered = levelStackView.getLocked();
  	
  	int rx = baseX + Library.model.getWidth()*Brush.tileset.xOffset;
  	int ry = baseY + Brush.tileset.yOffset;
  	
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
        PieceDrawer.drawPiece(this, rx, ry, piece, piece.x, piece.y, l, level);
      }
  	}
  	
  	for (Drawable d : drawables)
  	  d.draw();

    if (Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE_WITH_BACK_FILL || Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE)
    {
      if (hoveredIndex != -1)
        drawGridHover(hoveredIndex, hoverRect);
    }
    
    if (hoveredIndex != -1)
    {
      Rectangle bounds = layerBounds(hoveredIndex);
      
      this.noFill();
      this.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
  }
  
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
      this.strokeWeight(1.0f);
      this.stroke(0);
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
    int baseX = this.baseX + Brush.tileset.xOffset * Library.model.getWidth();
    int baseY = this.baseY - 1;
    
    Tileset ts = Brush.tileset;

    int fx1 = (int) (baseX + (x - y)/2.0f * ts.xOffset);
    int fy1 = (int) (baseY + (x + y)/2.0f * ts.yOffset);

    int fx2 = (int) (baseX + ((x+w) - (y+h))/2.0f * ts.xOffset);
    int fy2 = (int) (baseY + ((x+w) + (y+h))/2.0f * ts.yOffset);

    int fx3 = (int) (baseX + (x - (y+h))/2.0f * ts.xOffset);
    int fy3 = (int) (baseY + (x + (y+h))/2.0f * ts.yOffset);

    int fx4 = (int) (baseX + ((x+w) - y)/2.0f * ts.xOffset);
    int fy4 = (int) (baseY + ((x+w) + y)/2.0f * ts.yOffset);
    
    fy1 -= Brush.tileset.hOffset*l;
    fy2 -= Brush.tileset.hOffset*l;
    fy3 -= Brush.tileset.hOffset*l;
    fy4 -= Brush.tileset.hOffset*l;
    
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
    int baseX = this.baseX + Brush.tileset.xOffset * Library.model.getWidth();
    int baseY = this.baseY - 1;
    
    int fx1 = (int) (baseX + (x1 - y1)/2.0f * Brush.tileset.xOffset);
    int fy1 = (int) (baseY + (x1 + y1)/2.0f * Brush.tileset.yOffset);
    int fx2 = (int) (baseX + (x2 - y2)/2.0f * Brush.tileset.xOffset);
    int fy2 = (int) (baseY + (x2 + y2)/2.0f * Brush.tileset.yOffset);
    
    fy1 -= Brush.tileset.hOffset*h;
    fy2 -= Brush.tileset.hOffset*h;

    this.line(fx1, fy1, fx2, fy2);
  }
  
  public boolean tabPressed = false;

  @Override
  public void keyReleased()
  {
    if (this.key == '\t' && Brush.typeInverted)
    {
      Brush.typeInverted = false;
      mouseMoved();
    }
  }
  
  @Override
  public void keyPressed()
  {
    System.out.println("Is enabled: "+Settings.values.get(Setting.USE_TAB_TO_ROTATE));
    
    if (this.key == 'r')
    {
      Tasks.MODEL_RESET.execute();
    }
    else if (this.key == '\t' && !Brush.typeInverted && Settings.values.get(Setting.USE_TAB_TO_ROTATE))
    {
      Brush.typeInverted = true;
      mouseMoved();
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
        d.mouseReleased(x, y, mouseButton);
        return;
      }
    }
    
    Level locked = levelStackView.getLocked();
    if (locked != null)
    {
      Rectangle bounds = layerBounds(hoveredIndex);
      Rectangle hover = levelStackView.hover();

      if (x >= bounds.x && x < bounds.x+bounds.width && y >= bounds.y && y < bounds.y+bounds.height && hover != null)
      {
        Piece piece = locked.pieceAt(hover.x,hover.y);
        
        if (!locked.isFreeAt(hover.x, hover.y))
        {
          Library.model.removePiece(locked, piece);
          levelStackView.clearToBeDeleted();
        }
        else if (locked.canPlace(Brush.type(), hover.x, hover.y))
          Library.model.addPiece(locked,Brush.type(),Brush.color,hover.x,hover.y);
        
        Main.sketch.redraw();
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
    
    Level locked = levelStackView.getLocked();

    if (locked != null)
    {
      Rectangle bounds = layerBounds(hoveredIndex);
      Rectangle hover = levelStackView.hover();
      
      if (x >= bounds.x && x < bounds.x + bounds.width && y >= bounds.y && y < bounds.y + bounds.height)
      {
        x -= bounds.x + bounds.width/2 ;
        y -= bounds.y;
        
        int ix = (x / Brush.tileset.xOffset + y / Brush.tileset.yOffset) / 2;
        int iy = (y / Brush.tileset.yOffset - x / Brush.tileset.xOffset) / 2;
        
        
        //ix -= Library.model.getWidth()/2;
        //iy += Library.model.getHeight()/2;
        
        
        int bw = Brush.type().width;
        int bh = Brush.type().height;
        
        Log.i("Hover: "+ix+", "+iy);

        
        if (ix >= 0 && ix+bw <= Library.model.getWidth() && iy >= 0 && iy+bh <= Library.model.getHeight())
        {
          if (hover == null || hover.x != ix*2 || hover.y != iy*2 || hover.width != bw || hover.height != bh)
          {
            this.strokeWeight(4.0f);
            this.stroke(180,0,0,220);

            levelStackView.setHover(new Rectangle(ix*2, iy*2, bw, bh));
          }
        }
        else
          levelStackView.setHover(null);
        
      }
    }

  }
  
  public Rectangle layerBounds(int l)
  {
    int sx = baseX, sw = Library.model.getWidth()*Brush.tileset.xOffset * 2;
    int sy = baseY - l * Brush.tileset.hOffset - 1, sh = Library.model.getHeight()*Brush.tileset.yOffset*2;
    
    return new Rectangle(sx,sy,sw,sh);
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
