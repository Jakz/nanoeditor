package pixbits.nanoblock.gui.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.files.*;
import pixbits.nanoblock.gui.menus.Item;
import pixbits.nanoblock.gui.menus.Menus;
import pixbits.nanoblock.tasks.Tasks;

public class LibraryFrame extends JFrame
{
  private static final long serialVersionUID = 1L;
  private final JList<LibraryModel> list;
  private final LibraryTableModel model;
  private final JScrollPane scrollpane;
  
  private final LibraryInfoPanel infoPanel;
  
  public LibraryFrame()
  {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    model = new LibraryTableModel();
    list = new JList<>(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setCellRenderer(new LibraryModelRenderer());
    list.addListSelectionListener(selectionListener);
    list.addMouseListener(mouseListener);
    //table.setAutoCreateRowSorter(true);
    scrollpane = new JScrollPane(list);
    scrollpane.setPreferredSize(new Dimension(800,600));
    
    infoPanel = new LibraryInfoPanel();
    
    this.setLayout(new BorderLayout());
    this.add(scrollpane, BorderLayout.CENTER);
    this.add(infoPanel, BorderLayout.EAST);
    this.add(Menus.buildLibraryToolbar(), BorderLayout.NORTH);

    
    setTitle("Library");
    pack();
  }
  
  public LibraryTableModel getModel() { return model; }
  public LibraryInfoPanel getInfoPanel() { return infoPanel; }
  
  
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
  
  private final ListSelectionListener selectionListener = new ListSelectionListener() {
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
          Item.setLibraryModelOperationsEnabled(false);
          
        }
        else
        {
          LibraryModel lmodel = model.get(index);
          Library.i().setLibraryModel(lmodel);
          infoPanel.update(lmodel);
          Item.setLibraryModelOperationsEnabled(true);
        }
      }
    }
  };
  
  private final MouseListener mouseListener = new MouseAdapter() {
    public void mouseClicked(MouseEvent e)
    {
      JList list = (JList)e.getSource();
      
      if (e.getClickCount() == 2)
      {
        int index = list.getSelectedIndex();
        
        LibraryModel lmodel = model.get(index);
        
        list.clearSelection();
        
        Tasks.LIBRARY_OPEN_IN_EDITOR.execute(lmodel);
      }
    }
  };
  
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
    else if (willBeEmpty) list.clearSelection();
    else list.setSelectedIndex(index);
  }
  
  public void refreshLibrary(LibraryModel selected)
  {
    model.clear();
    model.add(Library.i().getModels());
    model.refresh();
    if (selected != null)
      list.setSelectedValue(selected, true);
  }
  
  public class LibraryTableModel extends AbstractListModel<LibraryModel>
  {
    private static final long serialVersionUID = 1L;
    
    final List<LibraryModel> data;
    
    LibraryTableModel()
    {
      data = new ArrayList<LibraryModel>();
    }
    
    @Override
    public LibraryModel getElementAt(int i) { return data.get(i); }
    
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
 
  public void refreshList()
  {
    model.refresh();
    if (list.getSelectedValue() != null)
      infoPanel.update((LibraryModel)list.getSelectedValue());
  }
  
  public void showMe()
  {
    Item.setLibraryModelOperationsEnabled(false);
    this.setLocationRelativeTo(Main.mainFrame);
    this.toFront();
    this.infoPanel.clear();
    this.list.clearSelection();
    this.setVisible(true);
  }
}
