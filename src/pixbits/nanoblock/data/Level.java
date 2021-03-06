package pixbits.nanoblock.data;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import pixbits.nanoblock.files.Log;
import pixbits.nanoblock.gui.PieceDrawer;
import pixbits.nanoblock.gui.SpriteBatch;

public class Level implements Iterable<Piece>
{
  final List<Piece> pieces;

  private final Model model;
  
  private Level previous;
  private Level next;
  
  private boolean dirty; //TODO: not logic related, move

  Level(Model model, Level previous)
  {
    this(model);
    this.previous = previous;
    
    this.dirty = false;
  }
  
  Level(Model model)
  {
    this.model = model;
    pieces = new ArrayList<Piece>();
  }
  
  void setNext(Level next)
  {
    this.next = next;
  }
  
  void setPrevious(Level previous)
  {
    this.previous = previous;
  }
  
  public Level previous() { return previous; }
  public Level next() { return next; }
  
  public boolean dirty() { return dirty; }
  public void setDirty(boolean dirty) { this.dirty = dirty; }
  
  void removeAllPieces()
  {
    Set<Piece> pieces = new HashSet<Piece>(this.pieces);
    removePieces(pieces);
  }
  
  void removePiece(Piece piece)
  {    
    pieces.remove(piece);
    
    Log.i("Removing piece "+piece);
    
    /* add caps to current level */
    if (previous != null && piece.type != PieceType.CAP)
    {
      Set<Piece> coveredPieces = new HashSet<>();
      
      /* find all pieces covered by the one that is going to be removed */
      for (int i = piece.x-1; i <= piece.x+piece.type.width*2; ++i)
        for (int j = piece.y-1; j <= piece.y+piece.type.height*2; ++j)
        {
          if (i >= 0 && j >= 0)
          {
            Piece piece2 = previous.pieceAt(i, j);
            if (piece2 != null && piece2.type != PieceType.CAP)
              coveredPieces.add(piece2);
          }
        }
      
      //TODO: verify behavior
      coveredPieces.stream().forEach(coveredPiece -> {
       coveredPiece.type.forEachCap((i, j) -> {      
         int xx = coveredPiece.x + i, yy = coveredPiece.y + j;
         if (shouldPlaceCapFromBelow(xx, yy))
         {
           Log.i("Adding cap for removal at "+xx+", "+yy+" thanks to "+coveredPiece);
           addPiece(new Piece(PieceType.CAP, coveredPiece.color, xx, yy));
         }
       });
          
      });
    }
    
    /* remove caps to next level */
    if (next != null && piece.type != PieceType.CAP)
    {
      next.removeCaps(piece.x, piece.y, piece.type);
    }
    
    dirty = true;
  }
    
  void addPiece(Piece piece)
  {
    //System.out.println("Place at "+piece.x+","+piece.y+"   "+piece);
    
    /* remove caps to current level */
    if (piece.type != PieceType.CAP && piece.color.opaque)
    {
      Iterator<Piece> lpieces = iterator();
      while (lpieces.hasNext())
      {
        Piece piece2 = lpieces.next();
        if (piece2.type == PieceType.CAP)
          if (piece2.x >= piece.x-1 && piece2.x < piece.x+piece.type.width*2 && piece2.y >= piece.y-1 && piece2.y < piece.y+piece.type.height*2)
            lpieces.remove();
      }
    }

    //System.out.println("Add "+piece);
    pieces.add(piece);    
    
    /* add caps to next level */
    if (next != null && piece.type != PieceType.CAP)
    {
      piece.type.forEachCap((i, j) -> {
        
        if (next.shouldPlaceCapFromBelow(piece.x+i,piece.y+j))
          next.addPiece(new Piece(PieceType.CAP, piece.color, piece.x+i, piece.y+j));
      });
    }
    
    dirty = true;
  }

  public boolean shouldPlaceCapFromBelow(int x, int y)
  {
    Piece piece = pieceAt(x, y);
    return piece == null || !piece.color.opaque;
  }
  
  public boolean isFreeAt(int x, int y)
  {
    Piece piece = pieceAt(x,y);       
    return piece == null || piece.type == PieceType.CAP;
  }
  
  public boolean canPlace(PieceType type, int x, int y)
  {    
    return type.parts().allMatch(p -> {
      int px = x + p.x*2;
      int py = y + p.y*2;
            
      return isFreeAt(px,py) && isFreeAt(px+1, py) && isFreeAt(px, py+1) && isFreeAt(px+1, py+1);
    });
  }
  
  public void removeCaps(int x, int y, PieceType type)
  {
    //TODO: inefficient
    type.forEachCap((cx, cy) -> {
      Piece piece = pieceAt(x + cx, y + cy);
      if (piece != null && piece.type == PieceType.CAP)
      {
        pieces.remove(piece);
        dirty = true;
      }
    });
  }
  
  public Piece pieceAt(int x, int y)
  {
    Iterator<Piece> pieces = iterator();
    
    while (pieces.hasNext())
    {
      Piece piece = pieces.next();

      /* coarse phase */
      if (x >= piece.x && x < piece.x+piece.type.maxWidth()*2 && y >= piece.y && y < piece.y+piece.type.maxHeight()*2)
      {
        /* fine phase*/
        if (piece.type.isConvex())
          return piece;
        else
        {
          /* TODO: not efficient */
          AtomicBoolean found = new AtomicBoolean(false);
          
          piece.type.forEachPart(p -> {
            int bx = piece.x + p.x*2, by = piece.y + p.y*2;
            if (!found.get() && x >= bx && x < bx + 2 && y >= by && y < by + 2)
              found.set(true);
          });
          
          return found.get() ? piece : null;
        }
      }
    }

    return null;
  }
  
  public Set<Piece> findAllCaps()
  {
    return pieces.stream()
      .filter(p -> p.type == PieceType.CAP)
      .collect(Collectors.toSet());
  }
  
  public Set<Piece> findAllRealPieces()
  {
    return pieces.stream()
      .filter(p -> p.type != PieceType.CAP)
      .collect(Collectors.toSet());
  }
  
  Set<Piece> findAllPieces() { return new HashSet<Piece>(pieces); }
  
  public void removePieces(Set<Piece> pieces)
  {
    for (Piece p : pieces)
      removePiece(p);
  }
  
  public void addPieces(Set<Piece> pieces)
  {
    for (Piece p : pieces)
      addPiece(p);
  }
  
  public int count()
  {
    return pieces.size();
  }
  
  public Iterator<Piece> iterator()
  {
    return pieces.iterator();
  }
  
  public void clear()
  {
    pieces.clear();
    dirty = true;
  }
  
  public int indexOfPiece(Piece piece)
  {
    int i = 0;
    for (Piece p : pieces)
    {
      if (p.equals(piece)) break;
      ++i;
    }
   
    return i;
  }
}
