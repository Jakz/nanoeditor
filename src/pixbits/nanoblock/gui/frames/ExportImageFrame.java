package pixbits.nanoblock.gui.frames;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import pixbits.nanoblock.Main;

public class ExportImageFrame extends BaseDialog
{
  private final JPHTextField fileName;
  
  private final JButton browse;

  private final ButtonGroup typeGroup;
  
  private final JFileChooser fc;
    
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
  
  public void showMe()
  {
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
      
    }
  }
}
