package pixbits.nanoblock.gui.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.*;

import java.util.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.ModelInfo;
import pixbits.nanoblock.files.*;

public class LibraryFrame extends JFrame
{
  private final JList list;
  private final LibraryTableModel model;
  private final JScrollPane scrollpane;
  
  public LibraryFrame()
  {
    model = new LibraryTableModel();
    list = new JList(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setCellRenderer(new LibraryModelRenderer());
    //table.setAutoCreateRowSorter(true);
    scrollpane = new JScrollPane(list);
    scrollpane.setPreferredSize(new Dimension(800,600));
    
    this.setLayout(new BorderLayout());
    this.add(scrollpane, BorderLayout.CENTER);
    this.add(new LibraryInfoPanel(), BorderLayout.EAST);
    
    setTitle("Library");
    pack();
  }
  
  public LibraryTableModel getModel() { return model; }
  
  
  private class LibraryModelRenderer extends DefaultListCellRenderer/*JLabel implements ListCellRenderer*/
  {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      
      LibraryModel model = (LibraryModel)value;
      this.setFont(new Font(this.getFont().getName(), this.getFont().getStyle(), 32));
      setIcon(model.thumbnail);
      setText(model.info.name);
      
      return this;
    }
  };
  
  public class LibraryTableModel extends AbstractListModel
  {
    final List<LibraryModel> data;
    
    LibraryTableModel()
    {
      data = new ArrayList<LibraryModel>();
    }
    
    @Override
    public Object getElementAt(int i) { return data.get(i); }
    
    @Override
    public int getSize() { return data.size(); }
    
    public void add(Collection<? extends LibraryModel> models) { data.addAll(models); }
    public void add(LibraryModel model) { data.add(model); }
    public void clear() { data.clear(); }
    
    public void refresh() { this.fireContentsChanged(this, 0, data.size()-1); }
  }
  
  /*public static class LibraryTableModel extends AbstractTableModel
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
  }*/
  
  public void showMe()
  {
    this.setLocationRelativeTo(Main.mainFrame);
    this.toFront();
    this.setVisible(true);
    
  }
}
