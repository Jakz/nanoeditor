package pixbits.nanoblock.gui.frames;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.*;

import java.util.*;

import pixbits.nanoblock.data.ModelInfo;
import pixbits.nanoblock.files.*;

public class LibraryFrame extends JFrame
{
  private final JTable table;
  private final LibraryTableModel model;
  private final JScrollPane scrollpane;
  
  public LibraryFrame()
  {
    model = new LibraryTableModel();
    table = new JTable(model);
    table.setAutoCreateRowSorter(true);
    scrollpane = new JScrollPane(table);
    scrollpane.setPreferredSize(new Dimension(800,600));
    
    this.setLayout(new BorderLayout());
    this.add(scrollpane, BorderLayout.CENTER);
    
    setTitle("Library");
    pack();
  }
  
  public LibraryTableModel getModel() { return model; }
  
  
  public static class LibraryTableModel extends AbstractTableModel
  {
    final List<LibraryModel> data;
    
    private final String[] columnNames = {"Name", "Author", "Size", "Levels"};
    private final Class<?>[] columnClasses = {String.class, String.class, String.class, String.class};
    
    LibraryTableModel()
    {
      data = new ArrayList<LibraryModel>();
    }
    
    @Override
    public int getRowCount() { return data.size(); }
    
    @Override
    public int getColumnCount() { return columnNames.length; }
    
    @Override
    public Class<?> getColumnClass(int i) { return columnClasses[i]; }
    
    @Override
    public String getColumnName(int i) { return columnNames[i]; }
    
    @Override
    public Object getValueAt(int r, int c)
    {
      ModelInfo m = data.get(r).info;
      
      switch (c)
      {
        case 0: return m.name;
        case 1: return m.author;
        case 2: return "" + m.width + "x" + m.height;
        case 3: return m.levels;
        default: return null;
      }
    }
    
    public void add(Collection<? extends LibraryModel> c) { data.addAll(c); }
    public void add(LibraryModel lm) { data.add(lm); }
    public void clear() { data.clear(); }
    public void refresh() { this.fireTableDataChanged(); }
  }
}
