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
package accounting.data.sqlite;

import java.sql.*;
import java.util.*;

public class ConnectionPool
{
	private HashMap<String, Queue<Connection>> connections;
	private static ConnectionPool instance = null;
	public static final long MAX_QUEUE_SIZE = 5;

	private ConnectionPool()
	{
		connections = new HashMap<String, Queue<Connection>>();
	}

	public static synchronized ConnectionPool getInstance()
	{
		if(instance == null)
		{
			instance = new ConnectionPool();
		}

		return instance;
	}

	public synchronized Connection getConnection(String connectionString) throws SQLException
	{
		Queue<Connection> queue;
		Connection connection = null;

		queue = getQueue(connectionString);

		while(!queue.isEmpty() && connection == null)
		{
			if(!validate((connection = queue.poll())))
			{
				connection = null;
			}
		}

		if(connection == null)
		{
			connection = DriverManager.getConnection(connectionString);
		}

		return connection;
	}

	public synchronized void closeConnection(Connection connection) 
	{
		Queue<Connection> queue;

		try
		{
			queue = getQueue(connection);
	
			if(validate(connection))
			{
				if(queue.size() < MAX_QUEUE_SIZE)
				{
					queue.offer(connection);
				}
				else
				{
					try
					{
						connection.close();
					}
					catch(SQLException e) { }
				}
			}
		}
		catch(SQLException e) { }
	}

	public synchronized void clear()
	{
		for(Queue<Connection> queue: connections.values())
		{
			for(Connection conn : queue)
			{
				try
				{
					conn.close();
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}

			queue.clear();
		}
	}

	private boolean validate(Connection connection)
	{
		if(connection == null)
		{
			return false;
		}

		try
		{
			connection.getMetaData();

			if(connection.isClosed())
			{
				return false;
			}
		}
		catch(SQLException e)
		{
			return false;
		}

		return true;
	}

	private Queue<Connection> getQueue(String connectionString)
	{
		if(!connections.containsKey(connectionString))
		{
			connections.put(connectionString, new LinkedList<Connection>());
		}

		return connections.get(connectionString);
	}

	private Queue<Connection> getQueue(Connection connection) throws SQLException
	{
		return getQueue(connection.getMetaData().getURL());
	}
}