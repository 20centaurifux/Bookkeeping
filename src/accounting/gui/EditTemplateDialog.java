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
import java.util.Calendar;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import org.picocontainer.PicoContainer;

import accounting.Injection;
import accounting.Translation;
import accounting.application.*;
import accounting.data.*;

public class EditTemplateDialog extends ADialog implements ActionListener, ICurrencyListener
{
	public static final int RESULT_CLOSE = 0;
	public static final int RESULT_APPLY = 1;
	public static final int RESULT_CANCEL = 2;

	private static final long serialVersionUID = 8848635910124681241L;
	private Factory factory;
	private Container contentPane;
	private Template template;
	private JTextField textName;
	private JComboBox comboCategory;
	private JTextArea areaRemarks;
	private JButton buttonClose;
	private JButton buttonApply;
	private JTextField textAccount;
	private JComboBox comboCurrency;
	private JButton buttonCurrency;
	private JSpinner spinnerDate;
	private JButton buttonCategory;
	private JSpinner spinnerAmount;
	private int result = RESULT_CLOSE;
	private Vector<ICategoryListener> listener = new Vector<ICategoryListener>();
	private Vector<ICurrencyListener> currencyListener = new Vector<ICurrencyListener>();
	private Translation translation;

	public EditTemplateDialog(JFrame parent, String title, Template template)
	{
			super(parent, title);

			this.template = template;

			populateCategoryBox();
			populateCurrencies();
			populateTemplate();
    }

	public EditTemplateDialog(JFrame parent, String title)
	{
			super(parent, title);

			template = null;

			populateCategoryBox();
			populateCurrencies();

			if(comboCategory.getSelectedIndex() == -1)
			{
				comboCategory.setSelectedIndex(0);
			}

			if(comboCurrency.getSelectedIndex() == -1)
			{
				comboCurrency.setSelectedIndex(0);
			}
    }

	public int getResult()
	{
		return result;
	}

	public Template getTemplate()
	{
		return template;
	}

	protected void initialize()
	{
		PicoContainer pico;
		BorderLayout borderLayout;
		JPanel panelContent;
		JPanel panel;
		JLabel label;
		Dimension dimension;

		translation = new Translation();
		
		try
		{
			pico = Injection.getContainer();
			factory = pico.getComponent(Factory.class);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		borderLayout = new BorderLayout();
		borderLayout.setVgap(5);
		contentPane = getContentPane();
		contentPane.setLayout(borderLayout);

		// content panel:
		panelContent = new JPanel();
		panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
		panelContent.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		contentPane.add(panelContent, BorderLayout.CENTER);

		// account:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);

		// name:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);

		label = new JLabel("Name:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 110);
		textName = new JTextField();
		textName.setHorizontalAlignment(JTextField.LEFT);
		GuiUtil.setPreferredWidth(textName, 250);
		panel.add(textName);

		// category:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("Category:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 110);
		comboCategory = new JComboBox();
		comboCategory.setName("comboCategory");
		comboCategory.setModel(new GenericComboBoxModel<Category>());
		GuiUtil.setPreferredWidth(comboCategory, 250);
		panel.add(comboCategory);        
		buttonCategory = new JButton("...");
		buttonCategory.setName("buttonCategory");
		buttonCategory.addActionListener(this);
		panel.add(buttonCategory);

		// amount:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("Amount:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 110);
		spinnerAmount= new JSpinner(new SpinnerNumberModel(0, 0, Double.MAX_VALUE, 5));
		spinnerAmount.setEditor(new JSpinner.NumberEditor(spinnerAmount, "0.00"));
		spinnerAmount.setName("spinnerAmount");
		GuiUtil.setPreferredWidth(spinnerAmount, 250);
		panel.add(spinnerAmount);        
		
		// currency:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		panelContent.add(panel); 
		label = new JLabel("Currency:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 110);
		comboCurrency = new JComboBox();
		comboCurrency.setName("comboCurrency");
		comboCurrency.setModel(new GenericComboBoxModel<Currency>());
		panel.add(comboCurrency);
		GuiUtil.setPreferredWidth(comboCurrency, 250);
		
		buttonCurrency = new JButton("...");
		buttonCurrency.setName("buttonCurrency");
		buttonCurrency.addActionListener(this);
		panel.add(buttonCurrency);
		
		// remarks:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		panelContent.add(panel); 
		panel.add(new JLabel("Remarks:"));
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		panelContent.add(panel); 
		areaRemarks = new JTextArea();
		panel.add(new JScrollPane(areaRemarks));

		// button panel:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		contentPane.add(panel, BorderLayout.SOUTH);
		
		buttonApply = new JButton("Apply");
		buttonApply.setName("buttonApply");
		buttonApply.addActionListener(this);
		panel.add(buttonApply);
		
		buttonClose = new JButton("Close");
		buttonClose.setName("buttonClose");
		buttonClose.addActionListener(this);
		panel.add(buttonClose);
		        
		// set optimal sizes:
		dimension = panelContent.getPreferredSize();
		dimension.width -= 20;
		dimension.height = 100;
		areaRemarks.setPreferredSize(dimension);
	}

	private void populateCategoryBox()
	{
		@SuppressWarnings("unchecked")
		GenericComboBoxModel<Category> model = (GenericComboBoxModel<Category>)comboCategory.getModel();

		try
		{
			model.clear();
			model.add(factory.getCategories(true));
			model.add(factory.getCategories(false));
			model.sort();
		}
		catch(ProviderException e)
		{
			e.printStackTrace();
		}
	}

	private void populateCurrencies()
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Currency> model = (GenericListModel<Currency>)comboCurrency.getModel();

		try
		{
			model.add(factory.getCurrencies());
			model.sort();
		}
		catch(ProviderException e)
		{
			e.printStackTrace();
		}
	}
	
	private void populateTemplate()
	{
		textName.setText(template.getName());
		comboCategory.setSelectedItem(template.getCategory());
		spinnerAmount.setValue(template.getCategory().isExpenditure() ? template.getRebate() : template.getIncome());
		areaRemarks.setText(template.getRemarks());
		comboCurrency.setSelectedItem(template.getCurrency());
	}

	private void close(int resultCode)
	{
		result = resultCode;
		processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void editCategories()
	{
		CategoryDialog dialog;
		Category category;

		dialog = new CategoryDialog(null, (Category)comboCategory.getSelectedItem(), CategoryDialog.SHOW_INCOME | CategoryDialog.SHOW_EXPENSE);

		for(ICategoryListener listener : this.listener)
		{
			dialog.addCategoryListener(listener);
		}

		dialog.open();

		populateCategoryBox();

		if(dialog.getResult() == CategoryDialog.RESULT_OK)
		{
			if((category = dialog.getSelectedCategory()) != null)
			{
				comboCategory.getModel().setSelectedItem(category);
			}
		}

		// select first category if selected category has been removed:
		if(comboCategory.getSelectedIndex() == -1)
		{
			comboCategory.setSelectedIndex(0);
		}
	}

	private void editCurrencies()
	{
		CurrencyDialog dialog;

		dialog = new CurrencyDialog(null, (Currency)comboCurrency.getSelectedItem());
		dialog.addCurrencyListener(this);

		for(ICurrencyListener listener : currencyListener)
		{
			dialog.addCurrencyListener(listener);
		}
		
		dialog.open();

		if(dialog.getResult() == CurrencyDialog.RESULT_OK)
		{
			comboCurrency.getModel().setSelectedItem(dialog.getSelectedCurrency());
		}
	}
	
	private boolean saveTemplate()
	{
		String message = null;

		if(template == null)
		{
			try
			{
				template = factory.createTemplate(textName.getText(),
				                                  (Category)comboCategory.getSelectedItem(), 
				                                  ((SpinnerNumberModel)spinnerAmount.getModel()).getNumber().doubleValue(),
				                                  (Currency)comboCurrency.getSelectedItem(), areaRemarks.getText());
			}
			catch(ProviderException e)
			{
				message = "Couldn't save template, an internal error occured. Please try again later.";
				e.printStackTrace();
			}
		}

		if(template != null)
		{	
			try
			{
				template.setName(textName.getText());
			}
			catch(AttributeException e)
			{
				message = "The selected name is invalid, please check your data.";
			}
			
			try
			{
				template.setCategory((Category)comboCategory.getSelectedItem());
			}
			catch(AttributeException e)
			{
				message = "The selected category is invalid, please check your data.";
			}

			try
			{
				template.setCurrency((Currency)comboCurrency.getSelectedItem());
			}
			catch(AttributeException e)
			{
				message = "The selected currency is invalid, please check your data.";
			}
			
			try
			{
				template.setAmount(((SpinnerNumberModel)spinnerAmount.getModel()).getNumber().doubleValue());
			}
			catch(AttributeException e)
			{
				message = "The entered amount is invalid, please check your data.";
			}
	
			try
			{
				template.setRemarks(areaRemarks.getText());
			}
			catch(AttributeException e)
			{
				message = "The entered remarks are invalid, please check your data.";
			}
		
			if(message == null)
			{
				try
				{
					template.save();
				}
				catch(ProviderException e)
				{
		            message = "Couldn't save template, an internal error occured. Please try again later.";
		            e.printStackTrace();
				}
			}
	
			if(message != null)
			{
				JOptionPane.showMessageDialog(this, translation.translate(message), translation.translate("Warning"), JOptionPane.ERROR_MESSAGE);
			}

			return message != null ? false : true;
		}

		return false;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
        if(event.getSource() instanceof JButton)
        {
        	if(event.getSource().equals(buttonClose))
        	{
        		close(RESULT_CLOSE);
        	}
        	else if(event.getSource().equals(buttonApply))
        	{
        		if(saveTemplate())
        		{
        			close(RESULT_APPLY);
        		}
        	}
        	else if(event.getSource().equals(buttonCategory))
        	{
        		editCategories();
        	}
        	else if(event.getSource().equals(buttonCurrency))
        	{
        		editCurrencies();
        	}
        }
	}
	
	public void addCategoryListener(ICategoryListener listener)
	{
		this.listener.add(listener);
	}

	public void removeCategoryListener(ICategoryListener listener)
	{
		this.listener.remove(listener);
	}

	public void addCurrencyListener(ICurrencyListener listener)
	{
		currencyListener.add(listener);
	}

	public void removeCurrencyListener(ICurrencyListener listener)
	{
		currencyListener.remove(listener);
	}

	@Override
	public void currencyChanged(EntityEvent event)
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Currency> model = (GenericListModel<Currency>)comboCurrency.getModel();
		Currency currency;
		Currency source = (Currency)event.getSource();

		comboCurrency.removeActionListener(this);

		for(int i = 0; i < model.getSize(); ++i)
		{
			currency = (Currency)model.getElementAt(i);

			if(currency.getId() == source.getId())
			{
				try
				{
					currency.setName(source.getName());
				}
				catch(AttributeException e)
				{
					e.printStackTrace();
				}

				model.sort();
				break;
			}
		}

		comboCurrency.addActionListener(this);
	}

	@Override
	public void currencyDeleted(EntityEvent event)
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Currency> model = (GenericListModel<Currency>)comboCurrency.getModel();
		
		comboCurrency.removeActionListener(this);
		model.remove((Currency)event.getSource());
		comboCurrency.addActionListener(this);
	}

	@Override
	public void currencyAdded(EntityEvent event)
	{
		@SuppressWarnings("unchecked")
		GenericListModel<Currency> model = (GenericListModel<Currency>)comboCurrency.getModel();

		comboCurrency.removeActionListener(this);
		model.add((Currency)event.getSource());
		model.sort();
		comboCurrency.addActionListener(this);
	}
}