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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import accounting.AppInfo;

public class AboutDialog extends ADialog implements ActionListener
{
	private static final long serialVersionUID = 2072415710193023874L;
	private Container contentPane;
	private JButton buttonOk;

	public AboutDialog(JFrame parent)
	{
		super(parent, AppInfo.TITLE);
	}

	@Override
	protected void initialize()
	{
		BorderLayout borderLayout;
		JPanel panelContent;
		JPanel panel;
		JLabel label;
		StringBuilder sb = new StringBuilder();

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		setResizable(false);

		borderLayout = new BorderLayout();
		borderLayout.setVgap(5);

		contentPane = getContentPane();
		contentPane.setLayout(borderLayout);

		// info labels:
		panelContent = new JPanel();
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
		contentPane.add(panelContent, BorderLayout.CENTER);
		
		label = new JLabel(String.format("<html><font size=+2>%s, version %s</font></html>", AppInfo.TITLE, AppInfo.VERSION_DESCRIPTION));
		label.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 10, 15, 0), null));
		panelContent.add(label);

		label = new JLabel(String.format("<html><font color=#525252>written by: %s &lt;%s&gt;</font></html>", AppInfo.AUTHOR, AppInfo.AUTHOR_EMAIL));
		label.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 10, 5, 0), null));
		panelContent.add(label);

		label = new JLabel(String.format("<html><font color=#525252>licensed under %s, %s</font></html>", AppInfo.LICENSE, AppInfo.DATE));
		label.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 10, 5, 0), null));
		panelContent.add(label);

		label = new JLabel(String.format("<html><font color=#525252>%s</font></html>", AppInfo.URL));
		label.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 10, 10, 0), null));
		panelContent.add(label);

		// image:
		panelContent = new JPanel();
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
		
		sb.append("<html>");
		
		for(String line : AppInfo.ICON)
		{
			sb.append(String.format("%s<br/>", line));
		}
		
		sb.append("</html>");
		
		label = new JLabel(sb.toString());
		label.setFont(new Font("Monospaced", Font.PLAIN, 1));
		panelContent.add(label);
		contentPane.add(panelContent, BorderLayout.WEST);

		// button panel:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		contentPane.add(panel, BorderLayout.SOUTH);
		
		buttonOk = new JButton("Ok");
		buttonOk.setName("buttonOk");
		buttonOk.addActionListener(this);
		panel.add(buttonOk);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(buttonOk))
		{
			close();
		}
	}
}