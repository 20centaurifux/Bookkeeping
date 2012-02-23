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

import accounting.Translation;
import accounting.application.annotation.*;
import accounting.data.ProviderException;

public class Category extends AEntity<Long> implements Comparable<Category>
{
	@Attribute(name="Name", readable=true, writeable=true)
	@StringValidator(allowNull=false, minLength=1, maxLength=32)
	private String name;
	@Attribute(name="Expenditure", readable=true, writeable=true)
	private boolean expenditure;
	private static Translation translation;

	public Category()
	{
		super();

		if(translation == null)
		{
			translation = new Translation();
		}
	}

	@Override
	protected void update() throws ProviderException
	{
		provider.updateCategory(this);
	}

	@Override
	protected void remove() throws ProviderException, ReferenceException
	{
		if(provider.countCategoryReferences(this.getId()) > 0)
		{
			throw new ReferenceException();
		}

		provider.deleteCategory(this.getId());
	}

	public String getName()
	{
		return getString("Name");
	}

	public void setName(String name) throws AttributeException
	{
		setAttribute("Name", name);
	}
	
	public boolean isExpenditure()
	{
		return getBool("Expenditure");
	}

	public void setExpenditure(boolean expenditure)
	{
		try
		{
			setAttribute("Expenditure", expenditure);
		}
		catch(AttributeException e) { }
	}

	@Override
	public String toString()
	{
		if(expenditure)
		{
			return translation.translate("Expenses") + ": " + name;
		}
		else
		{
			return translation.translate("Income") + ": " + name;
		}
	}

	@Override
	public boolean equals(Object object)
	{
		if(object instanceof Category)
		{
			return ((Category)object).getId().equals(getId());
		}

		return false;
	}

	@Override
	public int compareTo(Category category)
	{
		return toString().compareToIgnoreCase(category.toString());
	}
}