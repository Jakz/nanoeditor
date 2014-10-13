package pixbits.nanoblock.misc;

import java.util.*;

public class Settings
{
  private boolean[] bvalues = new boolean[Setting.values().length];
  private String[] paths = new String[Setting.Path.values().length];
  
  public void set(Setting setting) { set(setting, true); }
  public void unset(Setting setting) { set(setting, false); }
  public void toggle(Setting setting) { set(setting, !bvalues[setting.ordinal()]); }
  public void set(Setting setting, boolean value) { bvalues[setting.ordinal()] = value; }
  public boolean get(Setting setting) { return bvalues[setting.ordinal()]; }
  
  public void setPath(Setting.Path path, String value) { paths[path.ordinal()] = value; }
  public String getPath(Setting.Path path) { return paths[path.ordinal()]; }
    
  Setting.HoverPiece hoverPiece;
  
  public Setting.HoverPiece getHoverPiece() { return hoverPiece; }

  Settings()
  {
    Settings.values = this;
    
    set(Setting.DRAW_CAPS, true);
    set(Setting.HALF_STEPS_ENABLED, true);
    set(Setting.SHOW_PIECE_ORDER, false);
    
    set(Setting.VIEW_SHOW_LAYER_GRID, true);
    set(Setting.VIEW_LAYER_GRID_ALPHA, true);
    
    Setting.HoverSetter.INSTANCE.set(Setting.HoverPiece.DISABLE);
    
    setPath(Setting.Path.LIBRARY, "./library");
  }
  
  
  public static Settings values = new Settings();
}
