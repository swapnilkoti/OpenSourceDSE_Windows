package dsePackage;

import javax.swing.ListCellRenderer;
import javax.swing.JList;
import java.awt.Component;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;

class MyListRenderer implements ListCellRenderer
{
  public Component getListCellRendererComponent(JList jlist, 
                                                Object value, 
                                                int cellIndex, 
                                                boolean isSelected, 
                                                boolean cellHasFocus)
  {
    if (value instanceof JPanel)
    {
      Component component = (Component) value;
      component.setForeground (Color.white);
      if(isSelected){
    	  component.setBackground (Color.lightGray);  
      }
      else{
    	  component.setBackground(Color.white);
      }
      return component;
    }
    else
    {
      return new JLabel("???");
    }
  }
}

