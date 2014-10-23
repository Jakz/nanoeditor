package pixbits.nanoblock.tasks;

import java.awt.Rectangle;
import java.util.Set;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;

public class ModelOperations
{
  public static OperationBuilder buildShift(Direction direction) { return new ShiftBuilder(direction); }
  public static OperationBuilder buildRotate(Direction direction) { return new RotateBuilder(direction); }

  
  private static class ShiftBuilder implements OperationBuilder
  {
    private final Direction direction;
    
    public ShiftBuilder(Direction direction){ this.direction = direction; }
    @Override
    public UndoableTask build(Model model) { return new Shift(model, direction); }
  }
  
  private static class RotateBuilder implements OperationBuilder
  {
    private final Direction direction;
    
    public RotateBuilder(Direction direction) { this.direction = direction; }
    @Override
    public UndoableTask build(Model model) { return new Rotate(model, direction); }
  }
  
  
  public static class Shift extends UndoableLightTask
  {
    private final Direction direction;
    
    public Shift(Model model, Direction direction)
    {
      super(model);
      this.direction = direction;
    }
    
    protected boolean execute(Model model)
    {
      if (model.canShift(direction))
      { 
        model.shift(direction);
        Main.sketch.redraw();
        return true;
      }
      
      return false;
    }
    
    protected UndoableLightTask reverseAction()
    {
      Shift reverse = new Shift(model, direction.flipped());
      reverse.isRealAction = false;
      return reverse;
    }
  }
  
  public static class Rotate extends UndoableLightTask
  {
    private final Direction direction;
    
    public Rotate(Model model, Direction direction)
    {
      super(model);
      this.direction = direction;
    }
    
    protected boolean execute(Model model)
    {
      model.rotate(direction);
      Main.sketch.redraw();
      return true;
    }
    
    protected UndoableLightTask reverseAction()
    {
      Rotate reverse = new Rotate(model, direction.flipped());
      reverse.isRealAction = false;
      return reverse;
    }
  }
  
  public static class ReplaceColor extends UndoableHeavyTask
  {
    private final PieceColor from;
    private final PieceColor to;
    private final Level level; //TODO: level is a reference, won't work for redo if level is changed
    private final Set<PieceType> types;
    
    public ReplaceColor(Model model, PieceColor from, PieceColor to, Set<PieceType> types)
    {
      super(model);
      this.from = from;
      this.to = to;
      this.types = types;
      this.level = null;
    }
    
    public ReplaceColor(Model model, Level level, PieceColor from, PieceColor to, Set<PieceType> types)
    {
      super(model);
      this.from = from;
      this.to = to;
      this.types = types;
      this.level = level;
    }
    
    private void recolorCaps(Piece piece, Level next)
    {
      if (next != null)
      {
        if (!piece.type.monocap)
        {        
          for (int i = 0; i < piece.type.width*2; i += 2)
            for (int j = 0; j < piece.type.height*2; j += 2)
            {
              Piece cap = next.pieceAt(piece.x+i, piece.y+j);
              if (cap.type == PieceType.CAP)
                cap.color = piece.color;
            }
        }
        else
        {
          int i = piece.type.width/2 + piece.x;
          int j = piece.type.height/2 + piece.y;
          Piece cap = next.pieceAt(i, j);
          if (cap.type == PieceType.CAP)
            cap.color = piece.color;
        }
      }
    }
    
    protected boolean execute(Model model)
    {
      if (level != null)
      {
        Level next = level.next();
        for (Piece p : level)
        {
          if (types.contains(p.type) && p.color == from)
          { 
            p.color = to;
            recolorCaps(p, next);
          }
        }
      }
      else
      {
        for (Level l : model)
        {
          Level next = l.next();
          for (Piece p : l)
          {
            if (types.contains(p.type) && p.color == from)
            {
              p.color = to;
              recolorCaps(p, next);
            }
          }
        }
      }
      
      return true;
    }
  }
  
  public static class Resize extends UndoableHeavyTask
  {
    final int width;
    final int height;
    final VerAttach va;
    final HorAttach ha;
    final boolean keepCentered;
    final Rectangle bounds;
    
    
    public Resize(Model model, Rectangle bounds, int width, int height, VerAttach va, HorAttach ha, boolean keepCentered)
    {
      super(model);
      this.bounds = bounds;
      this.width = width;
      this.height = height;
      this.va = va;
      this.ha = ha;
      this.keepCentered = keepCentered;
    }
    
    public boolean execute(Model model)
    {
      return model.resize(bounds, width, height, va, ha, keepCentered);
    }
  }
  
  public static class Reset extends UndoableHeavyTask
  {
    public Reset(Model model)
    {
      super(model);
    }
    
    public boolean execute(Model model)
    {
      model.clear();
      return true;
    }
  }
}
