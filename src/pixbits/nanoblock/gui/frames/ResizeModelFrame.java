package pixbits.nanoblock.gui.frames;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.data.Attach;
import pixbits.nanoblock.tasks.ModelOperations;

public class ResizeModelFrame extends BaseDialog implements ModelOperationFrame
{
  private static class AttachSide
  {
    public final Attach hor;
    public final Attach ver;
    public final JToggleButton button;
    
    AttachSide(JToggleButton button, Attach hor, Attach ver)
    {
      this.button = button;
      this.hor = hor;
      this.ver = ver;
    }
  }
  
  private final Icon icons[][] = {
      { Icon.ARROW_UP_LEFT, Icon.ARROW_UP, Icon.ARROW_UP_RIGHT},
      { Icon.ARROW_LEFT, null, Icon.ARROW_RIGHT},
      { Icon.ARROW_DOWN_LEFT, Icon.ARROW_DOWN, Icon.ARROW_DOWN_RIGHT}
  };
  
  private final ButtonGroup group;
  private final AttachSide[][] buttons;
  
  private final JLabel labelSize;
  private final JLabel labelBounds;
  private final JTextField fieldSize;
  private final JTextField fieldBounds;
  
  private final JTextField width, height;
  
  private final JCheckBox cropToBounds;
  private final JCheckBox keepCentered;
  
  private Model model;
  private Rectangle bounds = null;
  
  public void show(Model model)
  {
    bounds = model.computeBound();
    fieldSize.setText(model.getWidth()+"x"+model.getHeight());
    fieldBounds.setText((bounds.width-bounds.x)+"x"+(bounds.height-bounds.y));
    width.setText(""+model.getWidth());
    height.setText(""+model.getHeight());
    this.model = model;
    
    setLocationRelativeTo(Main.mainFrame);
    setVisible(true);
  }
  
  public ResizeModelFrame()
  {
    super(Main.mainFrame, "Resize Model", new String[] {"Cancel", "Resize"});
    
    JPanel gridPanel = new JPanel(new GridLayout(3,3));
    gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

    top.setLayout(new GridLayout(2,2));
    top.setBorder(BorderFactory.createTitledBorder("Current Size"));
    
    middle.setBorder(BorderFactory.createTitledBorder("Resize To"));

    group = new ButtonGroup();
    buttons = new AttachSide[3][3];
    
    for (int y = 0; y < 3; ++y)
    {
      for (int x = 0; x < 3; ++x)
      {
        JToggleButton button = new JToggleButton();
        if (icons[y][x] != null) button.setIcon(icons[y][x].icon());
        button.setPreferredSize(new Dimension(40,40));
        button.addActionListener(listener);
        
        group.add(button);
        buttons[y][x] = new AttachSide(button, Attach.horizontal[x], Attach.vertical[y]);
        
        if (x == 1 && y == 1)
          button.setSelected(true);
        
        gridPanel.add(button);
      }
    }

    labelSize = new JLabel("Model Size");
    labelBounds = new JLabel("Model Bounds");
        
    fieldSize = new JTextField("1x1");
    fieldSize.setHorizontalAlignment(JTextField.CENTER);
    fieldSize.setEditable(false);
    
    fieldBounds = new JTextField("1x1");
    fieldBounds.setHorizontalAlignment(JTextField.CENTER);
    fieldBounds.setEditable(false);
    
    top.add(labelSize);
    top.add(fieldSize);
    top.add(labelBounds);
    top.add(fieldBounds);

    width = new JTextField(4);
    height = new JTextField(4);
    
    JPanel panel1 = new JPanel();
    
    panel1.add(width);
    panel1.add(new JLabel("x"));
    panel1.add(height);
    
    middle.setLayout(new BoxLayout(middle, BoxLayout.PAGE_AXIS));
    middle.add(panel1);
    
    JPanel panel2 = new JPanel(new GridLayout(2,1));
    cropToBounds = new JCheckBox("Crop to bounds");
    keepCentered = new JCheckBox("Keep model centered");
    cropToBounds.setAlignmentX(0.0f);
    keepCentered.setAlignmentX(0.0f);
    cropToBounds.addActionListener(listener);
    keepCentered.addActionListener(listener);
    
    panel2.add(cropToBounds);
    panel2.add(keepCentered);
    
    middle.add(panel2);
    middle.add(gridPanel);
    
    finalizeDialog();
  }
  
  @Override
  protected void buttonClicked(JButton button)
  {
    if (button == cancel)
    {
      ResizeModelFrame.this.setVisible(false);
      buttons[1][1].button.setSelected(true);
    }
    else if (button == execute)
    {
      int nw = 0, nh = 0;
      
      try
      {
        nw = Integer.parseInt(width.getText());
        nh = Integer.parseInt(height.getText());
      }
      catch (NumberFormatException ee)
      {
        Dialogs.showErrorDialog(ResizeModelFrame.this, "Error", "Please insert a valid number!");
        return;
      }
      
      Attach va = Attach.NONE;
      Attach ha = Attach.NONE;
      
      for (int y = 0; y < 3; ++y)
        for (int x = 0; x < 3; ++x)
          if (buttons[y][x].button.isSelected())
          {
            va = buttons[y][x].ver;
            ha = buttons[y][x].hor;
          }
      
      if (!new ModelOperations.Resize(model, bounds, nw, nh, va, ha, keepCentered.isSelected()).execute())
        Dialogs.showErrorDialog(ResizeModelFrame.this, "Error", "Final size must be at least equal to model bounds!");
      else
      {
        Main.sketch.redraw();
        ResizeModelFrame.this.setVisible(false);
      }
    }
  }
  
  
  private final ActionListener listener = new ActionListener() {
    public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() instanceof JCheckBox)
      {
        JCheckBox box = (JCheckBox)e.getSource();
        
        if (box == cropToBounds)
        {
          if (box.isSelected())
          {
            width.setText(""+(bounds.width-bounds.x));
            height.setText(""+(bounds.height-bounds.y));
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
