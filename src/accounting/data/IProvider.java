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
package accounting.data;

import java.util.Date;
import java.util.List;

import accounting.application.*;

public interface IProvider
{
	Currency createCurrency(String description) throws ProviderException;
	Currency getCurrency(long id) throws ProviderException;
	void updateCurrency(Currency currency) throws ProviderException;
	void deleteCurrency(long id) throws ProviderException;
	List<Currency> getCurrencies() throws ProviderException;
	int countCurrencyReferences(long id) throws ProviderException;
	Category createCategory(String name, boolean expenditure) throws ProviderException;
	Category getCategory(long id) throws ProviderException;
	void updateCategory(Category category) throws ProviderException;
	void deleteCategory(long id) throws ProviderException;
	List<Category> getCategories(boolean expenditure) throws ProviderException;
	int countCategoryReferences(long id) throws ProviderException;
	int countCategories(boolean expenditure) throws ProviderException;
	Account createAccount(String name, String remarks, Currency currency, String noPrefix, int noLength) throws ProviderException;
	Account getAccount(long id) throws ProviderException;
	void updateAccount(Account account) throws ProviderException;
	void deleteAccount(long id) throws ProviderException;
	List<Account> getAccounts() throws ProviderException;
	Transaction createTransaction(Account account, Category category, Date date, Double amount, String no, String remarks) throws ProviderException;
	Transaction getTransaction(long id, Account account) throws ProviderException;
	List<Transaction> getTransactions(Account account, long begin, long end) throws ProviderException;
	void updateTransaction(Transaction transaction) throws ProviderException;
	void deleteTransaction(long id) throws ProviderException;
	List<Long> getTimestamps(long accountId) throws ProviderException;
	double getBalance(long accountId, Date date) throws ProviderException;
	boolean transactionNoExists(String no) throws ProviderException;
	Template createTemplate(String name, Category category, Double amount, Currency currency, String remarks) throws ProviderException;
	Template getTemplate(long id) throws ProviderException;
	void updateTemplate(Template template) throws ProviderException;
	void deleteTemplate(long id) throws ProviderException;
	List<Template> getTemplates() throws ProviderException;
	boolean exchangeRateExists(Currency from, Currency to) throws ProviderException;
	double getExchangeRate(Currency from, Currency to) throws ProviderException;
	void updateExchangeRate(Currency from, Currency to, double rate) throws ProviderException;
}