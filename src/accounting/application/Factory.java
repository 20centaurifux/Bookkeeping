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

import java.util.List;

import org.picocontainer.annotations.Inject;

import accounting.data.*;

public final class Factory
{
	@Inject protected IProvider provider;

	public Currency createCurrency(String name) throws ProviderException
	{
		return provider.createCurrency(name);
	}

	public Currency getCurrency(long id) throws ProviderException
	{
		return provider.getCurrency(id);
	}

	public List<Currency> getCurrencies() throws ProviderException
	{
		return provider.getCurrencies();
	}

	public Category createCategory(String name, boolean expenditure) throws ProviderException
	{
		return provider.createCategory(name, expenditure);
	}

	public Category getCategory(long id) throws ProviderException
	{
		return provider.getCategory(id);
	}

	public List<Category> getCategories(boolean expenditure) throws ProviderException
	{
		return provider.getCategories(expenditure);
	}

	public int countCategories(boolean expenditure) throws ProviderException
	{
		return provider.countCategories(expenditure);
	}

	public Account createAccount(String name, String remarks, Currency currency, String noPrefix, int currentNo) throws ProviderException
	{
		return provider.createAccount(name, remarks, currency, noPrefix, currentNo);
	}

	public Account getAccount(long id) throws ProviderException
	{
		return provider.getAccount(id);
	}

	public List<Account> getAccounts() throws ProviderException
	{
		return provider.getAccounts();
	}
	
	public Template createTemplate(String name, Category category, Double amount, Currency currency, String remarks) throws ProviderException
	{
		return provider.createTemplate(name, category, amount, currency, remarks);
	}
	
	public Template getTemplate(long id) throws ProviderException
	{
		return provider.getTemplate(id);
	}
	
	public List<Template> getTemplates() throws ProviderException
	{
		return provider.getTemplates();
	}
}