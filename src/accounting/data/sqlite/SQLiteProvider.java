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
package accounting.data.sqlite;

import java.sql.*;
import java.util.*;
import java.util.Date;
import org.picocontainer.MutablePicoContainer;
import accounting.*;
import accounting.application.*;
import accounting.application.Currency;
import accounting.data.*;

public class SQLiteProvider implements IProvider
{
	private MutablePicoContainer pico;
	private static boolean initialized = false;
	private ConnectionPool pool;
	private String connectionString;

	public SQLiteProvider(String connectionString) throws ClassNotFoundException, SQLException, ProviderException
	{
		pico = Injection.getContainer();
		pool = ConnectionPool.getInstance();
		this.connectionString = connectionString;

		if(!initialized)
		{
			init();
			createInitialData();
		}
	}

	/*
	 * currencies:
	 */
	@Override
	public Currency createCurrency(String name) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		Currency currency = null;

		try
		{
			currency = pico.getComponent(Currency.class);
			currency.setName(name);
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "INSERT INTO currency (description, deleted) VALUES (?, 0)", new Object[]{ name });
			stat.execute();
			currency.setId(getLastInsertId(conn));
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		catch(AttributeException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return currency;
	}

	@Override
	public Currency getCurrency(long id) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		ResultSet result;
		Currency currency = null;

		try
		{
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "SELECT description FROM currency WHERE id=? AND deleted=0", new Object[] { id });
			result = stat.executeQuery();

			if(result.next())
			{
				currency = pico.getComponent(Currency.class);
				currency.setId(id);
				currency.setName(result.getString(1));
			}
			else
			{
				throw new ProviderException("Couldn't find currency.");
			}

			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		catch(AttributeException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return currency;
	}

	@Override
	public void updateCurrency(Currency currency) throws ProviderException
	{
		Connection conn = null;

		try
		{
			conn = pool.getConnection(connectionString);
			prepareAndExecuteStatement(conn, "UPDATE currency SET description=? WHERE id=?", new Object[]{ currency.getName(), currency.getId() });
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}
	}

	@Override
	public void deleteCurrency(long id) throws ProviderException
	{
		deleteEntity("currency", id);
	}

	@Override
	public List<Currency> getCurrencies() throws ProviderException
	{
		Connection conn = null;
		Statement stat;
		ResultSet result;
		List<Currency> currencies = new LinkedList<Currency>();
 
		try
		{
			conn = pool.getConnection(connectionString);
			stat = conn.createStatement();
			result = stat.executeQuery("SELECT id FROM currency WHERE deleted=0 ORDER BY description");
	
			while(result.next())
			{
				currencies.add(getCurrency(result.getLong(1)));
			}
	
			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}

		return currencies;
	}

	@Override
	public int countCurrencyReferences(long id) throws ProviderException
	{
		Object result;

		if((result = executeScalar("SELECT COUNT(id) FROM account WHERE account.deleted=0 AND currency_id=?", new Object[]{ id })) != null)
		{
			return (Integer)result;
		}

		return 0;
	}

	/*
	 * categories:
	 */
	@Override
	public Category createCategory(String name, boolean expenditure) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		Category category = null;

		try
		{
			category = pico.getComponent(Category.class);
			category.setName(name);
			category.setExpenditure(expenditure);
			
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "INSERT INTO category (description, expenditure, deleted) VALUES (?, ?, 0)", new Object[]{ name, expenditure });
			stat.execute();
			category.setId(getLastInsertId(conn));
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		catch(AttributeException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return category;
	}

	@Override
	public Category getCategory(long id) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		ResultSet result;
		Category category = null;

		try
		{
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "SELECT description, expenditure FROM category WHERE id=? AND deleted=0", new Object[] { id });
			result = stat.executeQuery();

			if(result.next())
			{
				category = pico.getComponent(Category.class);
				category.setId(id);
				category.setName(result.getString(1));
				category.setExpenditure(result.getBoolean(2));
			}
			else
			{
				throw new ProviderException("Couldn't find category.");
			}

			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		catch(AttributeException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return category;
	}

	@Override
	public void updateCategory(Category category) throws ProviderException
	{
		Connection conn = null;

		try
		{
			conn = pool.getConnection(connectionString);
			prepareAndExecuteStatement(conn, "UPDATE category SET description=?, expenditure=? WHERE id=?", new Object[]{ category.getName(), category.isExpenditure(), category.getId() });
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}
	}

	@Override
	public void deleteCategory(long id) throws ProviderException
	{
		deleteEntity("category", id);
	}

	@Override
	public List<Category> getCategories(boolean expenditure) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		ResultSet result;
		List<Category> categories = new LinkedList<Category>();
 
		try
		{
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "SELECT id FROM category WHERE deleted=0 AND expenditure=? ORDER BY \"name\"", new Object[] { expenditure });
			result = stat.executeQuery();
	
			while(result.next())
			{
				categories.add(getCategory(result.getLong(1)));
			}
	
			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}

		return categories;
	}

	@Override
	public int countCategoryReferences(long id) throws ProviderException
	{
		Object result;

		if((result = executeScalar("SELECT COUNT(id) FROM record WHERE deleted=0 AND category_id=?", new Object[]{ id })) != null)
		{
			return (Integer)result;
		}

		return 0;
	}

	@Override
	public int countCategories(boolean expenditure) throws ProviderException
	{
		Object result;

		if((result = executeScalar("SELECT COUNT(id) FROM category WHERE deleted=0 AND expenditure=?", new Object[]{ expenditure })) != null)
		{
			return (Integer)result;
		}

		return 0;
	}

	/*
	 * accounts:
	 */
	@Override
	public Account createAccount(String name, String remarks, Currency currency) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		Account account = null;

		try
		{
			account = pico.getComponent(Account.class);
			account.setName(name);
			account.setRemarks(remarks);
			account.setCurrency(currency);
			
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "INSERT INTO account (name, remarks, currency_id, deleted) VALUES (?, ?, ?, 0)", new Object[]{ name, remarks, currency == null ? null : currency.getId() });
			stat.execute();
			account.setId(getLastInsertId(conn));
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		catch(AttributeException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return account;
	}

	@Override
	public Account getAccount(long id) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		ResultSet result;
		Account account = null;
		Currency currency = null;

		try
		{
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "SELECT account.name, remarks, currency_id, description FROM account LEFT JOIN currency ON account.currency_id=currency.id WHERE account.id=? AND account.deleted=0", new Object[] { id });
			result = stat.executeQuery();

			if(result.next())
			{
				account = pico.getComponent(Account.class);
				account.setId(id);
				account.setName(result.getString(1));
				account.setRemarks(result.getString(2));

				if(result.getObject(3) != null)
				{
					currency = pico.getComponent(Currency.class);
					currency.setId(result.getLong(3));
					currency.setName(result.getString(4));
					account.setCurrency(currency);
				}
			}
			else
			{
				throw new ProviderException("Couldn't find account.");
			}

			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		catch(AttributeException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return account;
	}

	@Override
	public void updateAccount(Account account) throws ProviderException
	{
		Connection conn = null;

		try
		{
			conn = pool.getConnection(connectionString);
			prepareAndExecuteStatement(conn, "UPDATE account SET name=?, remarks=?, currency_id=? WHERE id=?", new Object[]{ account.getName(), account.getRemarks(), account.getCurrency() == null ? null : account.getCurrency().getId(), account.getId() });
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}
	}

	@Override
	public void deleteAccount(long id) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;

		try
		{
			conn = pool.getConnection(connectionString);
			conn.setAutoCommit(false);

			stat = prepareStatement(conn, "UPDATE account SET deleted=1 WHERE id=?", new Object[]{ id });
			stat.execute();
			stat.close();

			stat = prepareStatement(conn, "UPDATE record SET deleted=1 WHERE account_id=?", new Object[]{ id });
			stat.execute();
			stat.close();

			conn.commit();
		}
		catch(SQLException e0)
		{
			try
			{
				conn.rollback();
			}
			catch(Exception e1) { }
			throw new ProviderException(e0);
		}
		finally
		{
			try
			{
				conn.setAutoCommit(true);
			}
			catch(Exception e) { };
			pool.closeConnection(conn);
		}
	}

	public List<Account> getAccounts() throws ProviderException
	{
		Connection conn = null;
		Statement stat;
		ResultSet result;
		LinkedList<Account> accounts = new LinkedList<Account>();

		try
		{
			conn = pool.getConnection(connectionString);
			stat = conn.createStatement();
			result = stat.executeQuery("SELECT id FROM account WHERE deleted=0 ORDER BY name");

			while(result.next())
			{
				accounts.add(getAccount(result.getLong(1)));
			}

			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return accounts;
	}

	/*
	 * transactions:
	 */
	public Transaction createTransaction(Account account, Category category, Date date, Double amount, String no, String remarks) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		Transaction transaction = null;

		try
		{
			transaction = pico.getComponent(Transaction.class);
			transaction.setAccount(account);
			transaction.setCategory(category);
			transaction.setDate(date);
			transaction.setAmount(amount);
			transaction.setRemarks(remarks);
			transaction.setNo(no);
			
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "INSERT INTO record (account_id, category_id, date, amount, no, remarks, deleted) VALUES (?, ?, ?, ?, ?, ?, 0)",
			                              new Object[]{ account.getId(), category.getId(), date, category.isExpenditure() ? amount * -1 : amount, no, remarks });
			stat.execute();
			transaction.setId(getLastInsertId(conn));
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		catch(AttributeException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return transaction;
	}

	public Transaction getTransaction(long id, Account account) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		ResultSet result;
		Transaction transaction = null;
		Category category;

		try
		{
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "SELECT record.\"date\", record.amount, record.no, record.remarks, category_id, category.description, category.expenditure " +
			                              "FROM record INNER JOIN category ON category_id=category.id WHERE record.deleted=0 AND record.id=?", new Object[] { id });
			result = stat.executeQuery();

			if(result.next())
			{
				transaction = pico.getComponent(Transaction.class);
				transaction.setId(id);
				transaction.setDate(new Date(result.getLong(1)));
				transaction.setAmount(result.getDouble(2));
				transaction.setNo(result.getString(3));
				transaction.setRemarks(result.getString(4));

				category = pico.getComponent(Category.class);
				category.setId(result.getLong(5));
				category.setName(result.getString(6));
				category.setExpenditure(result.getBoolean(7));
				transaction.setCategory(category);

				transaction.setAccount(account);
			}
			else
			{
				throw new ProviderException("Couldn't find transaction.");
			}

			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		catch(AttributeException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return transaction;
	}

	@Override
	public List<Transaction> getTransactions(Account account, long begin, long end) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		ResultSet result;
		LinkedList<Transaction> transactions = new LinkedList<Transaction>();

		try
		{
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, "SELECT id FROM record WHERE deleted=0 AND account_id=? AND \"date\">=? AND \"date\"<=?", new Object[] { account.getId(), begin, end  });
			result = stat.executeQuery();

			while(result.next())
			{
				transactions.add(getTransaction(result.getLong(1), account));
			}

			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return transactions;
	}

	@Override
	public void updateTransaction(Transaction transaction) throws ProviderException
	{
		Connection conn = null;

		try
		{
			conn = pool.getConnection(connectionString);
			prepareAndExecuteStatement(conn, "UPDATE record SET account_id=?, category_id=?, \"date\"=?, amount=?, remarks=?, no=? WHERE id=?",
			                                 new Object[]{ transaction.getAccount().getId(), transaction.getCategory().getId(), transaction.getDate(),
                                                           transaction.getCategory().isExpenditure() ? transaction.getRebate() * -1 : transaction.getIncome(),
                                                           transaction.getRemarks(), transaction.getNo(), transaction.getId() });

		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}
	}

	@Override
	public void deleteTransaction(long id) throws ProviderException
	{
		deleteEntity("record", id);
	}

	@Override
	public List<Long> getTimestamps(long accountId) throws ProviderException
	{
		Connection conn = null;
		Statement stat;
		ResultSet result;
		List<Long> timestamps = new LinkedList<Long>();

		try
		{
			conn = pool.getConnection(connectionString);
			stat = conn.createStatement();
			result = stat.executeQuery("SELECT \"date\" FROM record WHERE deleted=0 ORDER BY \"date\" DESC");

			while(result.next())
			{
				timestamps.add(result.getLong(1));
			}

			result.close();
			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return timestamps;
	}

	/*
	 * balances:
	 */
	@Override
	public double getBalance(long accountId, Date date) throws ProviderException
	{
		Object result;

		if((result = executeScalar("SELECT SUM(amount) FROM record WHERE deleted=0 AND account_id=? AND \"date\"<?", new Object[]{ accountId, date.getTime() })) != null)
		{
			return (Double)result;
		}

		return 0;
	}

	/*
	 *	database initialization:
	 */
	private void init() throws ClassNotFoundException, SQLException
	{
		Connection conn;
		Statement stat;

		// load JDBC driver:
		Class.forName("SQLite.JDBCDriver");
	
		// create tables:
		conn = pool.getConnection(connectionString);
		stat = conn.createStatement();
		stat.execute("CREATE TABLE IF NOT EXISTS account (id INTEGER PRIMARY KEY, name VARCHAR(32) NOT NULL, remarks VARCHAR(512), currency_id INT, deleted INTEGER)");
		stat.execute("CREATE TABLE IF NOT EXISTS currency (id INTEGER PRIMARY KEY, description VARCHAR(32) NOT NULL, deleted INTEGER)");
		stat.execute("CREATE TABLE IF NOT EXISTS category (id INTEGER PRIMARY KEY, description VARCHAR(32) NOT NULL, expenditure BIT, deleted INTEGER)");
		stat.execute("CREATE TABLE IF NOT EXISTS record (id INTEGER PRIMARY KEY, account_id INT NOT NULL, category_id INT NOT NULL, date INT, amount REAL, no VARCHAR(32), remarks VARCHAR(512), deleted INTEGER)");

		// close connection:
		stat.close();
		pool.closeConnection(conn);

		// set success state:
		initialized = true;
	}

	private void createInitialData() throws ProviderException
	{
		Translation translation = new Translation();
		Currency currency;

		if(((Integer)executeScalar("SELECT COUNT(id) FROM account", null)) == 0)
		{
			currency = createCurrency("Euro");
			createAccount(translation.translate("Cash"), "", currency);
			createCategory(translation.translate("Books"), true);
			createCategory(translation.translate("Car"), true);
			createCategory(translation.translate("Clothes"), true);
			createCategory(translation.translate("Computer"), true);
			createCategory(translation.translate("Entertainment"), true);
			createCategory(translation.translate("Gifts"), true);
			createCategory(translation.translate("Hobbies"), true);
			createCategory(translation.translate("Insurance"), true);
			createCategory(translation.translate("Miscellaneous"), true);
			createCategory(translation.translate("Phone"), true);
			createCategory(translation.translate("Salary"), false);
			createCategory(translation.translate("Gifts received"), false);
		}
	}

	/*
	 *	JDBC helpers:
	 */
	private PreparedStatement prepareStatement(Connection connection, String query, Object[] args) throws SQLException
	{
		PreparedStatement stat;

		stat = connection.prepareStatement(query);

		if(args != null)
		{
			for(int i = 0; i < args.length; ++i)
			{
				if(args[i] == null)
				{
					stat.setObject(i + 1, null);
				}
				if(args[i] instanceof Long)
				{
					stat.setLong(i + 1, (Long)args[i]);
				}
				if(args[i] instanceof Integer)
				{
					stat.setInt(i + 1, (Integer)args[i]);
				}
				else if(args[i] instanceof String)
				{
					stat.setString(i + 1, (String)args[i]);
				}
				else if(args[i] instanceof Boolean)
				{
					stat.setInt(i + 1, (Boolean)args[i] ? 1 : 0);
				}
				else if(args[i] instanceof Date)
				{
					stat.setLong(i + 1, ((Date)args[i]).getTime());
				}
				else if(args[i] instanceof Double)
				{
					stat.setDouble(i + 1, (Double)args[i]);
				}
			}
		}

		return stat;
	}

	private Object executeScalar(String query, Object[] args) throws ProviderException
	{
		Connection conn = null;
		PreparedStatement stat;
		ResultSet result;
		Object value = null;

		try
		{
			conn = pool.getConnection(connectionString);
			stat = prepareStatement(conn, query, args);
			result = stat.executeQuery();

			if(result.next())
			{
				value = result.getObject(1);
			}

			stat.close();
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}

		return value;
	}

	private void prepareAndExecuteStatement(Connection connection, String query, Object[] args) throws SQLException
	{
		PreparedStatement stat;

		stat = prepareStatement(connection, query, args);
		stat.execute();
		stat.close();
	}

	private long getLastInsertId(Connection conn) throws SQLException, ProviderException
	{
		Statement stat = null;
		ResultSet result = null;

		try
		{
			stat = conn.createStatement();
			result = stat.executeQuery("SELECT LAST_INSERT_ROWID()");
	
			if(result.next())
			{
				return result.getLong(1);
			}
		}
		catch(SQLException e)
		{
			throw e;
		}
		finally
		{
			if(stat != null)
			{
				stat.close();
			}
			
			if(result != null)
			{
				result.close();
			}
		}

		throw new ProviderException("Couldn't get last insert id.");
	}

	/*
	 * helpers:
	 */
	private void deleteEntity(String table, long id) throws ProviderException
	{
		Connection conn = null;

		try
		{
			conn = pool.getConnection(connectionString);;
			prepareAndExecuteStatement(conn, "UPDATE \"" + table + "\" SET deleted=1 WHERE id=?", new Object[]{ id });
		}
		catch(SQLException e)
		{
			throw new ProviderException(e);
		}
		finally
		{
			pool.closeConnection(conn);
		}
	}
}