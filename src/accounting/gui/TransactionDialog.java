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

public class TransactionDialog extends ADialog implements ActionListener
{
	public static final int RESULT_CLOSE = 0;
	public static final int RESULT_APPLY = 1;
	public static final int RESULT_CANCEL = 2;

	private static final long serialVersionUID = 8848635910124681241L;
	private Factory factory;
	private Container contentPane;
	private Transaction transaction;
	private JTextField textNo;
	private JComboBox comboCategory;
	private JTextArea areaRemarks;
	private JButton buttonClose;
	private JButton buttonApply;
	private JTextField textAccount;
	private JSpinner spinnerDate;
	private JButton buttonCategory;
	private JSpinner spinnerAmount;
	private int result = RESULT_CLOSE;
	private Vector<ICategoryListener> listener = new Vector<ICategoryListener>();
	private Translation translation;
	private Account account;

	public TransactionDialog(JFrame parent, String title, Transaction transaction)
	{
			super(parent, title);

			this.transaction = transaction;
			account = transaction.getAccount();

			populateCategoryBox();
			populateTransaction();
    }

	public TransactionDialog(JFrame parent, String title, Account account)
	{
			super(parent, title);

			this.account = account;
			transaction = null;

			populateCategoryBox();

			textAccount.setText(account.getName());
			spinnerDate.setValue(Calendar.getInstance().getTime());
			comboCategory.setSelectedIndex(0);
    }

	public int getResult()
	{
		return result;
	}

	public Transaction getTransaction()
	{
		return transaction;
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

		label = new JLabel("Account:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 70);
		textAccount = new JTextField();
		textAccount.setName("textAccount");
		textAccount.setEnabled(false);
		GuiUtil.setPreferredWidth(textAccount, 250);
		panel.add(textAccount);        

		// date:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);

		label = new JLabel("Date:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 70);
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
		GuiUtil.setPreferredWidth(label, 70);
		textNo = new JTextField();
		textNo.setHorizontalAlignment(JTextField.RIGHT);
		GuiUtil.setPreferredWidth(textNo, 250);
		panel.add(textNo);

		// category:
		panelContent.add(Box.createHorizontalBox());
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelContent.add(panel);
		
		label = new JLabel("Category:");
		panel.add(label);
		GuiUtil.setPreferredWidth(label, 70);
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
		GuiUtil.setPreferredWidth(label, 70);
		spinnerAmount= new JSpinner(new SpinnerNumberModel(0, 0, Double.MAX_VALUE, 5));
		spinnerAmount.setEditor(new JSpinner.NumberEditor(spinnerAmount, "0.00"));
		spinnerAmount.setName("spinnerAmount");
		GuiUtil.setPreferredWidth(spinnerAmount, 250);
		panel.add(spinnerAmount);        
		
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

	private void populateTransaction()
	{
		textNo.setText(transaction.getNo());
		textAccount.setText(transaction.getAccount().getName());
		comboCategory.setSelectedItem(transaction.getCategory());
		spinnerDate.setValue(transaction.getDate());
		spinnerAmount.setValue(transaction.getCategory().isExpenditure() ? transaction.getRebate() : transaction.getIncome());
		areaRemarks.setText(transaction.getRemarks());
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
				comboCategory.setSelectedItem(category);
			}
		}

		// select first category if selected category has been removed:
		if(comboCategory.getSelectedIndex() == -1)
		{
			comboCategory.setSelectedIndex(0);
		}
	}

	private boolean saveTransaction()
	{
		String message = null;

		if(transaction == null)
		{
			try
			{
				transaction = account.createTransaction(factory.getCategories(true).get(0), Calendar.getInstance().getTime(), 0.0, null, null);
			}
			catch(ProviderException e)
			{
				message = "Couldn't save transaction, an internal error occured. Please try again later.";
				e.printStackTrace();
			}
		}

		if(transaction != null)
		{
			try
			{
				transaction.setDate(((SpinnerDateModel)spinnerDate.getModel()).getDate());
			}
			catch(AttributeException e)
			{
				message = "The given date is invalid, please check your data.";
			}
	
			try
			{
				transaction.setNo(textNo.getText());
			}
			catch(AttributeException e)
			{
				message = "The given no is invalid, please check your data.";
			}
	
			try
			{
				transaction.setCategory((Category)comboCategory.getSelectedItem());
			}
			catch(AttributeException e)
			{
				message = "The selected category is invalid, please check your data.";
			}
	
			try
			{
				transaction.setAmount(((SpinnerNumberModel)spinnerAmount.getModel()).getNumber().doubleValue());
			}
			catch(AttributeException e)
			{
				message = "The entered amount is invalid, please check your data.";
			}
	
			try
			{
				transaction.setRemarks(areaRemarks.getText());
			}
			catch(AttributeException e)
			{
				message = "The entered remarks are invalid, please check your data.";
			}
		
			if(message == null)
			{
				try
				{
					transaction.save();
				}
				catch(ProviderException e)
				{
		            message = "Couldn't save transaction, an internal error occured. Please try again later.";
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
        		if(saveTransaction())
        		{
        			close(RESULT_APPLY);
        		}
        	}
        	else if(event.getSource().equals(buttonCategory))
        	{
        		editCategories();
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
}