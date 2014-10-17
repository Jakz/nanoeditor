package pixbits.nanoblock.misc;

import com.google.gson.*;

import java.util.*;
import java.io.*;

import pixbits.nanoblock.files.FileUtils;

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
    Settings.values = this; //TODO: ugly but required for static initializer of Setting$HoverSetter
    
    set(Setting.DRAW_CAPS, true);
    set(Setting.HALF_STEPS_ENABLED, true);
    set(Setting.SHOW_PIECE_ORDER, false);
    
    set(Setting.VIEW_SHOW_LAYER_GRID, true);
    set(Setting.VIEW_LAYER_GRID_ALPHA, true);
    
    set(Setting.VIEW_SHOW_GRID_LINES, true);
    set(Setting.VIEW_SHOW_HALF_GRID_POINTS, true);
    
    Setting.HoverSetter.INSTANCE.set(Setting.HoverPiece.FRONT_STROKE_WITH_BACK_FILL);
    
    setPath(Setting.Path.LIBRARY, "./library");
    setPath(Setting.Path.CACHE, "./cache");
  }
  
  
  
  
  
  
  public static Settings values;
  
  public static final String SETTINGS_PATH = "./settings/settings.json";
  
  public static void loadSettings() throws FileNotFoundException, IOException
  {
    if (new File(SETTINGS_PATH).exists())
      Settings.values = FileUtils.readJson(SETTINGS_PATH, Settings.class);
    else
      Settings.values = new Settings();
  }
  
  public static void saveSettings() throws IOException
  {
    FileUtils.writeJson(SETTINGS_PATH, Settings.class, Settings.values);
  }

}
