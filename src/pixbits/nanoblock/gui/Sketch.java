package pixbits.nanoblock.gui;


import pixbits.nanoblock.*;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.gui.Tileset.PieceSpec;
import pixbits.nanoblock.gui.ui.*;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.ModelOperations;
import pixbits.nanoblock.tasks.Tasks;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.files.TileSetLoader;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.*;
import java.awt.event.*;
import java.nio.file.Paths;

import javax.swing.event.*;

import com.pixbits.lib.collections.LockableList;
import com.pixbits.lib.lang.Point;
import com.pixbits.lib.lang.Size;
import com.pixbits.lib.ui.color.Color;

import processing.core.*;

/*
 * 
 *  1,1,44,48  46,1,66,59   113,1,66,59   180,1,88,70
 *  1,72,44,48
 */

public class Sketch extends PApplet implements ChangeListener
{
  private static final long serialVersionUID = 1L;

  boolean hasInit = false;

  final LockableList<Drawable> drawables = new LockableList<>(new ArrayList<>());
    	
	public LevelStackView levelStackView;
	public Drawable.Wrapper<PiecePaletteView> pieceView;
	public ColorPaletteView colorPaletteView;
	public IsometricView isometricView;
	
	public PFont font;
  PImage tmp = null;

	
	private Model model;
	
	public Model getModel() { return model; }
	
  
  public void setup()
  {
    noLoop();

    //smooth();
    size(Main.SW, Main.SH, Sketch.P2D);
    
    PieceType.initMapping();
    Brush.tileset = TileSetLoader.loadAndBuild(Paths.get("tileset.json"));

    font = createFont("Helvetica", 16);
        
    colorPaletteView = new ColorPaletteView(this, 320, 700, 30, 10);
    drawables.add(colorPaletteView);
    
    pieceView = new Drawable.Wrapper<>(null);
    drawables.add(pieceView);
        
    updatePiecePalette();
    
    tmp = loadImage("tileset.png");

    /*UICheckBox checkBox = new UICheckBox(this, 400,20,20,"Antani");
    addDrawable(checkBox);
    
    addDrawable(new UIButton(this,500,20,20,20));*/
    hasInit = true;
  }
  
  public void initForModel(Model model)
  {
    if (levelStackView != null) levelStackView.dispose(this);
    levelStackView = new LevelStackView(this, 3, 0, 0, 14, 20, model);
    
    if (isometricView != null) drawables.remove(isometricView);
    isometricView = new IsometricView(this, model);
    drawables.add(isometricView);
    
    colorPaletteView.setPosition(levelStackView.totalWidth() + GUI.margin, colorPaletteView.y());
        
    this.model = model;
    
    updatePiecePalette();
  }
  
  public void onResize()
  {
    if (hasInit)
    {
      updatePiecePalette();
      redraw();
    }
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
    
    int baseX = levelStackView != null ? levelStackView.totalWidth() + GUI.margin : 0;
    int availableWidth = getWidth() - baseX - GUI.margin;
    int availableCells = availableWidth / GUI.piecePaletteCellSize;
    
    pieceView.set(new PiecePaletteView(this, baseX, 760, GUI.piecePaletteCellSize, availableCells, Settings.values.get(Setting.USE_TAB_TO_ROTATE)));
  }
  
  public void addDrawable(Drawable d)
  {
    drawables.add(d);
  }
  
  public void removeDrawable(Drawable d)
  {
    drawables.remove(d);
  }
  
  //public int baseX = 750;
  //public int baseY = 300;
  
  public int hoveredIndex = -1;
  
  List<Sprite> sprites = new ArrayList<>();
  
  void generateSprites()
  {
    Piece piece = new Piece(PieceType.P2x4, PieceColor.CREAM, 0, 0);
    Tileset ts = Brush.tileset;

    int l = 0;
    
    {
      /*piece.type.forEachCap((xx, yy) -> {
        final int isoX = piece.x + xx;
        final int isoY = piece.y + yy; 
        java.awt.Point position = PieceDrawer.isometricPositionForCoordinate(isoX, isoY, l+1);
      
        sprites.add(new Sprite(
            new Sprite.Key(isoX, isoY, l+1, Sprite.Type.TOP), 
            new java.awt.Point(getWidth()/2 + position.x - ts.xOffset, getHeight()/2 + position.y - ts.yOffset + 4),
            new Rectangle(1, 121, 44, 27)
            )
        );
      });*/

      Atlas atlas = new Atlas(1, 149,  45, 24, 8);
      for (int yy = 0; yy < piece.type.height; ++yy)
        for (int xx = 0; xx < piece.type.width; ++xx)
        {
          final int isoX = piece.x + xx*2;
          final int isoY = piece.y + yy*2; 
          java.awt.Point position = PieceDrawer.isometricPositionForCoordinate(isoX, isoY, l);
        
          int mask = piece.type.mask(xx, yy);     
          sprites.add(new Sprite(
              new Sprite.Key(isoX, isoY, l, Sprite.Type.TOP), 
              new java.awt.Point(getWidth()/2 + position.x - ts.xOffset, getHeight()/2 + position.y - ts.yOffset*2),
              atlas.get(mask)
              )
          );
        }
    }
    
    {
      Atlas atlas = new Atlas(1, 197,  22, 32,  45, 33);
      for (int xx = 0; xx < piece.type.width; ++xx)
      {
        final int isoX = piece.x + xx*2;
        final int isoY = piece.y + (piece.type.height - 1)*2; 
        
        java.awt.Point position = PieceDrawer.isometricPositionForCoordinate(isoX, isoY + 2, l); //TODO: +2 is an hack to adjust the value directly as coordinate
        int mask = piece.type.maskSouth(xx, piece.type.height - 1); 
        
        sprites.add(new Sprite(
            new Sprite.Key(isoX, isoY, l, Sprite.Type.WALL), 
            new java.awt.Point(getWidth()/2 + position.x, getHeight()/2 + position.y - ts.yOffset*2),
            atlas.get(mask)
            )
        );
      }
    }

    {
      Atlas atlas = new Atlas(181, 197,  22, 32,  45, 33);
      for (int yy = 0; yy < piece.type.height; ++yy)
      {
        final int isoX = piece.x + (piece.type.width - 1)*2;
        final int isoY = piece.y + yy*2;
        
        java.awt.Point position = PieceDrawer.isometricPositionForCoordinate(isoX, isoY, l);
        int mask = piece.type.maskEast(piece.type.width - 1, yy);   
        
        sprites.add(new Sprite(
            new Sprite.Key(isoX, isoY, l, Sprite.Type.WALL), 
            new java.awt.Point(getWidth()/2 + position.x, getHeight()/2 + position.y - ts.yOffset),
            atlas.get(mask)
            )
        );
      }
    }
    
    Collections.sort(sprites);
  }
      
  public void draw()
  {
  	if (model == null)
  	  return;

    background(GUI.theme.background);

    if (sprites.isEmpty())
      generateSprites();
    
    for (Sprite sprite : sprites)
      sprite.draw(this);
  
    //PieceDrawer.drawPiece(this, getWidth()/2, getHeight()/2, piece.type, piece.color, 0, 0, 0);
    //PieceDrawer.drawPiece(this, getWidth()/2, getHeight()/2, piece.type, piece.color, 4, 0, 0);



   
    /*
    drawables.setLocked(true);
  	for (Drawable d : drawables)
  	  d.draw();
  	drawables.setLocked(false);*/ 
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
  
  public void mouseReleased()
  {    	
    int x = mouseX;
    int y = mouseY;

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
        d.mouseWheelMoved(x, y, amount);
    }
  }
  
  public void stateChanged(ChangeEvent e)
  {
  	
  }
  
  void reset()
  {

  }
  
  public void rect(int r, int g, int b, int a, int x, int y, int w, int h)
  {
    noFill();
    stroke(r,g,b,a);
    rect(x, y, w, h);
  }
  
  public void fill(java.awt.Color c) { fill(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()); }
  public void stroke(java.awt.Color c) { stroke(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()); }

  public void fill(Color c) { fill(c.r(), c.g(), c.b(), c.a()); }
  public void stroke(Color c) { stroke(c.r(), c.g(), c.b(), c.a()); }
  
  public void background(Color c) { super.background(c.toInt()); }
}
