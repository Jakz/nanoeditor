package pixbits.nanoblock.files;

import java.io.File;

import pixbits.nanoblock.data.ModelInfo;

public class LibraryModel
{
  public final ModelInfo info;
  public final File file;
  
  LibraryModel(ModelInfo info, File file) { this.info = info; this.file = file; }
}