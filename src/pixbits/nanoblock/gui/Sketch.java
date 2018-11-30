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

import java.awt.Rectangle;
import java.util.*;
import java.awt.event.*;
import java.nio.file.Paths;

import javax.swing.event.*;

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

  final List<Drawable> drawables = new ArrayList<Drawable>();
  	
	public LevelStackView levelStackView;
	public PiecePaletteView pieceView;
	public IsometricView isometricView;
	
	public PFont font;
	
	private Model model;
	
	public Model getModel() { return model; }
  
  public void setup()
  {
    //smooth();
    size(Main.SW, Main.SH, Sketch.P2D);
    
    PieceType.initMapping();
    Brush.tileset = TileSetLoader.loadAndBuild(Paths.get("tileset.json"));

    font = createFont("Helvetica", 16);
        
    ColorPaletteView paletteView = new ColorPaletteView(this, 320, 700, 30, 10);
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
    
    if (isometricView != null) drawables.remove(isometricView);
    isometricView = new IsometricView(this, model);
    drawables.add(isometricView);
    
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
  
  //public int baseX = 750;
  //public int baseY = 300;
  
  public int hoveredIndex = -1;
  
  public void draw()
  {
  	if (model == null)
  	  return;

    background(220);
    	
  	for (Drawable d : drawables)
  	  d.draw();
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
}
