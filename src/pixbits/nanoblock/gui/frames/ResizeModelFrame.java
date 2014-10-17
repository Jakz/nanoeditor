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
  
  private final JLabel labelSize;
  private final JLabel labelBounds;
  private final JTextField fieldSize;
  private final JTextField fieldBounds;
  
  private final JTextField width, height;
  
  private final JCheckBox cropToBounds;
  private final JCheckBox keepCentered;
  
  private Dimension size = new Dimension(20,20);
  private Dimension bounds = new Dimension(14,12);
  
  public ResizeModelFrame()
  {
    JPanel gridPanel = new JPanel(new GridLayout(3,3));
    gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
    
    JPanel lowerPanel = new JPanel(new GridLayout(1,2));
    lowerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    
    JPanel sizePanel = new JPanel(new GridLayout(2,2));
    sizePanel.setBorder(BorderFactory.createTitledBorder("Current Size"));
    
    JPanel resizePanel = new JPanel();
    resizePanel.setBorder(BorderFactory.createTitledBorder("Resize To"));

    
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
    
    labelSize = new JLabel("Model Size");
    labelBounds = new JLabel("Model Bounds");
    
    fieldSize = new JTextField(size.width+"x"+size.height);
    fieldSize.setHorizontalAlignment(JTextField.CENTER);
    fieldSize.setEditable(false);
    
    fieldBounds = new JTextField(bounds.width+"x"+bounds.height);
    fieldBounds.setHorizontalAlignment(JTextField.CENTER);
    fieldBounds.setEditable(false);
    
    sizePanel.add(labelSize);
    sizePanel.add(fieldSize);
    sizePanel.add(labelBounds);
    sizePanel.add(fieldBounds);

    width = new JTextField(4);
    height = new JTextField(4);
    
    JPanel panel1 = new JPanel();
    
    panel1.add(width);
    panel1.add(new JLabel("x"));
    panel1.add(height);
    
    resizePanel.setLayout(new BoxLayout(resizePanel, BoxLayout.PAGE_AXIS));
    resizePanel.add(panel1);
    
    JPanel panel2 = new JPanel(new GridLayout(2,1));
    cropToBounds = new JCheckBox("Crop to bounds");
    keepCentered = new JCheckBox("Keep model centered");
    cropToBounds.setAlignmentX(0.0f);
    keepCentered.setAlignmentX(0.0f);
    cropToBounds.addActionListener(listener);
    keepCentered.addActionListener(listener);
    
    panel2.add(cropToBounds);
    panel2.add(keepCentered);
    resizePanel.add(panel2);
    resizePanel.add(gridPanel);
    
    this.setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
    this.add(sizePanel);
    this.add(resizePanel);
    this.add(lowerPanel);
    
    
    setTitle("Resize Model");
    pack();
  }
  
  private final ActionListener listener = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() instanceof JButton)
      {
        JButton src = (JButton)e.getSource();
        if (src == cancel)
        {
          ResizeModelFrame.this.setVisible(false);
          buttons[1][1].button.setSelected(true);
        }
      }
      else if (e.getSource() instanceof JCheckBox)
      {
        JCheckBox box = (JCheckBox)e.getSource();
        
        if (box == cropToBounds)
        {
          if (box.isSelected())
          {
            width.setText(""+bounds.width);
            height.setText(""+bounds.height);
            width.setEditable(false);
            height.setEditable(false);
            keepCentered.setEnabled(false);
            keepCentered.setSelected(false);
            
            for (int y = 0; y < 3; ++y)
              for (int x = 0; x < 3; ++x)
                buttons[y][x].button.setEnabled(false);
          }
          else
          {
            width.setEditable(true);
            height.setEditable(true);
            
            for (int y = 0; y < 3; ++y)
              for (int x = 0; x < 3; ++x)
                buttons[y][x].button.setEnabled(true);
            
            if (buttons[1][1].button.isSelected())
              keepCentered.setEnabled(true);
          }
        }
      }
      else if (e.getSource() instanceof JToggleButton)
      {
        JToggleButton src = (JToggleButton)e.getSource();
        
        if (src == buttons[1][1].button && src.isSelected())
          keepCentered.setEnabled(true);
        else
        {
          keepCentered.setEnabled(false);
          keepCentered.setSelected(false);
        }
      }
    }
  };
}
