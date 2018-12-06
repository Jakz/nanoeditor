package pixbits.nanoblock.gui;

import java.awt.Rectangle;
import java.awt.SystemTray;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Level;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.data.Piece;
import pixbits.nanoblock.data.PieceOutline;
import pixbits.nanoblock.data.PieceType;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.ModelOperations;
import processing.core.PConstants;
import processing.core.PImage;

public class IsometricView extends Node
{  
  private final Map<Level, SpriteBatch> cache; //TODO: never emptied, find a better solution
  private final Model model;
  
  private int hoveredIndex;

  IsometricView(Sketch p, Model model)
  {
    super(p, 0, 0);
    
    Rectangle bounds = PieceDrawer.computeLayerBounds(model, 0);

    this.model = model;
    this.hoveredIndex = -1;
        
    cache = new HashMap<>();
  }
  
  @Override
  public void revalidate()
  {
    Rectangle bounds = PieceDrawer.computeLayerBounds(model, 0);

    LevelStackView levelStackView = parent().at(0);
    ColorPaletteView colorPaletteView = parent().at(1);

    setPosition(
        levelStackView.totalWidth() + GUI.margin + bounds.width/2,
        colorPaletteView.y() - GUI.margin - bounds.width/2        
    );
  }

  public void invalidate()
  {
    for (Level level : model)
      level.setDirty(true);
  }
  
  public void draw()
  {
    int drawnSprites = 0;
    
    LevelStackView levelStackView = parent().at(0);

    
    Level hovered = levelStackView.getHoveredLevel();
    hoveredIndex = -1;
    PieceHover hover = levelStackView.hover();
   
    if (levelStackView.getLocked() != null)
      hovered = levelStackView.getLocked();
    
    for (int l = 0; l < model.levelCount(); ++l)
    {
      Level level = model.levelAt(l);

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
          drawGridHover(hoveredIndex, hover);
          drawGridHoverFill(hoveredIndex, hover);
        }
        else if (Settings.values.getHoverPiece() == Setting.HoverPiece.BACK_STROKE)
        {
          drawGridHover(hoveredIndex, hover);
        }
        
      }
      
      SpriteBatch batch = cache.computeIfAbsent(level, __ -> new SpriteBatch());   
      
      if (level.dirty())
      {
        batch.clear();
        for (Piece piece : level)
          PieceDrawer.generateSprites(piece, batch);
        level.setDirty(false);
      }
      
      batch.setPosition(x, y - l*Brush.tileset.hOffset);
      batch.draw(p);
      drawnSprites += batch.size();
  
      //TODO: reimplement DRAW_CAPS setting for new renderer
      /*for (Piece piece : level)
      {
        if (piece.type != PieceType.CAP || Settings.values.get(Setting.DRAW_CAPS))
        {
          PieceDrawer.drawPiece(p, ox, oy, piece, l, level);
        }
      }*/
    }
    
    p.text("Drawn Sprites: "+drawnSprites, 500, 30);
    

    if (Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE_WITH_BACK_FILL || Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE)
    {
      if (hoveredIndex != -1)
        drawGridHover(hoveredIndex, hover);
    }
    
    if (hoveredIndex != -1)
    {
      p.strokeWeight(2.0f);

      Rectangle bounds = PieceDrawer.computeLayerBoundsWithPiece(model, hoveredIndex);
      
      if (bounds != null)
        p.rect(255, 0, 0, 200,  x + bounds.x, y + bounds.y, bounds.width, bounds.height);

      bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);

      if (bounds != null)
        p.rect(255, 128, 0, 200,  x + bounds.x, y + bounds.y, bounds.width, bounds.height);
      
      bounds = PieceDrawer.computeRealBounds(model, hoveredIndex, Settings.values.get(Setting.DRAW_CAPS));
      
      if (bounds != null)
        p.rect(0, 0, 180, 200,  x + bounds.x, y + bounds.y, bounds.width, bounds.height);
      
      bounds = PieceDrawer.computeRealBounds(model, Settings.values.get(Setting.DRAW_CAPS));
      
      if (bounds != null)
        p.rect(0, 180, 0, 200,  x + bounds.x, y + bounds.y, bounds.width, bounds.height);
    }
  }
  
  public void drawGrid(int l)
  {
    p.strokeWeight(2.0f);
    p.stroke(40,40,40,128);
    p.noFill();
    
    int w = model.getWidth(), h = model.getHeight();
    
    for (int i = 0; i < w+1; ++i)
      drawGridLine(0,i*2,h*2,i*2,l);
    
    for (int i = 0; i < h+1; ++i)
      drawGridLine(i*2,0,i*2,w*2,l);
  }
  
  public void drawFilledGrid(int l)
  {
    int w = model.getWidth(), h = model.getHeight();

    p.noStroke();
    p.fill(220,220,220,150);    
    drawIsoSquare(0, 0, w*2, h*2, l);
    
    drawGrid(l);
  }
  
  public void drawGridHoverFill(int h, PieceHover r)
  {    
    if (r != null)
    {
      p.fill(220,0,0,180);
      p.strokeWeight(1.0f);
      p.stroke(0);

      PieceOutline outline = r.outline;
      outline.renderIsometric(p, r.x, r.y, x, y - 1 - h*Brush.tileset.hOffset);
    }

  }
  
  public void drawGridHover(int h, PieceHover r)
  {    
    if (r != null)
    {
      p.strokeWeight(4.0f);
      p.noFill();
      p.stroke(180,0,0,220);

      PieceOutline outline = r.outline;
      outline.renderIsometric(p, r.x, r.y, x, y - 1 - h*Brush.tileset.hOffset);
    }
  }
  
  public void drawIsoSquare(int x, int y, int w, int h, int l)
  {
    int bx = this.x;
    int by = this.y - 1;
    
    Tileset ts = Brush.tileset;

    int fx1 = (int) (bx + (x - y)/2.0f * ts.xOffset);
    int fy1 = (int) (by + (x + y)/2.0f * ts.yOffset);

    int fx2 = (int) (bx + ((x+w) - (y+h))/2.0f * ts.xOffset);
    int fy2 = (int) (by + ((x+w) + (y+h))/2.0f * ts.yOffset);

    int fx3 = (int) (bx + (x - (y+h))/2.0f * ts.xOffset);
    int fy3 = (int) (by + (x + (y+h))/2.0f * ts.yOffset);

    int fx4 = (int) (bx + ((x+w) - y)/2.0f * ts.xOffset);
    int fy4 = (int) (by + ((x+w) + y)/2.0f * ts.yOffset);
    
    fy1 -= Brush.tileset.hOffset*l;
    fy2 -= Brush.tileset.hOffset*l;
    fy3 -= Brush.tileset.hOffset*l;
    fy4 -= Brush.tileset.hOffset*l;
    
    p.beginShape();
    p.vertex(fx1, fy1);
    p.vertex(fx3, fy3);
    p.vertex(fx2, fy2);
    p.vertex(fx4, fy4);
    p.vertex(fx1, fy1);
    p.endShape();
  }
  
  public void drawGridLine(int x1, int y1, int x2, int y2, int h)
  {
    int bx = this.x;
    int by = this.y - 1;
    
    int fx1 = (int) (bx + (x1 - y1)/2.0f * Brush.tileset.xOffset);
    int fy1 = (int) (by + (x1 + y1)/2.0f * Brush.tileset.yOffset);
    int fx2 = (int) (bx + (x2 - y2)/2.0f * Brush.tileset.xOffset);
    int fy2 = (int) (by + (x2 + y2)/2.0f * Brush.tileset.yOffset);
    
    fy1 -= Brush.tileset.hOffset*h;
    fy2 -= Brush.tileset.hOffset*h;

    p.line(fx1, fy1, fx2, fy2);
  }

  @Override
  public boolean isInside(int x, int y)
  {
    LevelStackView levelStackView = parent().at(0);

    Level locked = levelStackView.getLocked();
    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += x;
      bounds.y += y;
      
      return bounds.contains(x, y);
    }
    else
      return false;
    /*
     *     Tileset ts = Brush.tileset;

    final int width = (model.getWidth()+model.getHeight())*ts.xOffset;
    final int baseHeight = (model.getHeight()+model.getWidth())*ts.yOffset;
    final int layersHeight = model.levelCount()*ts.hOffset;

    boolean isInside = x >= ox - width/2 && x < ox + width/2 &&
        y >= oy - (baseHeight/2 + layersHeight) && y < oy + baseHeight/2;
    
    System.out.println("Inside: "+isInside+" "+ox+" "+oy+" "+ x+" "+y);
        
    return isInside;*/
  }

  @Override
  public void mouseReleased(int x, int y, int b)
  {
    LevelStackView levelStackView = parent().at(0);

    Level locked = levelStackView.getLocked();
    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += x;
      bounds.y += y;
      
      PieceHover hover = levelStackView.hover();

      if (bounds.contains(x, y) && hover != null)
      {        
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
  
  
  int lockX = -1, lockY = -1;

  @Override
  public void mouseDragged(int x, int y, int b)
  {
    if (b == PConstants.RIGHT)
    {
      if (!draggingLock())
      {        
        LevelStackView levelStackView = parent().at(0);
        Level locked = levelStackView.getLocked();

        if (locked == null)
        {
          Rectangle bounds = PieceDrawer.computeRealBounds(model, false);
          bounds.x += x;
          bounds.y += y;
          
          //TODO: redundant with isInside called for Drawable management?
          if (bounds.contains(x, y))
          {
            dragging = true;
            lockX = x;
            lockY = y;
          }
          else
            return;
          
        }
        else
          return;
      }
      
      /*TODO: ????
      this.baseX += x - lockX;
      this.baseY += y - lockY;
      */
      
      lockX = x;
      lockY = y;
      
      p.redraw();
    }
  }

  @Override
  public void mouseMoved(int x, int y)
  {
    LevelStackView levelStackView = parent().at(0);
    
    Level locked = levelStackView.getLocked();

    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += x;
      bounds.y += y;
      
      PieceHover hover = levelStackView.hover();

      //TODO: redundant with isInside called for Drawable management?
      if (bounds.contains(x, y))
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
        
        //TODO: half steps not working
        PieceHover newHover = new PieceHover(ix*2, iy*2, Brush.type().outline());

        /* TODO: no need to set it on each mouseMoved since it only changes with change of piece */
        if (ix >= 0 && ix+bw <= model.getWidth() && iy >= 0 && iy+bh <= model.getHeight())
        {
          if (hover == null || !newHover.equals(hover))
          {            
            levelStackView.setHover(newHover);
          }
        }
        else
          levelStackView.setHover(null);       
      }
    }
  }

  @Override
  public void mouseExited()
  {
    
  }

  @Override
  public void mouseWheelMoved(int x, int y, int v)
  {
    LevelStackView levelStackView = parent().at(0);

    Level locked = levelStackView.getLocked();

    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += x;
      bounds.y += y;
      
      //TODO: redundant with isInside called for Drawable management?
      if (bounds.contains(x, y))
      {
        if (v > 0 && locked.previous() != null)
        {
          levelStackView.setLocked(locked.previous());
          p.mouseMoved();
        }
        else if (v < 0 && locked.next() != null)
        {
          levelStackView.setLocked(locked.next());
          p.mouseMoved();
        }   
      }
    }
  }
}
