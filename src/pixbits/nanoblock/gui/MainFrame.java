package pixbits.nanoblock.gui;

import pixbits.nanoblock.*;
import pixbits.nanoblock.files.ModelLoader;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainFrame extends JFrame implements WindowListener
{
  public MainFrame()
  {	
  	super("Nanoblock Architect v0.1");

    setLayout(new BorderLayout());
    Sketch embed = Main.sketch;
    add(embed, BorderLayout.CENTER);
    embed.init();

    this.addWindowListener(this);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
    pack();
    setVisible(true);
    setLocation(100, 100);
  }
  
  public void windowActivated(WindowEvent e) { }
  
  public void windowClosing(WindowEvent e)
  { 
    ModelLoader.saveModel(Main.sketch.model, "model.nblock");
  }
  
  
  public void windowClosed(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowGainedFocus(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowLostFocus(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }
  public void windowStateChanged(WindowEvent e) { }
}