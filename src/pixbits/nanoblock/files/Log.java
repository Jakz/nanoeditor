package pixbits.nanoblock.files;

public class Log
{
  public static void e(String str) { System.out.println(str); }
  public static void i(String str) { System.out.println(str); }
  public static void e(Exception e) { System.out.println("Exception!"); e.printStackTrace(); }
}
