package pixbits.nanoblock.gui.frames;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ResizeModelFrame extends JFrame
{
  public static enum HorAttach { LEFT, NONE, RIGHT }
  public static enum VerAttach { TOP, NONE, BOTTOM }
  
  private static class AttachSide
  {
    public final HorAttach hor;
    public final VerAttach ver;
    public final JToggleButton button;
    
    AttachSide(JToggleButton button, HorAttach hor, VerAttach ver)
    {
      this.button = button;
      this.hor = hor;
      this.ver = ver;
    }
  }
  
  private final ButtonGroup group;
  private final AttachSide[][] buttons;
  private final JButton cancel, execute;
  
  public ResizeModelFrame()
  {
    JPanel gridPanel = new JPanel(new GridLayout(3,3));
    gridPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0), BorderFactory.createTitledBorder("Side to attach")));
    
    JPanel lowerPanel = new JPanel(new GridLayout(1,2));
    
    group = new ButtonGroup();
    buttons = new AttachSide[3][3];
    
    for (int y = 0; y < 3; ++y)
    {
      for (int x = 0; x < 3; ++x)
      {
        JToggleButton button = new JToggleButton();
        button.setPreferredSize(new Dimension(40,40));
        button.addActionListener(listener);
        
        group.add(button);
        buttons[y][x] = new AttachSide(button, HorAttach.values()[x], VerAttach.values()[y]);
        
        if (x == 1 && y == 1)
          button.setSelected(true);
        
        gridPanel.add(button);
      }
    }
    
    cancel = new JButton("Cancel");
    execute = new JButton("Resize");
    cancel.addActionListener(listener);
    execute.addActionListener(listener);
    
    lowerPanel.add(cancel);
    lowerPanel.add(execute);
    
    this.setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
    this.add(gridPanel);
    this.add(lowerPanel);
    
    
    setTitle("Resize Model");
    pack();
  }
  
  private final ActionListener listener = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      JButton src = (JButton)e.getSource();
      
      if (src == cancel)
      {
        ResizeModelFrame.this.setVisible(false);
        buttons[1][1].button.setSelected(true);
      }
    }
  };
}
