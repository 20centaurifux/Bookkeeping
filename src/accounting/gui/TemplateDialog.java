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
import java.awt.event.*;
import java.util.Vector;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import org.picocontainer.PicoContainer;
import accounting.*;
import accounting.application.*;
import accounting.data.ProviderException;

public class TemplateDialog extends ADialog implements ActionListener
{
	public static final int RESULT_OK = 1;

	private static final long serialVersionUID = 6261344341258703750L;
	private Factory factory;
	private Container contentPane;
    private JList<Template> listEntries;
    private JButton buttonAdd;
    private JButton buttonEdit;
    private JButton buttonDelete;
    private JButton buttonOk;
	private int result = RESULT_OK;
	private Vector<ITemplateListener> listener = new Vector<ITemplateListener>();
	private Vector<ICategoryListener> categoryListener = new Vector<ICategoryListener>();
	private Vector<ICurrencyListener> currencyListener = new Vector<ICurrencyListener>();
	private Translation translation = new Translation();

	public TemplateDialog(JFrame parent, Template preSelected)
	{
		super(parent, "Templates");
		populateTemplates();

		if(preSelected != null)
		{
			listEntries.setSelectedValue(preSelected, true);
		}
		else
		{
			listEntries.setSelectedIndex(0);
		}
	}

	public int getResult()
	{
		return result;
	}

	public Template getSelectedTemplate()
	{
		List<Template> entries = listEntries.getSelectedValuesList();

		if(entries.size() > 0)
		{
			return entries.get(0);
		}

		return null;
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
		listEntries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listEntries.registerKeyboardAction(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent event)
			{
				deleteSelectedTemplate();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		panelContent.add(new JScrollPane(listEntries), BorderLayout.CENTER);
		
		// action buttons:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelContent.add(panel, BorderLayout.SOUTH);
		
		buttonAdd = new JButton("Add template");
		buttonAdd.setName("buttonAdd");
		buttonAdd.addActionListener(this);
		panel.add(buttonAdd);

		buttonEdit = new JButton("Edit");
		buttonEdit.setName("buttonEdit");
		buttonEdit.addActionListener(this);
		panel.add(buttonEdit);
		
		buttonDelete = new JButton("Delete");
		buttonDelete.setName("buttonDelete");
		buttonDelete.addActionListener(this);
		panel.add(buttonDelete);

		// button panel:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		contentPane.add(panel, BorderLayout.SOUTH);
		
		buttonOk = new JButton("Ok");
		buttonOk.setName("buttonOk");
		buttonOk.addActionListener(this);
		panel.add(buttonOk);
	}

	@SuppressWarnings("unchecked")
	private void addTemplate()
	{
		EditTemplateDialog dialog;
		Template template;
		EntityEvent ev;
		
		dialog = new EditTemplateDialog(null, translation.translate("Add template"));
		
		for(ICategoryListener l : categoryListener)
		{
			dialog.addCategoryListener(l);
		}
		
		dialog.open();
		
		if(dialog.getResult() == EditTemplateDialog.RESULT_APPLY)
		{
			template = dialog.getTemplate();
			((GenericListModel<Template>)listEntries.getModel()).add(template);
			listEntries.setSelectedValue(template, true);
			
			ev = new EntityEvent(template);
			
			for(ITemplateListener l : listener)
			{
				l.templateAdded(ev);
			}
		}
	}

	private void editSelectedTemplate()
	{
		Template template;
		EditTemplateDialog dialog;
		EntityEvent ev;

		if((template = getSelectedTemplate()) != null)
		{
			dialog = new EditTemplateDialog(null, template.getName(), template);

			for(ICategoryListener l : categoryListener)
			{
				dialog.addCategoryListener(l);
			}
			
			for(ICurrencyListener l : currencyListener)
			{
				dialog.addCurrencyListener(l);
			}
			
			dialog.open();

			if(dialog.getResult() == EditTemplateDialog.RESULT_APPLY)
			{
				ev = new EntityEvent(template);

				for(ITemplateListener l : listener)
				{
					l.templateChanged(ev);
				}
			}
		}
	}

	private void deleteSelectedTemplate()
	{
		Template template;
		EntityEvent ev;

		if((template= getSelectedTemplate()) != null)
		{
			if(JOptionPane.showConfirmDialog(this, translation.translate("Do you really want to delete the selected template?")) == JOptionPane.YES_OPTION)
			{
				try
				{
					template.delete();

					ev = new EntityEvent(template);
					
					for(ITemplateListener listener : this.listener)
					{
						listener.templateDeleted(ev);
					}
				}
				catch(ReferenceException e)
				{
					JOptionPane.showMessageDialog(this, translation.translate("Cannot delete template, object is still in use."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				}
				catch(ProviderException e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, translation.translate("An internal failure occured, please try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				}
			}    
		}
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
			if(event.getSource().equals(buttonAdd))
			{
				addTemplate();
			}
			else if(event.getSource().equals(buttonEdit))
			{
				editSelectedTemplate();
			}
			else if(event.getSource().equals(buttonDelete))
			{
				deleteSelectedTemplate();
			}
			else if(event.getSource().equals(buttonOk))
			{
				result = RESULT_OK;
				close();
			}
		}
	}

	public void addTemplateListener(ITemplateListener listener)
	{
		this.listener.add(listener);
	}
	
	public void removeTemplateListener(ITemplateListener listener)
	{
		this.listener.remove(listener);
	}

	public void addCategoryListener(ICategoryListener listener)
	{
		this.categoryListener.add(listener);
	}
	
	public void removeCategoryListener(ICategoryListener listener)
	{
		this.categoryListener.remove(listener);
	}

	public void addCurrencyListener(ICurrencyListener listener)
	{
		currencyListener.add(listener);
	}

	public void removeCurrencyListener(ICurrencyListener listener)
	{
		currencyListener.remove(listener);
	}
}