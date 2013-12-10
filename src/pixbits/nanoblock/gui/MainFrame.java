package pixbits.nanoblock.gui;

import pixbits.nanoblock.*;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame
{
  public MainFrame()
  {	
  	super("Nanoblock Architect v0.1");

    setLayout(new BorderLayout());
    Sketch embed = Main.sketch;
    add(embed, BorderLayout.CENTER);
    embed.init();

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
    pack();
    setVisible(true);
    setLocation(100, 100);
  }
}