/***************************************************************************
    begin........: February 2012
    copyright....: Sebastian Fedrau
    email........: lord-kefir@arcor.de
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

import java.awt.Container;
import java.awt.event.*;
import javax.swing.JFrame;

public abstract class AFrame extends JFrame
{
	protected Container contentPane;

	private static final long serialVersionUID = 647825449450921122L;

    public AFrame(String title)
    {
    	super(title);
    	contentPane = this.getContentPane();

    	setDefaultCloseOperation(DISPOSE_ON_CLOSE);  
        initialize();
        centerFrame();
        GuiUtil.translate(this);
    }

    public void open()
    {
        setVisible(true);
    }

    protected abstract void initialize();

    protected void centerFrame()
    {
    	this.setLocationRelativeTo(null);
    }
    
    protected void close()
    {
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}