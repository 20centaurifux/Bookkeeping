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

import accounting.application.annotation.*;
import accounting.data.ProviderException;

@SuppressWarnings("unused")
public final class Currency extends AEntity<Long> implements Comparable<Currency>
{
	@Attribute(name="Name", readable=true, writeable=true)
	@StringValidator(minLength=1, maxLength=32, allowNull=false)
	private String name;

	@Override
	protected void update() throws ProviderException
	{
		provider.updateCurrency(this);
	}

	@Override
	protected void remove() throws ProviderException, ReferenceException
	{
		if(provider.countCurrencyReferences(this.getId()) > 0)
		{
			throw new ReferenceException();
		}

		provider.deleteCurrency(this.getId());
	}

	public String getName()
	{
		return getString("Name");
	}

	public void setName(String name) throws AttributeException
	{
		setAttribute("Name", name);
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public boolean equals(Object object)
	{
		if(object instanceof Currency)
		{
			return ((Currency)object).getId().equals(getId()); 
		}

		return false;
	}

	@Override
	public int compareTo(Currency currency)
	{
		return toString().compareToIgnoreCase((currency.toString()));
	}
}