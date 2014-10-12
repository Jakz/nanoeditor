package pixbits.nanoblock.gui.frames;

import pixbits.nanoblock.*;
import pixbits.nanoblock.files.ModelLoader;
import pixbits.nanoblock.gui.Sketch;
import pixbits.nanoblock.tasks.Tasks;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class MainFrame extends JFrame implements WindowListener, MouseWheelListener
{
  private static final long serialVersionUID = 1L;
  
  public MainFrame()
  {	
  	super("Nanoblock Architect v0.1");

    setLayout(new BorderLayout());
    Sketch embed = Main.sketch;
    add(embed, BorderLayout.CENTER);
    embed.init();

    this.addWindowListener(this);
    this.addMouseWheelListener(this);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    Menus.buildMenu(this);
	    
    pack();
    setVisible(true);
    setLocation(100, 100);
  }
  
  public void windowActivated(WindowEvent e) { }
  
  public void windowClosing(WindowEvent e)
  { 
    Tasks.saveModel();
  }
  
  
  public void windowClosed(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowGainedFocus(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowLostFocus(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }
  public void windowStateChanged(WindowEvent e) { }
  
  public void mouseWheelMoved(MouseWheelEvent e)
  {
    Main.sketch.mouseWheelMoved(e.getWheelRotation());
  }
}