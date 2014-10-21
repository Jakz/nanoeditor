package pixbits.nanoblock.gui.frames;

import pixbits.nanoblock.*;
import pixbits.nanoblock.gui.Sketch;
import pixbits.nanoblock.gui.menus.Menus;
import pixbits.nanoblock.tasks.Tasks;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class MainFrame extends JFrame implements MouseWheelListener
{
  private static final long serialVersionUID = 1L;
  
  public MainFrame()
  {	
  	super("Nanoblock Architect v0.1");

    setLayout(new BorderLayout());
    Sketch embed = Main.sketch;
    add(embed, BorderLayout.CENTER);
    add(Menus.buildEditorToolbar(), BorderLayout.NORTH);
    embed.init();

    this.addWindowListener(windowListener);
    this.addMouseWheelListener(this);
    
    this.setJMenuBar(Menus.buildMenu());
    
    	    
    pack();
  }
  
  private final WindowListener windowListener = new WindowAdapter() { 
    public void windowClosing(WindowEvent e)
    { 
      Tasks.closeEditor();
    }
  };

  public void mouseWheelMoved(MouseWheelEvent e)
  {
    Main.sketch.mouseWheelMoved(e.getWheelRotation());
  }
}