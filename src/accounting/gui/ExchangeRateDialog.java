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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.picocontainer.PicoContainer;
import accounting.*;
import accounting.application.*;
import accounting.data.ProviderException;

public class ExchangeRateDialog extends ADialog implements ActionListener
{
	private static final long serialVersionUID = 4211144843728803150L;
	private Factory factory;
	private Container contentPane;
	private JComboBox<Currency> comboCurrency;
	private JTable tableCurrencies;
    private JButton buttonSave;
    private JButton buttonClose;
    private Translation translation;
    private PicoContainer pico;
    private ExchangeUtil util;

    private class CurrenciesTableModel extends AbstractTableModel
    {
		private static final long serialVersionUID = 8393773962709370547L;

		public class ExchangeRate
    	{
    		public Currency to;
    		public Double rate;
    	}
    	
    	private List<ExchangeRate> data = new ArrayList<ExchangeRate>();

		@Override
		public int getRowCount()
		{
			return data.size();
		}

		@Override
		public int getColumnCount()
		{
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if(columnIndex == 0)
			{
				return data.get(rowIndex).to;
			}
			else
			{
				return data.get(rowIndex).rate;
			}
		}
    	
		public void setValueAt(Object value, int row, int column)
		{
			ExchangeRate rate = data.get(row);
		
			if(column == 0)
			{
				if(rate.to != (Currency)value)
				{
					rate.to = (Currency)value;
					setEdited(true);
				}
			}
			else
			{
				if(value == null)
				{
					value = 0.0;
				}
				
				if(!rate.rate.equals(value))
				{
					rate.rate = (Double)value;
					setEdited(true);
				}
			}

			fireTableDataChanged();
		}
		
		public boolean isCellEditable(int row, int column)
		{
			if(column == 1)
			{
				return true;
			}
			
			return false;
		}
		
		public void clear()
		{
			data.clear();
			fireTableDataChanged();
		}
		
		public void addCurrency(Currency from, Currency to)
		{
			ExchangeRate rate = new ExchangeRate();

			rate.to = to;
			rate.rate = 0.0;

			try
			{
				rate.rate = util.getExchangeRate(from, to);

				if(rate.rate == null)
				{
					rate.rate = 0.0;
				}
			}
			catch(ExchangeRateNotFoundException e)
			{
				/* ignore */
			}
			catch(ExchangeRateUtilException e)
			{
				rate.rate = 0.0;
				e.printStackTrace();
			}

			data.add(rate);

			fireTableDataChanged();
		}
    }
    
    private class ExchangeRateCellEditor extends AbstractCellEditor implements TableCellEditor
    {
		private static final long serialVersionUID = 4453531828277768648L;
		private JSpinner spinner = null;
    	private double currentValue;
    	private double lastValue;
    	
    	public ExchangeRateCellEditor()
    	{
			spinner = new JSpinner();
			spinner = new JSpinner(new SpinnerNumberModel(0, 0, Double.MAX_VALUE, 0.1));
			spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.00"));
		}
    	
    	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    	{   
    		spinner.setValue(0.0);
 
    		if(value != null)
    		{
    			spinner.setValue(value);
    		}
    		
    		currentValue = (Double)value;
 
    		if(lastValue == 0.0 && lastValue == currentValue)
    		{
    			/*
    			 * I found a strange Swing related behavior here: the spinner is set to zero
    			 * explicitly but still shows the entered number. This problem only occurs
    			 * when the previous table cell contained zero and the user aborts editing
    			 * by selecting another zero cell. Either I'm stupid or this is a glitch. Anyway,
    			 * this is my hackish but working solution:
    			 */
    			SwingUtilities.invokeLater(new Runnable()
    			{
    				public void run()
    				{
    					spinner.setValue(0.0);
    				}
    			});
    		}
    		
    		return spinner;
    	}
    	
		@Override
		public Object getCellEditorValue()
		{
			return spinner.getValue();
		}
		
		public boolean stopCellEditing()
		{
			lastValue = currentValue;
			
			return super.stopCellEditing();
		}
    }
    
    private class CurrenciesCellRenderer implements TableCellRenderer
    {
    	private DecimalFormat decimalFormat = new DecimalFormat("0.00");

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			JLabel label = null;

			if(value != null)
			{
				label = new JLabel(String.format("<html><strong>%s</strong></html>", decimalFormat.format((Double)value)));
				label.setHorizontalAlignment(JLabel.RIGHT);
			}
			
			return label;
		}
    }
    
	public ExchangeRateDialog(JFrame parent)
	{
		super(parent, "Exchange rates");
		populateCurrencies();
	}

	@SuppressWarnings("unchecked")
	protected void initialize()
	{
		BorderLayout borderLayout;
		JPanel panel;
		TableColumnModel columns;

		translation = new Translation();

		setTitle(translation.translate(getTitle()));

		try
		{
			pico = Injection.getContainer();
			factory = pico.getComponent(Factory.class);
			util = pico.getComponent(ExchangeUtil.class);
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
		
		// currency list:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		contentPane.add(panel, BorderLayout.NORTH);

		panel.add(new JLabel("Currency:"));

		comboCurrency = new JComboBox<Currency>();
		comboCurrency.setModel(new GenericComboBoxModel<Currency>());
		GuiUtil.setPreferredWidth(comboCurrency, 150);
		panel.add(comboCurrency);
		
		comboCurrency.addItemListener(new ItemListener()
		{	
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				populateExchangeRates();
			}
		});

		// exchange rates:
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		contentPane.add(panel, BorderLayout.CENTER);
		
		tableCurrencies = new JTable(new CurrenciesTableModel());
		tableCurrencies.putClientProperty("terminateEditOnFocusLost", true);
		
		columns = tableCurrencies.getColumnModel();
		columns.getColumn(0).setHeaderValue(translation.translate("Currency"));
		columns.getColumn(1).setHeaderValue(translation.translate("Exchange rate"));		
		columns.getColumn(1).setCellEditor(new ExchangeRateCellEditor());
		columns.getColumn(1).setCellRenderer(new CurrenciesCellRenderer());

		tableCurrencies.setColumnSelectionAllowed(false);
		tableCurrencies.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableCurrencies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(new JScrollPane(tableCurrencies), BorderLayout.CENTER);
		
		// button panel:
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		contentPane.add(panel, BorderLayout.SOUTH);
		
		buttonSave = new JButton("Save");
		buttonSave.setName("buttonSave");
		buttonSave.addActionListener(this);
		panel.add(buttonSave);
		
		buttonClose = new JButton("Close");
		buttonClose.setName("buttonClose");
		buttonClose.addActionListener(this);
		panel.add(buttonClose);
	}
	
	@SuppressWarnings("unchecked")
	private void populateCurrencies()
	{
		GenericComboBoxModel<Currency> model;
		
		model = (GenericComboBoxModel<Currency>)comboCurrency.getModel();
		
		try
		{
			model.add(factory.getCurrencies());
			model.selectFirst();
		}
		catch(ProviderException e)
		{
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private void populateExchangeRates()
	{
		Currency base;
		JPanel panel;
		CurrenciesTableModel model = (CurrenciesTableModel)tableCurrencies.getModel();

		model.clear();
		
		try
		{
			base = (Currency)comboCurrency.getSelectedItem();

			for(Currency c : factory.getCurrencies())
			{
				if(c.equals(base))
				{
					continue;
				}
				
				model.addCurrency(base, c);
			}
		}
		catch(ProviderException e)
		{
			e.printStackTrace();
		}
		finally
		{
			setEdited(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(buttonClose))
		{
			close();
		}
		else if(e.getSource().equals(buttonSave))
		{
			saveExchangeRates();
		}
	}

	private void setEdited(boolean edited)
	{
		if(edited)
		{
			this.comboCurrency.setToolTipText(translation.translate("Please save your changes before you select a different currency."));
			this.comboCurrency.setEnabled(false);
		}
		else
		{
			this.comboCurrency.setToolTipText(null);
			this.comboCurrency.setEnabled(true);
		}
	}
	
	private void saveExchangeRates()
	{
		Currency base;
		CurrenciesTableModel model = (CurrenciesTableModel)tableCurrencies.getModel();
		
		base = (Currency)comboCurrency.getSelectedItem();
		
		for(int i = 0; i < model.getRowCount(); i++)
		{
			try
			{
				util.updateExchangeRate(base, (Currency)model.getValueAt(i, 0), (Double)model.getValueAt(i, 1));
				setEdited(false);
			}
			catch (ExchangeRateUtilException e)
			{
				JOptionPane.showMessageDialog(this, "Couldn't update exchange rates, please try again.", "Exchange rates", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				break;
			}
		}
	}
}