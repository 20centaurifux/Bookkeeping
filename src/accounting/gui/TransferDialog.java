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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import org.picocontainer.PicoContainer;

import java.util.*;
import accounting.*;
import accounting.application.*;
import accounting.data.ProviderException;

public class TransferDialog extends ADialog implements ActionListener, ItemListener
{
	public static final int RESULT_DELETE = -1;
	public static final int RESULT_APPLY = 1;
	public static final int RESULT_CLOSE = 2;
	public static final int RESULT_FAILURE = 3;

	private static final long serialVersionUID = -7478415710199023874L;
	private Container contentPane;
	private Account account;
	private JButton buttonApply;
	private JButton buttonClose;
	private JTextField textAccount;
	private JTextField textNo;
	private JSpinner spinnerDate;
	private JComboBox<Category> comboCategoryFrom;
	private JButton buttonCategoryFrom;
	private JSpinner spinnerAmount;
	private JComboBox<Account> comboAccountTo;
	private JComboBox<Category> comboCategoryTo;
	private JButton buttonCategoryTo;
	private JSpinner spinnerExchangeRate;
	private JTextArea areaRemarks;
	private int result = RESULT_DELETE;
	private Factory factory;
	private Vector<ICategoryListener> categoryListener = new Vector<ICategoryListener>();
	private Transaction fromTransaction;
	private Transaction toTransaction;
	private ExchangeUtil exchangeUtil;
	private Account lastDestination;

	public TransferDialog(JFrame parent, Account account)
	{
		super(parent, "Transfer");

		PicoContainer container = null;

		try
		{
			container = Injection.getContainer();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		factory = container.getComponent(Factory.class);
		exchangeUtil = container.getComponent(ExchangeUtil.class);

		this.account = account;

		populate();
	}

	@Override
	public void open()
	{
		Translation translation = new Translation();
		String message = null;

		if(comboAccountTo.getModel().getSize() == 0)
		{
			message = "You need at least two accounts to transfer money.";
		}

		if(message != null)
		{
			JOptionPane.showMessageDialog(null, translation.translate(message), translation.translate("Warning"), JOptionPane.WARNING_MESSAGE);
			close();
		}
		else
		{
			setVisible(true);
		}
	}

	public int getResult()
	{
		return result;
	}

	public Transaction getFromTransaction()
	{
		return fromTransaction;
	}

	public Transaction getToTransaction()
	{
		return toTransaction;
	}

	public void addCategoryListener(ICategoryListener listener)
	{
		categoryListener.add(listener);
	}

	public void removeCategoryListener(ICategoryListener listener)
	{
		categoryListener.remove(listener);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initialize()
	{
		BorderLayout borderLayout;
		JPanel panelContent;
		JPanel panel;
		JLabel label;
		Dimension dimension;
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(this);
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
		
		// account (from):
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("From:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 120);
		textAccount = new JTextField();
		textAccount.setName("textAccount");
		textAccount.setEnabled(false);
		GuiUtil.setPreferredWidth(textAccount, 250);
		panel.add(textAccount);        

		// category (from):
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("Category:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 120);
		comboCategoryFrom = new JComboBox<Category>();
		comboCategoryFrom.setName("comboCategoryFrom");
		comboCategoryFrom.setModel(new GenericComboBoxModel<Category>());
		GuiUtil.setPreferredWidth(comboCategoryFrom, 250);
		panel.add(comboCategoryFrom);

		buttonCategoryFrom = new JButton("...");
		buttonCategoryFrom.setName("buttonCategoryFrom");
		buttonCategoryFrom.addActionListener(this);
		panel.add(buttonCategoryFrom);

		// separator:
		panelContent.add(new JSeparator());

		// date:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("Date:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 120);
		spinnerDate = new JSpinner(new SpinnerDateModel());
		spinnerDate.setEditor(new JSpinner.DateEditor(spinnerDate, "dd.MM.yyyy HH:mm:ss"));
		spinnerDate.setName("spinnerDate");
		GuiUtil.setPreferredWidth(spinnerDate, 250);
		panel.add(spinnerDate);

		// no:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("No:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 120);
		textNo = new JTextField();
		textNo.setHorizontalAlignment(JTextField.RIGHT);
		textNo.setName("textNo");
		GuiUtil.setPreferredWidth(textNo, 250);
		panel.add(textNo);

		// amount:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("Amount:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 120);
		spinnerAmount = new JSpinner(new SpinnerNumberModel(0, 0, Double.MAX_VALUE, 5));
		spinnerAmount.setEditor(new JSpinner.NumberEditor(spinnerAmount, "0.00"));
		spinnerAmount.setName("spinnerAmount");
		GuiUtil.setPreferredWidth(spinnerAmount, 250);
		panel.add(spinnerAmount);        

		// separator:
		panelContent.add(new JSeparator());

		// account (to):
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("To:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 120);
		comboAccountTo = new JComboBox<Account>();
		comboAccountTo.setName("comboAccountTo");
		comboAccountTo.setModel(new GenericComboBoxModel<Account>());
		GuiUtil.setPreferredWidth(comboAccountTo, 250);
		panel.add(comboAccountTo);
		comboAccountTo.addItemListener(this);

		// category (to):
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("Category:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 120);
		comboCategoryTo = new JComboBox<Category>();
		comboCategoryTo.setName("comboCategoryTo");
		comboCategoryTo.setModel(new GenericComboBoxModel<Category>());
		GuiUtil.setPreferredWidth(comboCategoryTo, 250);
		panel.add(comboCategoryTo);        

		buttonCategoryTo = new JButton("...");
		buttonCategoryTo.setName("buttonCategoryTo");
		buttonCategoryTo.addActionListener(this);
		panel.add(buttonCategoryTo);

		// separator:
		panelContent.add(new JSeparator());
		
		// exchange rate:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("Exchange rate:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 120);
		spinnerExchangeRate = new JSpinner(new SpinnerNumberModel(0, 0, Double.MAX_VALUE, 1));
		spinnerExchangeRate.setEditor(new JSpinner.NumberEditor(spinnerExchangeRate, "0.00"));
		spinnerExchangeRate.setName("spinnerExchangeRate");
		GuiUtil.setPreferredWidth(spinnerExchangeRate, 250);
		panel.add(spinnerExchangeRate);
		
		// separator:
		panelContent.add(new JSeparator());
		
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

	private void populate()
	{
		@SuppressWarnings("unchecked")
		GenericComboBoxModel<Account> model = (GenericComboBoxModel<Account>)comboAccountTo.getModel();
		java.util.List<Account> accounts;
		Properties props;
		int id;

		textAccount.setText(account.getName());
		textNo.setText(account.nextNo());
		
		try
		{
			model.clear();
			accounts = factory.getAccounts();
			accounts.remove(account);
			model.add(accounts);
			model.selectFirst();

			populateCategories(true, null);
			populateCategories(false, null);

			if((props = Configuration.getProperties()) != null)
			{
				try
				{
					id = Integer.parseInt(props.getProperty("transfer.from_id"));
					selectCategoryById(id, comboCategoryFrom);
				}
				catch(NumberFormatException e) { }

				try
				{
					id = Integer.parseInt(props.getProperty("transfer.to_id"));
					selectCategoryById(id, comboCategoryTo);
				}
				catch(NumberFormatException e) { }
			}
		}
		catch(ProviderException e)
		{
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void populateCategories(boolean expenditure, Category selected)
	{
		GenericComboBoxModel<Category> model;

		if(expenditure)
		{
			model = (GenericComboBoxModel<Category>)comboCategoryFrom.getModel();
		}
		else
		{
			model = (GenericComboBoxModel<Category>)comboCategoryTo.getModel();
		}

		model.clear();

		try
		{
			model.add(factory.getCategories(expenditure));
		}
		catch(ProviderException e)
		{
			e.printStackTrace();
		}

		model.sort();
		model.selectFirst();

		if(selected != null)
		{
			model.setSelectedItem(selected);
		}
	}

	@SuppressWarnings("unchecked")
	private void editCategories(boolean expenditure)
	{
		CategoryDialog dialog;
		Category selectedCategory;
		JComboBox<Category> comboBox;
		GenericComboBoxModel<Category> model;

		comboBox = expenditure ? comboCategoryFrom : comboCategoryTo;
		model = (GenericComboBoxModel<Category>)comboBox.getModel();
		selectedCategory = (Category)comboBox.getSelectedItem();

		dialog = new CategoryDialog(null,
				                    selectedCategory,
				                    expenditure ? CategoryDialog.SHOW_EXPENSE : CategoryDialog.SHOW_INCOME);

		for(ICategoryListener listener : categoryListener)
		{
			dialog.addCategoryListener(listener);
		}

		dialog.open();

		if(dialog.getResult() == CategoryDialog.RESULT_OK)
		{
			populateCategories(expenditure, dialog.getSelectedCategory());
		}
		else
		{
			populateCategories(expenditure, selectedCategory);
		}
	
		if(comboBox.getSelectedIndex() == -1)
		{
			model.selectFirst();
		}
	}
	
	private void populateExchangeRate()
	{
		Account currentAccount;
		
		currentAccount = (Account)comboAccountTo.getSelectedItem();
		
		if(lastDestination == null || !currentAccount.equals(lastDestination))
		{
			if(currentAccount.getCurrency().equals(account.getCurrency()))
			{
				spinnerExchangeRate.setEnabled(false);
				spinnerExchangeRate.setValue(0.0);
			}
			else
			{
				spinnerExchangeRate.setEnabled(true);
				
				try
				{
					spinnerExchangeRate.setValue(exchangeUtil.getExchangeRate(account.getCurrency(), currentAccount.getCurrency()));
				}
				catch(ExchangeRateNotFoundException e)
				{
					spinnerExchangeRate.setValue(1.0);
				}
				catch(ExchangeRateUtilException e)
				{
					e.printStackTrace();
				}
			}

			lastDestination = currentAccount;
		}
	}

	private void transfer() throws ProviderException
	{
		Category categoryFrom;
		Category categoryTo;
		Date date;
		Double amount;
		String no;
		String remarks;
		Properties props;
		Account accountTo;

		accountTo = (Account)comboAccountTo.getSelectedItem();
		
		try
		{
			// update exchange rate:
			if(!account.getCurrency().equals(accountTo.getCurrency()))
			{
				exchangeUtil.updateExchangeRate(account.getCurrency(), accountTo.getCurrency(), (Double)spinnerExchangeRate.getValue());
			}

			// create transactions:
			categoryFrom = (Category)comboCategoryFrom.getSelectedItem();
			categoryTo = (Category)comboCategoryTo.getSelectedItem();
			date = ((SpinnerDateModel)spinnerDate.getModel()).getDate();
			amount = ((SpinnerNumberModel)spinnerAmount.getModel()).getNumber().doubleValue();
			no = textNo.getText();
			remarks = areaRemarks.getText();
			
			fromTransaction = account.createTransaction(categoryFrom, date, amount, no, remarks);
			toTransaction = accountTo.createTransaction(categoryTo, date, exchangeUtil.exchange(account.getCurrency(), accountTo.getCurrency(), amount), no, remarks);

			// save categories to configuration:
			props = Configuration.getProperties();
			props.setProperty("transfer.from_id", categoryFrom.getId().toString());
			props.setProperty("transfer.to_id", categoryTo.getId().toString());
	
			try
			{
				Configuration.storeProperties(props);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch(ExchangeRateUtilException e)
		{
			JOptionPane.showMessageDialog(this, "Couldn't transfer money, please try again.", "Transfer", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	private void selectCategoryById(int id, JComboBox<Category> comboBox)
	{
		@SuppressWarnings("unchecked")
		GenericComboBoxModel<Category> model = (GenericComboBoxModel<Category>)comboBox.getModel();

		for(int i = 0; i < model.getSize(); ++i)
		{
			if(((Category)model.getElementAt(i)).getId() == id)
			{
				comboBox.setSelectedIndex(i);
				break;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource().equals(buttonClose))
		{
			result = RESULT_CLOSE;
			close();
		}
		else if(event.getSource().equals(buttonApply))
		{
			try
			{
				transfer();
				result = RESULT_APPLY;
				close();
			}
			catch(ProviderException e)
			{
				result = RESULT_FAILURE;
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Couldn't transfer money, please try again.", "Transfer", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(event.getSource().equals(buttonCategoryFrom))
		{
			editCategories(true);
		}
		else if(event.getSource().equals(buttonCategoryTo))
		{
			editCategories(false);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event)
	{
		if(event.getSource().equals(comboAccountTo))
		{
			populateExchangeRate();
		}
	}
}