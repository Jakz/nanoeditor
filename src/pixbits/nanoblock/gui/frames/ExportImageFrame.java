package pixbits.nanoblock.gui.frames;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.pixbits.lib.ui.elements.ComponentBorder;

import java.io.File;
import java.awt.*;

import javax.swing.*;

import java.awt.event.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.data.Model;
import pixbits.nanoblock.tasks.*;

public class ExportImageFrame extends BaseDialog
{
  private final JPHTextField fileName;
  
  private final JButton browse;
  
  private final JFileChooser fc;
  
  private final JCheckBox showCaps;
  private final JCheckBox allRotations;
  
  private Model model;
  private File file;
  
  
    
  private ExportImageFrame(JFrame parent, Model model)
  {
    super(parent, "Export Image", new String[] {"Cancel", "Export"});
    
    this.model = model;

    middle.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    
    fileName = new JPHTextField(30);
    fileName.setPlaceholder("filename");

    browse = new JButton("...");
    browse.addActionListener(listener);
    browse.setBorder(null);
    
    ComponentBorder cb = new ComponentBorder(browse, ComponentBorder.Edge.RIGHT);
    cb.install(fileName);

    allRotations = new JCheckBox("Export all rotations");
    showCaps = new JCheckBox("Show caps");
    
    top.add(showCaps);
    top.add(allRotations);
    
    middle.add(fileName);
    
    fc = new JFileChooser();
    
    finalizeDialog();
  }
  
  public static void showMe(Model model)
  {
    JFrame parent = Main.mainFrame; 
    
    if (Main.libraryFrame.isVisible())
      parent = Main.libraryFrame;
    
    ExportImageFrame frame = new ExportImageFrame(parent, model);
    frame.setLocationRelativeTo(parent);
    frame.setVisible(true);
  }
  
  @Override
  public void buttonClicked(JButton button)
  {
    if (button == cancel)
    {
      fileName.setText("");
      setVisible(false);
    }
    else if (button == browse)
    {
      int choice = fc.showSaveDialog(this);
      
      if (choice == JFileChooser.APPROVE_OPTION)
      {
        File file = fc.getSelectedFile();
        String name = file.getName();
        if (!name.endsWith(".png"))
        {
          if (name.charAt(name.length()-1) == '.')
            file = new File(file.getAbsoluteFile()+"png");
          else
            file = new File(file.getAbsoluteFile()+".png");
        }
        fileName.setText(file.getAbsolutePath());
      }
    }
    else if (button == execute)
    {
      if (file == null && fileName.getText().equals(""))
      {
        Dialogs.showErrorDialog(this, "Error", "Please specify an output file name");
        return;
      }
      
      file = new File(fileName.getText());
      
      Task task = new Tasks.ExportModelImageTask(this, model, file, showCaps.isSelected(), allRotations.isSelected());
      if (task.execute())
        this.setVisible(false);
    }
  }
}
