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
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import org.picocontainer.PicoContainer;
import accounting.*;
import accounting.application.*;
import accounting.data.ProviderException;

public class CurrencyDialog extends ADialog implements ActionListener
{
	public static final int RESULT_DELETE = 0;
	public static final int RESULT_OK = 1;

	private static final long serialVersionUID = 2261144841758703750L;
	private Factory factory;
	private Container contentPane;
    private JList<Currency> listEntries;
    private JButton buttonAdd;
    private JButton buttonRename;
    private JButton buttonDelete;
    private JButton buttonOk;
	private int result = RESULT_DELETE;
	private Vector<ICurrencyListener> listener = new Vector<ICurrencyListener>();
	private Translation translation = new Translation();

	public CurrencyDialog(JFrame parent, Currency preSelected)
	{
		super(parent, "Currencies");
		populateCurrencies();

		if(preSelected != null)
		{
			listEntries.setSelectedValue(preSelected, true);
		}
	}

	public int getResult()
	{
		return result;
	}

	public Currency getSelectedCurrency()
	{
		List<Currency> entries = listEntries.getSelectedValuesList();

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
		listEntries = new JList<Currency>();
		listEntries.setModel(new GenericListModel<Currency>());
		listEntries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listEntries.registerKeyboardAction(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent event)
			{
				deleteSelectedCurrency();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		panelContent.add(new JScrollPane(listEntries), BorderLayout.CENTER);
		
		// action buttons:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelContent.add(panel, BorderLayout.SOUTH);
		
		buttonAdd = new JButton("Add currency");
		buttonAdd.setName("buttonAdd");
		buttonAdd.addActionListener(this);
		panel.add(buttonAdd);

		buttonRename = new JButton("Rename");
		buttonRename.setName("buttonRename");
		buttonRename.addActionListener(this);
		panel.add(buttonRename);
		
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
	private void addCurrency()
	{
		String name;
		GenericListModel<Currency> model = (GenericListModel<Currency>)listEntries.getModel();
		Currency currency;

		name = JOptionPane.showInputDialog(this, translation.translate("Please enter a name for the new currency:"), translation.translate("new currency"));

		if (name != null && !name.isEmpty())
		{
			try
			{
				currency = factory.createCurrency(name);
				model.add(currency);
				model.sort();
				listEntries.setSelectedValue(currency, true);

				for(ICurrencyListener listener : this.listener)
				{
					listener.currencyAdded(new EntityEvent(currency));
				}
			}
			catch(ProviderException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, translation.translate("Couldn't create currency, please try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void renameSelectedCurrency()
	{
		GenericListModel<Currency> model;
		Currency currency;
		String name;

		if((currency = getSelectedCurrency()) != null)
		{
			name = JOptionPane.showInputDialog(this, translation.translate("Please enter a name for the currency:"), currency.getName());

			if (name != null && !name.isEmpty())
			{
				try
				{
					currency.setName(name);
					currency.save();

					for(ICurrencyListener listener : this.listener)
					{
						listener.currencyChanged(new EntityEvent(currency));
					}
					
					model = (GenericListModel<Currency>)listEntries.getModel();
					model.sort();
					listEntries.setSelectedValue(currency, true);
				}
				catch(AttributeException e)
				{
					JOptionPane.showMessageDialog(this, translation.translate("Invalid currency name, please check the entered text and try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				}
				catch(ProviderException e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, translation.translate("An internal failure occured, please try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				}
			}    
		}
	}

	private void deleteSelectedCurrency()
	{
		Currency currency;

		if((currency = getSelectedCurrency()) != null)
		{
			if(listEntries.getModel().getSize() == 1)
			{
				JOptionPane.showMessageDialog(this, translation.translate("You cannot delete the last currency."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if(JOptionPane.showConfirmDialog(this, translation.translate("Do you really want to delete the selected currency?")) == JOptionPane.YES_OPTION)
			{
				try
				{
					currency.delete();

					for(ICurrencyListener listener : this.listener)
					{
						listener.currencyDeleted(new EntityEvent(currency));
					}
				}
				catch(ReferenceException e)
				{
					JOptionPane.showMessageDialog(this, translation.translate("Cannot delete currency, object is still in use."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				}
				catch(ProviderException e)
				{
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, translation.translate("An internal failure occured, please try again."), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
				}
			}    
		}
	}

	private void populateCurrencies()
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Currency> model = (GenericListModel<Currency>)listEntries.getModel();

		try
		{
			model.add(factory.getCurrencies());
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
				addCurrency();
			}
			else if(event.getSource().equals(buttonRename))
			{
				renameSelectedCurrency();
			}
			else if(event.getSource().equals(buttonDelete))
			{
				deleteSelectedCurrency();
			}
			else if(event.getSource().equals(buttonOk))
			{
				result = RESULT_OK;
				close();
			}
		}
	}

	public void addCurrencyListener(ICurrencyListener listener)
	{
		this.listener.add(listener);
	}

	public void removeCurrencyListener(ICurrencyListener listener)
	{
		this.listener.remove(listener);
	}
}