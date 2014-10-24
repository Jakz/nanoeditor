package pixbits.nanoblock.gui.frames;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import pixbits.nanoblock.Main;

public abstract class BaseDialog extends JDialog
{
  private final JPanel mainPanel;
  
  private final JPanel subPanels[];
  private final JButton lowButtons[];
  
  final protected JPanel top, middle, low;
  final protected JButton cancel, execute;
     
  BaseDialog(JFrame parent, String title, String[] lowerButtonNames)
  {
    super(parent, title, Dialog.ModalityType.APPLICATION_MODAL);

    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
    this.getContentPane().setLayout(new BorderLayout());
   
    subPanels = new JPanel[] { new JPanel(), new JPanel(), new JPanel() };
    top = subPanels[0];
    middle = subPanels[1];
    low = subPanels[2];
    
    if (lowerButtonNames != null)
    {
      //subPanels[2].setLayout(new GridLayout(1, lowerButtonNames.length));
      subPanels[2].setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.NONE;
      c.gridy = 0;
      c.anchor = GridBagConstraints.PAGE_END;
      
      lowButtons = new JButton[lowerButtonNames.length];
      
      for (int i = 0; i < lowButtons.length; ++i)
      {
        c.gridx = i;
        lowButtons[i] = new JButton(lowerButtonNames[i]);
        lowButtons[i].addActionListener(listener);
        subPanels[2].add(lowButtons[i], c);
      }
      
      cancel = lowButtons[0];
      execute = lowButtons[1];
    }
    else
    {
      lowButtons = null;
      cancel = null;
      execute = null;
    }
        
    this.add(mainPanel);
    this.add(subPanels[0], BorderLayout.NORTH);
    this.add(subPanels[1], BorderLayout.CENTER);
    this.add(subPanels[2], BorderLayout.SOUTH);
  }
  
  
  JButton getButton(int index) { return lowButtons[index]; }
  
  protected void finalizeDialog()
  {
    this.pack();
  }
  
  final protected ActionListener listener = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      buttonClicked((JButton)e.getSource());
    }
  };
  
  abstract protected void buttonClicked(JButton button);

}
