package pixbits.nanoblock.gui.frames;

import javax.swing.filechooser.FileNameExtensionFilter;

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

  private final ButtonGroup typeGroup;
  
  private final JFileChooser fc;
  
  private Model model;
  private File file;
    
  public ExportImageFrame()
  {
    //TODO: not library but main
    super(Main.libraryFrame, "Export Image", new String[] {"Cancel", "Export"});

    middle.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
    
    fileName = new JPHTextField(40);
    fileName.setPlaceholder("filename");
    
    browse = new JButton("...");
    browse.addActionListener(listener);
    
    middle.add(fileName);
    middle.add(browse);
    
    typeGroup = new ButtonGroup();
    
    fc = new JFileChooser();
    
    finalizeDialog();
  }
  
  public void showMe(Model model)
  {
    this.model = model;
    setLocationRelativeTo(Main.libraryFrame);
    setVisible(true);
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
        file = fc.getSelectedFile();
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
      Task task = new Tasks.ExportModelImageTask(model, file, false);
      task.execute();
    }
  }
}
