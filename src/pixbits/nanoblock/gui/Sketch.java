package pixbits.nanoblock.gui;


import pixbits.nanoblock.*;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.ui.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.ModelOperations;
import pixbits.nanoblock.tasks.Tasks;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.files.TileSetLoader;

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
	public PiecePaletteView pieceView;
	
	public PFont font;
	
	private Model model;
	
	public Model getModel() { return model; }
  
  public void setup()
  {
    //smooth();
    size(Main.SW, Main.SH, Sketch.P2D);
    
    PieceType.initMapping();
    Brush.tileset = TileSetLoader.loadAndBuild("tileset.json");

    font = createFont("Helvetica", 16);


    /*Library.model = ModelLoader.loadModel(new File("model.nblock"));
    
    if (Library.model == null)
    { 
      Library.model = new Model(20,20);
      Library.model.allocateLevels(12);
    }*/
        
    ColorPaletteView paletteView = new ColorPaletteView(this, 320,700,30,7);
    drawables.add(paletteView);
    
    updatePiecePalette();

    /*UICheckBox checkBox = new UICheckBox(this, 400,20,20,"Antani");
    addDrawable(checkBox);
    
    addDrawable(new UIButton(this,500,20,20,20));*/
    
    noLoop();
  }
  
  public void initForModel(Model model)
  {
    if (levelStackView != null) levelStackView.dispose(this);
    levelStackView = new LevelStackView(this, 3, 0, 0, 14, 20, model);
    
    this.model = model;
  }
  
  public void hideMe()
  {
    this.model = null; 
  }
  
  public void updatePiecePalette()
  {
    PieceType brush = Brush.realType();
    
    boolean found = false;
    for (PieceType spiece : PieceType.spieces)
      if (brush == spiece)
      {
        found = true;
        break;
      }
    
    if (!found)
      Brush.setType(PieceType.getRotation(brush));
    
    if (pieceView != null) pieceView.dispose(this);
    pieceView = new PiecePaletteView(this, 320,760,100,10, Settings.values.get(Setting.USE_TAB_TO_ROTATE));
    drawables.add(pieceView);
  }
  
  public void addDrawable(Drawable d)
  {
    drawables.add(d);
  }
  
  public void removeDrawable(Drawable d)
  {
    drawables.remove(d);
  }
  
  public int baseX = 750;
  public int baseY = 300;
  
  public int hoveredIndex = -1;
  
  public void draw()
  {
  	if (model == null)
  	  return;
    
    
    background(220);
  	
  	Level hovered = levelStackView.getHoveredLevel();
  	hoveredIndex = -1;
  	Rectangle hoverRect = levelStackView.hover();
  	
  	if (levelStackView.getLocked() != null)
  	  hovered = levelStackView.getLocked();
  	
  	int rx = baseX;
  	int ry = baseY;// + Brush.tileset.yOffset;
  	
  	for (int l = 0; l < model.levelCount(); ++l)
  	{
  	  Level level = model.levelAt(l);
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
      //Rectangle bounds = PieceDrawer.computeRealBounds(model, Settings.values.get(Setting.DRAW_CAPS));
      
      Rectangle bounds = PieceDrawer.computeLayerBoundsWithPiece(model, hoveredIndex);
      
      this.strokeWeight(2.0f);

      
      if (bounds != null)
      {
        this.stroke(255,0,0,200);
        this.noFill();
        this.rect(rx+bounds.x, ry+bounds.y, bounds.width, bounds.height);
      }
      
      bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);

      if (bounds != null)
      {
        this.stroke(255,128,0,200);
        this.noFill();
        this.rect(rx+bounds.x, ry+bounds.y, bounds.width, bounds.height);
      }
      
      bounds = PieceDrawer.computeRealBounds(model, hoveredIndex, Settings.values.get(Setting.DRAW_CAPS));
      
      if (bounds != null)
      {
        this.stroke(0,0,180,200);
        this.noFill();
        this.rect(rx+bounds.x, ry+bounds.y, bounds.width, bounds.height);
      }
      
      bounds = PieceDrawer.computeRealBounds(model, Settings.values.get(Setting.DRAW_CAPS));
      
      if (bounds != null)
      {
        this.stroke(0,180,0,200);
        this.noFill();
        this.rect(rx+bounds.x, ry+bounds.y, bounds.width, bounds.height);
      }
    }
  }
  
  public void drawGrid(int l)
  {
    this.strokeWeight(2.0f);
    this.stroke(40,40,40,128);
    
    int w = model.getWidth(), h = model.getHeight();
    
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
    
    int w = model.getWidth(), h = model.getHeight();
    
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
    int baseX = this.baseX;
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
    int baseX = this.baseX;
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
    if (this.key == 'r')
    {
      new ModelOperations.Reset(model).execute();
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
        case UP: new ModelOperations.Shift(model, Direction.NORTH).execute(); break;
        case DOWN: new ModelOperations.Shift(model, Direction.SOUTH).execute(); break;
        case LEFT:
        {
          if ((keyEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0)
            new ModelOperations.Rotate(model, Direction.WEST).execute();
          else
            new ModelOperations.Shift(model, Direction.WEST).execute();
          break;
        }
        case RIGHT: 
        {
          if ((keyEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0)
            new ModelOperations.Rotate(model, Direction.EAST).execute();
          else
            new ModelOperations.Shift(model, Direction.EAST).execute();
          break;
        }
      }     
    }

    redraw();
  }
  
  int lockX = -1, lockY = -1;
  boolean draggingLock = false;

  public void mouseReleased()
  {    	
    int x = mouseX;
    int y = mouseY;
    
    if (draggingLock)
    {
      draggingLock = false;
      return;
    }
        
    for (Drawable d : drawables)
    {
      boolean dragLocked = d.draggingLock(); 
      d.draggingReset();
      
      if (!dragLocked && d.isInside(x, y))
      {
        d.mouseReleased(x, y, mouseButton);
        return;
      }
    }
    
    Level locked = levelStackView.getLocked();
    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += baseX;
      bounds.y += baseY;
      
      Rectangle hover = levelStackView.hover();

      if (x >= bounds.x && x < bounds.x+bounds.width && y >= bounds.y && y < bounds.y+bounds.height && hover != null)
      {
        Piece piece = locked.pieceAt(hover.x,hover.y);
        
        if (!locked.isFreeAt(hover.x, hover.y))
        {
          new ModelOperations.Remove(model, hoveredIndex, hover.x, hover.y).execute();
          levelStackView.clearToBeDeleted();
        }
        else if (locked.canPlace(Brush.type(), hover.x, hover.y))
          new ModelOperations.Place(model, hoveredIndex, Brush.type(), Brush.color, hover.x, hover.y).execute();
        
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
    
    if (!draggingLock)
    {
      for (Drawable d : drawables)
        if (d.draggingLock())
        {
          d.mouseDragged(x, y, mouseButton);
          return;
        }
      
      for (Drawable d : drawables)
      {
        if (d.isInside(x, y))
        {
          d.mouseDragged(x, y, mouseButton);
          return;
        }
      }
    }
    
    if (mouseButton == PConstants.RIGHT)
    {
      if (!draggingLock)
      {        
        Level locked = levelStackView.getLocked();

        if (locked == null)
        {
          Rectangle bounds = PieceDrawer.computeRealBounds(model, false);
          bounds.x += baseX;
          bounds.y += baseY;
          
        if (x >= bounds.x && x < bounds.x + bounds.width && y >= bounds.y && y < bounds.y + bounds.height)
        {
          draggingLock = true;
          lockX = x;
          lockY = y;
        }
        else
          return;
          
        }
        else
          return;
      }
      
      this.baseX += x - lockX;
      this.baseY += y - lockY;
      
      lockX = x;
      lockY = y;
      
      redraw();
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
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += baseX;
      bounds.y += baseY;
      
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

        
        if (ix >= 0 && ix+bw <= model.getWidth() && iy >= 0 && iy+bh <= model.getHeight())
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
  
  public void mouseWheelMoved(int amount)
  {
    int x = mouseX;
    int y = mouseY;
    
    for (Drawable d : drawables)
    {
      if (d.isInside(x, y))
        d.mouseWheelMoved(amount);
    }
    
    Level locked = levelStackView.getLocked();

    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += baseX;
      bounds.y += baseY;
      
      if (x >= bounds.x && x < bounds.x + bounds.width && y >= bounds.y && y < bounds.y + bounds.height)
      {
        if (amount > 0 && locked.previous() != null)
        {
          levelStackView.setLocked(locked.previous());
          mouseMoved();
        }
        else if (amount < 0 && locked.next() != null)
        {
          levelStackView.setLocked(locked.next());
          mouseMoved();
        }   
      }
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
