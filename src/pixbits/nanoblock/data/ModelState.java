package pixbits.nanoblock.data;

import java.util.*;

public class ModelState implements Iterable<ModelState.PieceState>
{
  static class PieceState
  {
    final int level;
    final int x;
    final int y;
    final PieceColor color;
    final PieceType type;
    
    PieceState(int level, int x, int y, PieceType type, PieceColor color)
    {
      this.level = level;
      this.x = x;
      this.y = y;
      this.type = type;
      this.color = color;
    }
  }
  
  private final int width;
  private final int height;
  private final int levels;
  private final Set<PieceState> pieces;
  
  ModelState(int width, int height, int levels)
  {
    this.width = width;
    this.height = height;
    this.levels = levels;
    this.pieces = new HashSet<PieceState>();
  }
  
  public int getWidth() { return width; }
  public int getHeight() { return height; }
  public int getLevels() { return levels; }
  
  public void addPiece(int level, Piece piece)
  {
    pieces.add(new PieceState(level, piece.x, piece.y, piece.type, piece.color));
  }
  
  public Iterator<ModelState.PieceState> iterator() { return pieces.iterator(); }
  
}
