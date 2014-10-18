package pixbits.nanoblock.gui.frames;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.*;

import java.awt.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.files.LibraryModel;


public class LibraryInfoPanel extends JPanel 
{
  private static final long serialVersionUID = 1L;

  private final JLabel thumbnail;
  private final JLabel modelName;
  private final JTable infoTable;
  private final InfoTableModel tableModel;
  
  private LibraryModel lmodel;
  
  LibraryInfoPanel()
  {
    lmodel = null;
    
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
    lmodel = model;
    thumbnail.setIcon(model.thumbnail);
    modelName.setText(model.info.name);
    tableModel.refresh();
  }
  
  private class InfoTableModel extends AbstractTableModel
  {
    private final String[] columnNames = {"Info", "Value"};
    
    private final String[] rowNames = {
      "Model Name",
      "Author",
      "Source",
      "Model Number",
      "Size",
      "Levels",
      "Piece Count",
      "Total Colors"
    };
        
    InfoTableModel()
    {

    }

    public void refresh()
    {
      this.fireTableDataChanged();
    }
    
    @Override
    public boolean isCellEditable(int r, int c) { return lmodel != null && r <= 3 && c == 1; }
    
    @Override
    public void setValueAt(Object o, int r, int c)
    {
      if (r == 0)
        lmodel.info.name = (String)o;
      else if (r == 1)
        lmodel.info.author = (String)o;
      else if (r == 2)
        lmodel.info.source = (String)o;
      else if (r == 3)
        lmodel.info.modelNumber = (String)o;
      
      Main.libraryFrame.getModel().refresh();
      lmodel.writeBack();
      refresh();
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
      else
      {   
        if (lmodel != null)
        {
          switch (r)
          {
            case 0: return lmodel.info.name;
            case 1: return lmodel.info.author;
            case 2: return lmodel.info.source;
            case 3: return lmodel.info.modelNumber;
            case 4: return lmodel.info.width + "x" + lmodel.info.height;
            case 5: return "" + lmodel.info.levels;
            case 6: return "" + lmodel.pieceCount;
            case 7: return "" + lmodel.colorCount;
            default: return null;
          }
        }
        else
          return "N/A";
      }
    }
  }
}
