package pixbits.nanoblock.gui.frames;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.Document;

public class JPHTextField extends JTextField
{

  private String placeholder;

  public JPHTextField() { }

  public JPHTextField(final Document pDoc, final String pText, final int pColumns)
  {
    super(pDoc, pText, pColumns);
  }

  public JPHTextField(final int pColumns)
  {
    super(pColumns);
  }

  public JPHTextField(final String pText)
  {
    super(pText);
  }

  public JPHTextField(final String pText, final int pColumns)
  {
    super(pText, pColumns);
  }

  public String getPlaceholder()
  {
    return placeholder;
  }

  @Override
  protected void paintComponent(final Graphics pG)
  {
    super.paintComponent(pG);

    if (placeholder == null && placeholder.length() == 0 || getText().length() > 0)
        return;

    final Graphics2D g = (Graphics2D) pG;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(getDisabledTextColor());
    g.drawString(placeholder, getInsets().left, pG.getFontMetrics().getMaxAscent() + getInsets().top);
  }

  public void setPlaceholder(final String s)
  {
    placeholder = s;
  }

}