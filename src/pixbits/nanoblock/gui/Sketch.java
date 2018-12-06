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

    final ParentNode<Node> node = new ParentNode<>(this);
      	
    public LevelStackView levelStackView;
	public PiecePaletteView pieceView;
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

    font = createFont("Helvetica", 16);
                
    tmp = loadImage("tileset.png");

    /*UICheckBox checkBox = new UICheckBox(this, 400,20,20,"Antani");
    addDrawable(checkBox);
    
    addDrawable(new UIButton(this,500,20,20,20));*/
  }
  
  public void initForModel(Model model)
  {
    this.model = model;
    
    node.clear();
    
    levelStackView = new LevelStackView(this, 14, 20, model);
    colorPaletteView = new ColorPaletteView(this, 30, 10);
    pieceView = new PiecePaletteView(this, GUI.piecePaletteCellSize);
    isometricView = new IsometricView(this, model);
    
    node.add(levelStackView);
    node.add(colorPaletteView);
    node.add(pieceView);
    node.add(isometricView);
    
    node.setSize(getWidth(), getHeight());
    node.onRevalidate();
    
    hasInit = true;
  }
  
  public void onResize()
  {
    if (hasInit)
    {
      node.setSize(getWidth(), getHeight());
      node.onRevalidate();
      redraw();
    }
  }
  
  public void hideMe()
  {
    this.model = null; 
  }
  
  public void updateComponentPositions()
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
  }
  
  //public int baseX = 750;
  //public int baseY = 300;
  
  public int hoveredIndex = -1;
  
  SpriteBatch sprites = new SpriteBatch();
  
  void generateSprites()
  {
    Piece piece = new Piece(PieceType.P2x4, PieceColor.CREAM, 0, 0);
    PieceDrawer.generateSprites(piece, sprites);
    sprites.setPosition(getWidth()/2, getHeight()/2); 
  }
      
  public void draw()
  {
    if (model == null || !hasInit)
  	  return;

    background(GUI.theme.background);
  	node.onDraw();
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
    
    node.onMouseReleased(x, y, mouseButton);
  }
  
  public void mousePressed()
  {    	

  }
 
  public void mouseDragged()
  {
    int x = mouseX;
    int y = mouseY;
    
    node.onMouseDragged(x, y, mouseButton);
  }
  
  public void mouseMoved()
  { 
    int x = mouseX;
    int y = mouseY;
    
    requestFocus();
    node.onMouseMoved(x, y);
  }
  
  public void mouseWheelMoved(int amount)
  {
    int x = mouseX;
    int y = mouseY;
    
    node.onMouseWheelMoved(x, y, amount);
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
