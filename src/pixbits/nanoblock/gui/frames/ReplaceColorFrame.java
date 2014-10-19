package pixbits.nanoblock.gui.frames;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.util.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.*;
import pixbits.nanoblock.files.Library;
import pixbits.nanoblock.tasks.*;


public class ReplaceColorFrame extends JDialog
{
  private final JComboBox fromColor;
  private final JComboBox toColor;
  private final JCheckBox onlyLockedLayer;
  private final JList pieces;
  
  private final JButton cancel;
  private final JButton execute;
  
  private final Map<PieceColor, ImageIcon> colorIcons;
  
  private final PieceSelection[] types;
  
  private Model model;
  
  private class PieceSelection
  {
    final PieceType type;
    boolean isSelected;
    
    PieceSelection(PieceType type)
    {
      this.type = type;
      isSelected = true;
    }
    
    public String toString() { return type != null ? type.toString() : "All"; }
  }
  
  public ReplaceColorFrame()
  {
    super(Main.mainFrame, "Replace Color", Dialog.ModalityType.APPLICATION_MODAL);
    
    model = null;
    
    colorIcons = new HashMap<PieceColor, ImageIcon>();
    
    fromColor = new JComboBox();
    toColor = new JComboBox();
    
    for (PieceColor c : PieceColor.values())
    {
      fromColor.addItem(c);
      toColor.addItem(c);
    }
    
    fromColor.setRenderer(new ColorCellRenderer());
    toColor.setRenderer(new ColorCellRenderer());
    
    onlyLockedLayer = new JCheckBox("Limit to selected level");
    
    JPanel panel1 = new JPanel();
    panel1.add(fromColor);
    panel1.add(new JLabel(" -> "));
    panel1.add(toColor);
    panel1.setBorder(BorderFactory.createTitledBorder("Colors"));
    
    types = new PieceSelection[PieceType.count()+1];
    types[0] = new PieceSelection(null);
    for (int i = 0; i < PieceType.count(); ++i) types[i+1] = new PieceSelection(PieceType.at(i));
    
    pieces = new JList(types);
    pieces.setCellRenderer(new PieceCellRenderer());
    pieces.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    pieces.addMouseListener(listListener);
    
    JScrollPane listPane = new JScrollPane(pieces);
    listPane.setBorder(BorderFactory.createTitledBorder("Piece Types"));
    
    
    JPanel lowerPanel = new JPanel(new BorderLayout());
    
    JPanel btPanel = new JPanel(new GridLayout(1,2));
    cancel = new JButton("Cancel");
    execute = new JButton("Replace");
    cancel.addActionListener(listener);
    execute.addActionListener(listener);
    
    btPanel.add(cancel);
    btPanel.add(execute);
    
    lowerPanel.add(btPanel, BorderLayout.CENTER);
    lowerPanel.add(onlyLockedLayer, BorderLayout.NORTH);
    
    this.setLayout(new BorderLayout());
    this.add(panel1, BorderLayout.NORTH);
    this.add(listPane, BorderLayout.CENTER);
    this.add(lowerPanel, BorderLayout.SOUTH);
    
    pack();
  }
  
  private ImageIcon iconForColor(PieceColor color)
  {
    ImageIcon icon = colorIcons.get(color);
    
    if (icon == null)
    {
      BufferedImage image = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D)image.getGraphics();
      
      g.setColor(color.fillColor);
      g.fillRect(0, 0, 16, 16);
      g.setColor(color.strokeColor);
      g.drawRect(0, 0, 15, 15);
      
      icon = new ImageIcon(image);
      colorIcons.put(color, icon);
    }

    return icon;
  }
  
  private final MouseListener listListener = new MouseAdapter() {
    public void mouseReleased(MouseEvent e)
    {
      JList list = (JList)e.getSource();
      
      int index = list.locationToIndex(e.getPoint());
      PieceSelection piece = (PieceSelection)list.getModel().getElementAt(index);
      
      piece.isSelected = !piece.isSelected;
      
      if (piece.type != null)
      {
        list.repaint(list.getCellBounds(index, index));
        
        if (types[0].isSelected && !piece.isSelected)
        {
          types[0].isSelected = false;
          if (list.getFirstVisibleIndex() == 0)
            list.repaint(list.getCellBounds(0, 0));
        }
        else if (!types[0].isSelected)
        {
          boolean allSelected = true;
          
          for (PieceSelection ps : types)
            if (ps.type != null && !ps.isSelected)
              allSelected = false;

          if (allSelected)
          {
            types[0].isSelected = true;
            if (list.getFirstVisibleIndex() == 0)
              list.repaint(list.getCellBounds(0, 0));
          }
        }
      }
      else
      {
        if (piece.isSelected)
        {
          for (PieceSelection ps : types)
            ps.isSelected = true;
          list.repaint();
        }
        else
          list.repaint(list.getCellBounds(0, 0));

      }
    }
  };
  
  private final ActionListener listener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if (e.getSource() == cancel)
      {
        for (PieceSelection ps : types)
          ps.isSelected = true;
        
        onlyLockedLayer.setSelected(false);
        
        setVisible(false);
      }
      else if (e.getSource() == execute)
      {
        Set<PieceType> set = new HashSet<PieceType>();
        for (PieceSelection ps : types)
          if (ps.isSelected && ps.type != null)
            set.add(ps.type);
        
        if (set.isEmpty())
        {
          Dialogs.showErrorDialog(ReplaceColorFrame.this, "Error", "You must select at least one piece type.");
          return;
        }
        else if (fromColor.getSelectedItem() == toColor.getSelectedItem())
        {
          Dialogs.showErrorDialog(ReplaceColorFrame.this, "Error", "Source and destination color must be different.");
          return;
        }
        
        Task task = null;
        
        if (onlyLockedLayer.isSelected())
          task = new Tasks.ReplaceColorTask(model, Main.sketch.levelStackView.getLocked(), (PieceColor)fromColor.getSelectedItem(), (PieceColor)toColor.getSelectedItem(), set);
        else
          task = new Tasks.ReplaceColorTask(model, (PieceColor)fromColor.getSelectedItem(), (PieceColor)toColor.getSelectedItem(), set);
        
        task.execute();
        Main.sketch.redraw();
        setVisible(false);
      }
    }
  };
  
  private class PieceCellRenderer extends JCheckBox implements ListCellRenderer
  {
    private static final long serialVersionUID = 1L;
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {      
      
      PieceSelection piece = (PieceSelection)value;

      setEnabled(list.isEnabled());
      setSelected(piece.isSelected);
      setFont(list.getFont());
      setBackground(list.getBackground());
      setForeground(list.getForeground());
      setText(value.toString());
      return this;
    }
  }
  
  private class ColorCellRenderer extends DefaultListCellRenderer
  {
    private static final long serialVersionUID = 1L;
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      
      PieceColor color = (PieceColor)value;

      setIcon(iconForColor(color));
      setText(color.caption);
      
      return this;
    }
  };
  
  public void show(Model model)
  {
    this.model = model;
    this.setLocationRelativeTo(Main.mainFrame);
    onlyLockedLayer.setEnabled(Main.sketch.levelStackView.getLocked() != null);
    this.setVisible(true);
  }
}