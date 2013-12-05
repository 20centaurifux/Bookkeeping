/***************************************************************************
    begin........: January 2012
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
package accounting.application;

import java.util.Date;

import accounting.application.annotation.*;
import accounting.data.ProviderException;

@SuppressWarnings("unused")
public class Template extends AEntity<Long> implements Comparable<Template>
{
	@Attribute(name="Name", readable=true, writeable=true)
	@StringValidator(allowNull=false, minLength=0, maxLength=32)
	private String name;
	@Attribute(name="Category", readable=true, writeable=true)
	@ObjectValidator(allowNull=false)
	private Category category;
	@Attribute(name="Amount", readable=true, writeable=true)
	@DoubleValidator(allowNull=false, min=-Float.MAX_VALUE, max=Float.MAX_VALUE)
	double amount;
	@Attribute(name="Remarks", readable=true, writeable=true)
	@StringValidator(allowNull=true, minLength=0, maxLength=512)
	private String remarks;

	@Override
	protected void update() throws ProviderException
	{
		provider.updateTemplate(this);
	}

	@Override
	protected void remove() throws ProviderException, ReferenceException
	{
		provider.deleteTemplate(getId());
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public String getName()
	{
		return getString("Name");
	}

	public void setName(String name) throws AttributeException
	{
		setAttribute("Name", name);
	}

	public Category getCategory()
	{
		return (Category)getObject("Category");
	}

	public void setCategory(Category category) throws AttributeException
	{
		if(this.category != null && category.isExpenditure() != this.category.isExpenditure())
		{
			amount *= -1;
		}

		setAttribute("Category", category);
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
			setAttribute("Amount", category.isExpenditure() ? amount * -1 : amount);
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

	@Override
	public int compareTo(Template template)
	{
		return getId().compareTo(template.getId());
	}
}