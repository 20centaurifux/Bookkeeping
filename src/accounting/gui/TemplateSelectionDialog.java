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

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import org.picocontainer.PicoContainer;
import accounting.*;
import accounting.application.*;
import accounting.data.ProviderException;

public class TemplateSelectionDialog extends ADialog implements ActionListener
{
	public static final int RESULT_DELETE = 0;
	public static final int RESULT_OK = 1;
	public static final int RESULT_CANCEL = 2;

	private static final long serialVersionUID = 6261344341258703750L;
	private Factory factory;
	private Container contentPane;
    private JList<Template> listEntries;
    private JButton buttonOk;
    private JButton buttonCancel;
	private int result = RESULT_DELETE;
	private Translation translation;

	public TemplateSelectionDialog(JFrame parent, String title)
	{
		super(parent, title);
		populateTemplates();
		listEntries.setSelectedIndex(0);
	}

	public int getResult()
	{
		return result;
	}

	public List<Template> getSelectedTemplates()
	{
		List<Template> templates = new ArrayList<Template>();
		
		for(Template tpl : listEntries.getSelectedValuesList())
		{
			templates.add(tpl);
		}

		return templates;
	}

	@SuppressWarnings("unchecked")
	protected void initialize()
	{
		PicoContainer pico;
		BorderLayout borderLayout;
		JPanel panelContent;
		JPanel panel;

		translation = new Translation();

		setTitle(translation.translate(getTitle()));

		try
		{
			pico = Injection.getContainer();
			factory = pico.getComponent(Factory.class);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		contentPane = getContentPane();

		setResizable(false);
		borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		contentPane.setLayout(borderLayout);
		
		// content panel:
		panelContent = new JPanel();
		borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		panelContent.setLayout(borderLayout);
		panelContent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		contentPane.add(panelContent, BorderLayout.CENTER);

		// list:
		listEntries = new JList<Template>();
		listEntries.setModel(new GenericListModel<Template>());
		listEntries.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		panelContent.add(new JScrollPane(listEntries), BorderLayout.CENTER);
		
		// action buttons:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelContent.add(panel, BorderLayout.SOUTH);

		// button panel:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		contentPane.add(panel, BorderLayout.SOUTH);
		
		buttonOk = new JButton("Ok");
		buttonOk.setName("buttonOk");
		buttonOk.addActionListener(this);
		panel.add(buttonOk);
		
		buttonCancel = new JButton("Cancel");
		buttonCancel.setName("buttonCancel");
		buttonCancel.addActionListener(this);
		panel.add(buttonCancel);
	}

	private void populateTemplates()
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Template> model = (GenericListModel<Template>)listEntries.getModel();

		try
		{
			model.add(factory.getTemplates());
			model.sort();
		}
		catch (ProviderException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() instanceof JButton)
		{
			if(event.getSource().equals(buttonOk))
			{
				result = RESULT_OK;
				close();
			}
			else if(event.getSource().equals(buttonCancel))
			{
				result = RESULT_CANCEL;
				close();
			}
		}
	}
}