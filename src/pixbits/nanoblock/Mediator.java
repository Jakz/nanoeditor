package pixbits.nanoblock;

import pixbits.nanoblock.files.Library;

public class Mediator
{
  private Library library = new Library();
  
  
  public Library library() { return library; }
  public int librarySize() { return library.getModels().size(); }
}
