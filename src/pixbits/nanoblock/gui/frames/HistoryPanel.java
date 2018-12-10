package pixbits.nanoblock.gui.frames;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.ListModel;

import pixbits.nanoblock.tasks.UndoableTask;

public class HistoryPanel extends JPanel
{
  private final JList<UndoableTask> list;
  private final List<UndoableTask> tasks;
  private final ListModel<UndoableTask> model;
    
  public HistoryPanel(List<UndoableTask> tasks)
  {
    this.tasks = tasks;
    this.list = new JList<>();
    this.model = new ListModel<>(list, DataSource.of(tasks));

    setLayout(new BorderLayout());
    
    add(new JScrollPane(list), BorderLayout.CENTER);
    
    JButton refresh = new JButton("refresh");
    refresh.addActionListener(e -> model.refresh());
    add(refresh, BorderLayout.SOUTH);
  }
}
