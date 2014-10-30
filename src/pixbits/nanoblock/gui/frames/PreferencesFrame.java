package pixbits.nanoblock.gui.frames;

import java.awt.*;

import javax.swing.*;

import pixbits.nanoblock.Main;
import pixbits.nanoblock.files.Thumbnails;
import pixbits.nanoblock.misc.Settings;
import pixbits.nanoblock.tasks.Tasks;

import java.awt.event.*;

public class PreferencesFrame extends BaseDialog
{
  JTextField thumbnailWidth, thumbnailHeight, thumbnailPadding;
  
  public PreferencesFrame()
  {
    super(Main.libraryFrame, "Preferences", new String[] {"Cancel", "Save"});

    JPanel thumbnail = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.NONE;
    c.gridy = 0;
    
    thumbnail.setBorder(BorderFactory.createTitledBorder("Thumbnail"));
    
    thumbnailWidth = new JTextField(4);
    thumbnailWidth.setHorizontalAlignment(JTextField.CENTER);
    thumbnailHeight = new JTextField(4);
    thumbnailHeight.setHorizontalAlignment(JTextField.CENTER);
    thumbnailPadding = new JTextField(4);
    thumbnailPadding.setHorizontalAlignment(JTextField.CENTER);
    
    c.gridx = 0;
    c.insets = new Insets(0,0,0,10);
    thumbnail.add(new JLabel("Size"), c);
    c.gridx = 1;
    c.insets = new Insets(0,0,0,0);
    thumbnail.add(thumbnailWidth, c);
    c.gridx = 2;
    thumbnail.add(new JLabel("x"), c);
    c.gridx = 3;
    thumbnail.add(thumbnailHeight, c);
    c.gridx = 4;
    c.insets = new Insets(0,40,0,10);
    thumbnail.add(new JLabel("Padding"), c);
    c.gridx = 5;
    c.insets = new Insets(0,0,0,0);
    thumbnail.add(thumbnailPadding, c);
    
    top.add(thumbnail);
    
    finalizeDialog();
  }
  
  public void refresh()
  {
    Dimension thumbnailSize = Settings.values.getThumbnailSize();
    int thumbnailPadding = Settings.values.getThumbnailPadding();
    
    thumbnailWidth.setText(""+thumbnailSize.width);
    thumbnailHeight.setText(""+thumbnailSize.height);
    this.thumbnailPadding.setText(""+thumbnailPadding);
  }
  
  public void showMe()
  {
    refresh();
    this.setLocationRelativeTo(Main.libraryFrame);
    setVisible(true);
  }
  
  public int parseInt(JTextField field, String fieldName) throws NumberFormatException
  {
    try
    {
      int value = Integer.parseInt(field.getText());
      return value;
    }
    catch (NumberFormatException e)
    {
      Dialogs.showErrorDialog(this, "Parsing error", fieldName+" should be a number!");
      throw e;
    }
  }
  
  @Override
  protected void buttonClicked(JButton button)
  {
    if (button == cancel)
    {
      setVisible(false);
    }
    else if (button == execute)
    {
      try
      {
        int tbWidth = parseInt(thumbnailWidth, "Width of thumbnail");
        int tbHeight = parseInt(thumbnailHeight, "Height of thumbnail");
        int tbPadding = parseInt(thumbnailPadding, "Padding of thumbnail");
        
        if (Settings.values.updateThumbnailSpec(tbWidth, tbHeight, tbPadding))
        {
          Thumbnails.refreshAllThumbnails();
          Main.libraryFrame.refreshList();
          Main.libraryFrame.getInfoPanel().updateThumbnailSize();
        }
        
        // TODO: mey be to be removed later
        Tasks.saveSettings();
        setVisible(false);
      }
      catch (NumberFormatException e)
      {
        return;
      }
    }
  }
}
