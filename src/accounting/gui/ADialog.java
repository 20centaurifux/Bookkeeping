/***************************************************************************
    begin........: February 2012
    copyright....: Sebastian Fedrau
    email........: sebastian.fedrau@gmail.com
 ***************************************************************************/

/***************************************************************************
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License 3 as published by
    the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    General Public License for more details.
 ***************************************************************************/
package accounting.gui;

import java.awt.event.*;
import javax.swing.*;

public abstract class ADialog extends JDialog implements WindowListener
{
	private static final long serialVersionUID = -895595702063803865L;

	public ADialog(JFrame parent, String title)
	{
		super(parent, title, true);
		initialize();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		GuiUtil.translate(this);
		pack();
		setLocationRelativeTo(null);

		addWindowListener(this);

		rootPane.registerKeyboardAction(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent event)
			{
				close();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public void open()
	{
		setVisible(true);
	}

    protected void close()
    {
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }   

	protected abstract void initialize();

	@Override
	public void windowClosing(WindowEvent event) { }

	@Override
	public void windowActivated(WindowEvent event) { }

	@Override
	public void windowClosed(WindowEvent event) { }

	@Override
	public void windowDeactivated(WindowEvent event) { }

	@Override
	public void windowDeiconified(WindowEvent event) { }

	@Override
	public void windowIconified(WindowEvent event) { }

	@Override
	public void windowOpened(WindowEvent event) { }
}