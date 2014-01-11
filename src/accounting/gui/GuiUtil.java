/***************************************************************************
    begin........: February 2012
    copyright....: Sebastian Fedrau
    email........: sebastian.fedrau@gmail.com
 ***************************************************************************/

/***************************************************************************
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.
 ***************************************************************************/
package accounting.gui;

import java.awt.*;
import javax.swing.*;
import accounting.Translation;

public final class GuiUtil
{
	public static void setLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(InstantiationException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch(UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
	}

    public static void setPreferredWidth(JComponent component, int width)
    {
        Dimension d = component.getPreferredSize();
        d.width = width;
        component.setPreferredSize(d);
    }
    
    public static int getPreferredWidth(JComponent component)
    {
        Dimension d = component.getPreferredSize();
        return d.width;
    }

    public static void translate(Container container)
    {
    	Translation translation = new Translation();

    	for(Component component : container.getComponents())
    	{
    		translate(translation, component);
    	}
    }

    private static void translate(Translation translation, Component component)
    {
		if(component instanceof JLabel)
		{
			((JLabel)component).setText(translation.translate(((JLabel)component).getText()));
		}
		else if(component instanceof JMenu)
		{
			((JMenu)component).setText(translation.translate(((JMenu)component).getText()));
		}
		else if(component instanceof JMenuItem)
		{
			((JMenuItem)component).setText(translation.translate(((JMenuItem)component).getText()));
		}
		else if(component instanceof JButton)
		{
			((JButton)component).setText(translation.translate(((JButton)component).getText()));
		}

    	if(component instanceof Container)
    	{
    		for(Component child : ((Container)component).getComponents())
    		{
    			translate(translation, child);
    		}
    	}

    	if(component instanceof JMenu)
    	{
    		for(int i = 0; i < ((JMenu)component).getItemCount(); ++i)
    		{
    			translate(translation, ((JMenu)component).getItem(i));
    		}
    	}
    }
}