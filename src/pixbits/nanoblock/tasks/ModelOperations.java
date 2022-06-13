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
    
    @Override
    public String toString()
    {
      return String.format("Shift(%s)", direction);
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
    
    @Override
    public String toString()
    {
      return String.format("Rotate(%s)", direction);
    }
  }
  
  public static class Remove extends UndoableLightTask
  {
    private final Piece oldPiece;
    private final int level;
    
    public Remove(Model model, int level, int x, int y)
    {
      super(model);
     
      Piece piece = model.levelAt(level).pieceAt(x, y);
      oldPiece = piece != null ? piece.dupe() : null;
      this.level = level;
    }
    
    @Override
    protected boolean execute(Model model)
    {
      if (oldPiece != null)
        model.removePiece(model.levelAt(level), oldPiece);
      
      return true;
    }
    
    @Override 
    protected UndoableLightTask reverseAction()
    {
      Place reverse = new Place(model, oldPiece, level);
      reverse.isRealAction = false;
      return reverse;
    }
    
    @Override
    public String toString()
    {
      return String.format("Remove(%s, %s, %d:%d:%d)", oldPiece.type, oldPiece.color, oldPiece.x, oldPiece.y, level);
    }
  }
  
  public static class Place extends UndoableLightTask
  {
    private final Piece piece;
    private final int level;
    
    public Place(Model model, Piece piece, int level)
    {
      super(model);
      this.piece = piece.dupe();
      this.level = level;
    }
    
    public Place(Model model, int level, PieceType type, PieceColor color, int x, int y)
    {
      this(model, new Piece(type, color, x, y), level);
    }
    
    @Override
    protected boolean execute(Model model)
    {
      model.addPiece(model.levelAt(level), piece.type, piece.color, piece.x, piece.y);
      return true;
    }
    
    @Override 
    protected UndoableLightTask reverseAction()
    {
      Remove reverse = new Remove(model, level, piece.x, piece.y);
      reverse.isRealAction = false;
      return reverse;
    }
    
    @Override
    public String toString()
    {
      return String.format("Place(%s, %s, %d:%d:%d)", piece.type, piece.color, piece.x, piece.y, level);
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
        piece.type.forEachCap((x,y) -> {
          Piece cap = next.pieceAt(piece.x+x, piece.y+y);
          if (cap.type == PieceType.CAP)
            cap.color = piece.color;
        });
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
    final Attach va;
    final Attach ha;
    final boolean keepCentered;
    final Rectangle bounds;
    
    
    public Resize(Model model, Rectangle bounds, int width, int height, Attach va, Attach ha, boolean keepCentered)
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
