package pixbits.nanoblock.gui.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.util.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.ModelInfo;
import pixbits.nanoblock.files.*;

public class LibraryFrame extends JFrame
{
  private static final long serialVersionUID = 1L;
  private final JList list;
  private final LibraryTableModel model;
  private final JScrollPane scrollpane;
  
  private final LibraryInfoPanel infoPanel;
  
  public LibraryFrame()
  {
    model = new LibraryTableModel();
    list = new JList(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setCellRenderer(new LibraryModelRenderer());
    list.addListSelectionListener(new LibraryModelListener());
    //table.setAutoCreateRowSorter(true);
    scrollpane = new JScrollPane(list);
    scrollpane.setPreferredSize(new Dimension(800,600));
    
    infoPanel = new LibraryInfoPanel();
    
    this.setLayout(new BorderLayout());
    this.add(scrollpane, BorderLayout.CENTER);
    this.add(infoPanel, BorderLayout.EAST);
    this.add(Toolbar.buildLibraryToolbar(), BorderLayout.NORTH);

    
    setTitle("Library");
    pack();
  }
  
  public LibraryTableModel getModel() { return model; }
  
  
  private class LibraryModelRenderer extends DefaultListCellRenderer/*JLabel implements ListCellRenderer*/
  {
    private static final long serialVersionUID = 1L;
    
    @Override
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
  
  private class LibraryModelListener implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
      if (!e.getValueIsAdjusting())
      {
        int index = list.getSelectedIndex();
        
        if (index == -1)
        {
          Library.i().setLibraryModel(null);
          infoPanel.clear();
          
        }
        else
        {
          //System.out.println("VALUE CHANGEDDDD!!!!");
          LibraryModel lmodel = model.get(index);
          Library.i().setLibraryModel(lmodel);
          infoPanel.update(lmodel);
        }
      }
    }
  }
  
  public void addModel(LibraryModel lmodel)
  {
    model.add(lmodel);
    model.refresh();
  }
  
  // TODO: maybe don't remove from library view, just refresh it fetching the whole library again
  public void removeModel(LibraryModel lmodel)
  {
    int index = model.indexOf(lmodel);
    boolean isLast = index == model.getSize() - 1;
    boolean willBeEmpty = model.getSize() == 1;

    model.remove(lmodel);
    model.refresh();
    
    if (isLast) list.setSelectedIndex(index - 1);
    else if (willBeEmpty) list.setSelectedIndex(-1);
    else list.setSelectedIndex(index);
  }
  
  public class LibraryTableModel extends AbstractListModel
  {
    private static final long serialVersionUID = 1L;
    
    final List<LibraryModel> data;
    
    LibraryTableModel()
    {
      data = new ArrayList<LibraryModel>();
    }
    
    @Override
    public Object getElementAt(int i) { return data.get(i); }
    
    @Override
    public int getSize() { return data.size(); }
    
    public int indexOf(LibraryModel model) { return data.indexOf(model); }
    public void remove(LibraryModel model) { data.remove(model); }
    public void add(Collection<? extends LibraryModel> models) { data.addAll(models); }
    public void add(LibraryModel model) { data.add(model); }
    public void clear() { data.clear(); }
    public LibraryModel get(int index) { return data.get(index); }
    
    public void refresh() { this.fireContentsChanged(this, 0, data.size()); }
    public void refresh(int index) { this.fireContentsChanged(this, index, index); }
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
