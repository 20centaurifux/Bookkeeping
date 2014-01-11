/***************************************************************************
    begin........: January 2012
    copyright....: Sebastian Fedrau
    email........: sebastian.fedrau@gmail.com
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
package accounting.export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import accounting.Translation;
import accounting.application.Account;
import accounting.application.Transaction;

public class CSVExport extends AExport
{
	private Translation translation;

	public CSVExport()
	{
			translation = new Translation();
	}
	
	@Override
	public String getName()
	{
		return "CSV Export";
	}

	@Override
	public String getDescription()
	{
		return "Export data to CSV file.";
	}

	@Override
	public void exportMonth(Account account, int year, int month)
	{
		try
		{
			List<Transaction> transactions = account.getTransactions(getFirstOfMonth(year, month), getLastOfMonth(year, month));
			export(transactions);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void export(List<Transaction> transactions)
	{
		JFileChooser chooser = new JFileChooser();
		int result;
		
		// select file:
		chooser.setFileFilter(new FileNameExtensionFilter("Comma-separated values (*.csv)", "csv"));
		chooser.setVisible(true);

		result = chooser.showSaveDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION)
		{
			// get absolute path:
			String path = chooser.getSelectedFile().getAbsolutePath();
			
			if(!path.endsWith(".csv"))
			{
				path += ".csv";
			}
			
			// write file:
			writeFile(path, transactions);
		}		
	}
	
	private void writeFile(String path, List<Transaction> transactions)
	{
		FileWriter writer = null;
		PrintWriter out = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("\"yyyy-MM-dd kk:mm:ss\"");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		DecimalFormat decimalFormat;

		symbols.setDecimalSeparator('.');
		decimalFormat = new DecimalFormat("0.00", symbols);

		try
		{			
			writer = new FileWriter(path);
			out = new PrintWriter(writer);

			// write column names:
			out.println("Date,No,Remarks,Category,Income,Rebate");

			// write transactions:
			for(Transaction t : transactions)
			{
				out.println(String.format("%s,%s,%s,%s,%s,%s",
						dateFormat.format(t.getDate()), t.getNo(), quoteString(t.getRemarks()), quoteString(t.getCategory().getName()),
						decimalFormat.format(t.getIncome()), decimalFormat.format(t.getRebate())));
			}
			
			JOptionPane.showMessageDialog(null, translation.translate("Data exported successfully."), translation.translate("CSV export"), JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			
			JOptionPane.showMessageDialog(null, translation.translate("Data export failed."), translation.translate("CSV export"), JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			if(out != null)
			{
				out.flush();
				out.close();
			}
			
			if(writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	String quoteString(String text)
	{
		return String.format("\"%s\"", text.replaceAll("\"", "\"\""));
	}
}