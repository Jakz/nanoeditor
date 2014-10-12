package pixbits.nanoblock.misc;

import java.util.*;

public class Settings
{
  private boolean[] bvalues = new boolean[Setting.values().length];
  
  public void set(Setting setting) { set(setting, true); }
  public void unset(Setting setting) { set(setting, false); }
  public void toggle(Setting setting) { set(setting, !bvalues[setting.ordinal()]); }
  public void set(Setting setting, boolean value) { bvalues[setting.ordinal()] = value; }
  
  public boolean get(Setting setting) { return bvalues[setting.ordinal()]; }
  
  Settings()
  {
    set(Setting.DRAW_CAPS, true);
    set(Setting.HALF_STEPS_ENABLED, true);
    set(Setting.SHOW_PIECE_ORDER, false);
  }
  
  
  public String pathLibrary = "./library";

  public final static Settings values = new Settings();
}
