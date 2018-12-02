package pixbits.nanoblock.gui;

import java.awt.Rectangle;
import java.util.Iterator;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Level;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.data.Piece;
import pixbits.nanoblock.data.PieceType;
import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.misc.Setting;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.ModelOperations;
import processing.core.PConstants;

public class IsometricView extends Drawable
{  
  private final Model model;
  
  private int hoveredIndex;
  
  IsometricView(Sketch p, Model model)
  {
    super(p, 0, 0);
    
    Rectangle bounds = PieceDrawer.computeLayerBounds(model, 0);
        
    ox = p.levelStackView.gridWidth() + p.levelStackView.scrollbar.width() + GUI.margin + bounds.width/2;
    oy = p.colorPaletteView.y() - GUI.margin - bounds.width/2;
    
    this.model = model;
    this.hoveredIndex = -1;
  }
  
  public void dispose() { }

  
  public void draw()
  {
    Level hovered = p.levelStackView.getHoveredLevel();
    hoveredIndex = -1;
    Rectangle hoverRect = p.levelStackView.hover();
    
    if (p.levelStackView.getLocked() != null)
      hovered = p.levelStackView.getLocked();
    
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
          drawGridHover(hoveredIndex, hoverRect);
          drawGridHoverFill(hoveredIndex, hoverRect);
        }
        else if (Settings.values.getHoverPiece() == Setting.HoverPiece.BACK_STROKE)
        {
          drawGridHover(hoveredIndex, hoverRect);
        }
        
      }
  
      for (Piece piece : level)
      {
        if (piece.type != PieceType.CAP || Settings.values.get(Setting.DRAW_CAPS))
        {
          PieceDrawer.drawPiece(p, ox, oy, piece, piece.x, piece.y, l, level);
        }
      }
    }
    

    if (Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE_WITH_BACK_FILL || Settings.values.getHoverPiece() == Setting.HoverPiece.FRONT_STROKE)
    {
      if (hoveredIndex != -1)
        drawGridHover(hoveredIndex, hoverRect);
    }
    
    if (hoveredIndex != -1)
    {
      p.strokeWeight(2.0f);

      Rectangle bounds = PieceDrawer.computeLayerBoundsWithPiece(model, hoveredIndex);
      
      if (bounds != null)
        p.rect(255, 0, 0, 200,  ox + bounds.x, oy + bounds.y, bounds.width, bounds.height);

      bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);

      if (bounds != null)
        p.rect(255, 128, 0, 200,  ox + bounds.x, oy + bounds.y, bounds.width, bounds.height);
      
      bounds = PieceDrawer.computeRealBounds(model, hoveredIndex, Settings.values.get(Setting.DRAW_CAPS));
      
      if (bounds != null)
        p.rect(0, 0, 180, 200,  ox + bounds.x, oy + bounds.y, bounds.width, bounds.height);
      
      bounds = PieceDrawer.computeRealBounds(model, Settings.values.get(Setting.DRAW_CAPS));
      
      if (bounds != null)
        p.rect(0, 180, 0, 200,  ox + bounds.x, oy + bounds.y, bounds.width, bounds.height);
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
  
  public void drawGridHoverFill(int h, Rectangle r)
  {    
    if (r != null)
    {
      p.fill(220,0,0,180);
      p.strokeWeight(1.0f);
      p.stroke(0);
      this.drawIsoSquare(r.x, r.y, r.width*2, r.height*2, h);
    }

  }
  
  public void drawGridHover(int h, Rectangle r)
  {    
    if (r != null)
    {
      p.strokeWeight(4.0f);
      p.stroke(180,0,0,220);
    
      drawGridLine(r.x, r.y, r.x+r.width*2, r.y, h);
      drawGridLine(r.x, r.y+r.height*2, r.x+r.width*2, r.y+r.height*2, h);
      drawGridLine(r.x, r.y, r.x, r.y+r.height*2, h);
      drawGridLine(r.x+r.width*2, r.y, r.x+r.width*2, r.y+r.height*2, h);
    }
  }
  
  public void drawIsoSquare(int x, int y, int w, int h, int l)
  {
    int bx = this.ox;
    int by = this.oy - 1;
    
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
    int bx = this.ox;
    int by = this.oy - 1;
    
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
    Level locked = p.levelStackView.getLocked();
    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += ox;
      bounds.y += oy;
      
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
    Level locked = p.levelStackView.getLocked();
    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += ox;
      bounds.y += oy;
      
      Rectangle hover = p.levelStackView.hover();

      if (bounds.contains(x, y) && hover != null)
      {        
        if (!locked.isFreeAt(hover.x, hover.y))
        {
          new ModelOperations.Remove(model, hoveredIndex, hover.x, hover.y).execute();
          p.levelStackView.clearToBeDeleted();
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
        Level locked = p.levelStackView.getLocked();

        if (locked == null)
        {
          Rectangle bounds = PieceDrawer.computeRealBounds(model, false);
          bounds.x += ox;
          bounds.y += oy;
          
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
    Level locked = p.levelStackView.getLocked();

    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += ox;
      bounds.y += oy;
      
      Rectangle hover = p.levelStackView.hover();

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

        
        if (ix >= 0 && ix+bw <= model.getWidth() && iy >= 0 && iy+bh <= model.getHeight())
        {
          if (hover == null || hover.x != ix*2 || hover.y != iy*2 || hover.width != bw || hover.height != bh)
          {            
            p.levelStackView.setHover(new Rectangle(ix*2, iy*2, bw, bh));
          }
        }
        else
          p.levelStackView.setHover(null);       
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
    Level locked = p.levelStackView.getLocked();

    if (locked != null)
    {
      Rectangle bounds = PieceDrawer.computeLayerBounds(model, hoveredIndex);
      bounds.x += ox;
      bounds.y += oy;
      
      //TODO: redundant with isInside called for Drawable management?
      if (bounds.contains(x, y))
      {
        if (v > 0 && locked.previous() != null)
        {
          p.levelStackView.setLocked(locked.previous());
          p.mouseMoved();
        }
        else if (v < 0 && locked.next() != null)
        {
          p.levelStackView.setLocked(locked.next());
          p.mouseMoved();
        }   
      }
    }
  }
}