/***************************************************************************
    begin........: January 2012
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
package accounting.application;

import java.util.Date;
import accounting.application.annotation.*;
import accounting.data.ProviderException;

public class Transaction extends AEntity<Long>
{
	@Attribute(name="Account", readable=true, writeable=true)
	@ObjectValidator(allowNull=false)
	Account account;
	@Attribute(name="Date", readable=true, writeable=true)
	private Date date;
	@Attribute(name="Category", readable=true, writeable=true)
	@ObjectValidator(allowNull=false)
	private Category category;
	@Attribute(name="Amount", readable=true, writeable=true)
	@DoubleValidator(allowNull=false, min=-Float.MAX_VALUE, max=Float.MAX_VALUE)
	double amount;
	@Attribute(name="No", readable=true, writeable=true)
	@StringValidator(allowNull=true, minLength=0, maxLength=32)
	private String no;
	@Attribute(name="Remarks", readable=true, writeable=true)
	@StringValidator(allowNull=true, minLength=0, maxLength=512)
	private String remarks;

	@Override
	protected void update() throws ProviderException
	{
		provider.updateTransaction(this);
	}

	@Override
	protected void remove() throws ProviderException, ReferenceException
	{
		provider.deleteTransaction(getId());
	}

	@Override
	public String toString()
	{
		return getRemarks();
	}

	public Account getAccount()
	{
		return (Account)getObject("Account");
	}

	public void setAccount(Account account) throws AttributeException
	{
		setAttribute("Account", account);
	}

	public Category getCategory()
	{
		return (Category)getObject("Category");
	}

	public void setCategory(Category category) throws AttributeException
	{
		if(this.category != null && category.isExpenditure() != this.category.isExpenditure())
		{
			amount *= -1.0;
		}

		setAttribute("Category", category);
	}

	public Date getDate()
	{
		return (Date)getObject("Date");
	}

	public void setDate(Date date) throws AttributeException
	{
		setAttribute("Date", date);
	}

	public String getNo()
	{
		return getString("No");
	}

	public void setNo(String no) throws AttributeException
	{
		setAttribute("No", no);
	}

	public String getRemarks()
	{
		return getString("Remarks");
	}

	public void setRemarks(String remarks) throws AttributeException
	{
		setAttribute("Remarks", remarks);
	}

	public void setAmount(double amount) throws AttributeException
	{
		if(category != null)
		{
			setAttribute("Amount", category.isExpenditure() ? Math.abs(amount) * -1.0 : Math.abs(amount));
		}
		else
		{
			setAttribute("Amount", amount);
		}
	}

	public double getIncome()
	{
		if(!category.isExpenditure())
		{
			return amount;
		}

		return 0;
	}

	public double getRebate()
	{
		if(category.isExpenditure())
		{
			return Math.abs(amount);
		}

		return 0;
	}
}