package pixbits.nanoblock.tasks;

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
  
  
  public static class Shift extends UndoableTask
  {
    private final Direction direction;
    
    public Shift(Model model, Direction direction)
    {
      super(model);
      this.direction = direction;
    }
    
    protected void execute(Model model)
    {
      if (model.canShift(direction)) { model.shift(direction); Main.sketch.redraw(); }
    }
  }
  
  public static class Rotate extends UndoableTask
  {
    private final Direction direction;
    
    public Rotate(Model model, Direction direction)
    {
      super(model);
      this.direction = direction;
    }
    
    protected void execute(Model model)
    {
      model.rotate(direction);
      Main.sketch.redraw();
    }
  }
  
  public static class ReplaceColor extends UndoableTask
  {
    private final PieceColor from;
    private final PieceColor to;
    private final Level level;
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
    
    protected void execute(Model model)
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
    }
  }
}
