package pixbits.nanoblock.gui.frames;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.*;

import java.awt.*;

import pixbits.nanoblock.files.LibraryModel;


public class LibraryInfoPanel extends JPanel 
{
  private static final long serialVersionUID = 1L;

  private final JLabel thumbnail;
  private final JLabel modelName;
  private final JTable infoTable;
  private final InfoTableModel tableModel;
  
  LibraryInfoPanel()
  {
    this.setPreferredSize(new Dimension(300,600));
    
    thumbnail = new JLabel();
    thumbnail.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
    thumbnail.setAlignmentX(Component.CENTER_ALIGNMENT);
    thumbnail.setPreferredSize(new Dimension(240, 200));
    
    modelName = new JLabel();
    modelName.setFont(new Font(modelName.getFont().getName(), Font.BOLD, 20));
    modelName.setAlignmentX(Component.CENTER_ALIGNMENT);
    modelName.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    
    tableModel = new InfoTableModel();
    infoTable = new JTable(tableModel);
    
    JScrollPane spTable = new JScrollPane(infoTable);
    spTable.setPreferredSize(new Dimension(240,200));
    //spTable.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    spTable.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    this.add(thumbnail);
    this.add(modelName);
    this.add(spTable);
  }
  
  void clear()
  {
    thumbnail.setIcon(null);
  }
  
  void update(LibraryModel model)
  {
    thumbnail.setIcon(model.thumbnail);
    modelName.setText(model.info.name);
    tableModel.update(model);
    tableModel.refresh();
  }
  
  private static class InfoTableModel extends AbstractTableModel
  {
    private final String[] columnNames = {"Info", "Value"};
    
    private final String[] rowNames = {
      "Model Name",
      "Author",
      "Source",
      "Size",
      "Levels",
      "Piece Count",
      "Total Colors"
    };
    
    private final String[] values;
    
    InfoTableModel()
    {
      values = new String[rowNames.length];
      for (int i = 0; i < values.length; ++i)
        values[i] = "N/A";
    }
    
    public void update(LibraryModel model)
    {
      values[0] = model.info.name;
      values[1] = model.info.author;
      values[2] = model.info.source;
      values[3] = model.info.width + "x" + model.info.height;
      values[4] = "" + model.info.levels;
      values[5] = "" + model.pieceCount;
      values[6] = "" + model.colorCount;
    }
    
    public void refresh()
    {
      this.fireTableDataChanged();
    }
    
    @Override
    public int getRowCount() { return rowNames.length; }
    
    @Override
    public int getColumnCount() { return 2; }
    
    @Override
    public String getColumnName(int i) { return columnNames[i]; }
    
    @Override
    public Object getValueAt(int r, int c) {
      if (c == 0) return rowNames[r];
      else return values[r];
    }
  }
}
