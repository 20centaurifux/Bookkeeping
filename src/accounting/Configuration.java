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
package accounting;

import java.io.*;
import java.util.Properties;

public final class Configuration
{
	public static final String DB_CONNECTIONSTRING = "database.connectionString";
	public static final String DB_PROVIDER = "database.provider";
	public static final String LANGUAGE = "language";

	private static final String DEFAULT_DB_PROVIDER = "accounting.data.sqlite.SQLiteProvider";

	public static Properties getProperties()
	{
			Properties props = new Properties();

			// set default values:
			props.setProperty(DB_CONNECTIONSTRING, getDefaultConnectionString());
			props.setProperty(DB_PROVIDER, DEFAULT_DB_PROVIDER);
			props.setProperty(LANGUAGE, System.getProperty("user.language"));

			// try to load properties from ini file:
			try
			{
				FileInputStream in = new FileInputStream(Configuration.getConfigFilename());
				props.load(in);
			}
			catch(FileNotFoundException e) { }
			catch(IOException e)
			{
				e.printStackTrace();
			}

			return props;
    }
    
	public static void storeProperties(Properties properties) throws IOException
	{
		FileOutputStream out = new FileOutputStream(Configuration.getConfigFilename());
		properties.store(out, null);
	}

	public static void prepareHomeDir() throws IOException
	{
		String path;
		File file;

		path = getHomeDir();
		file = new File(path);
		
		if(!file.exists())
		{
			if(!file.mkdir())
			{
				throw new IOException("Couldn't access path: " + path);
			}
		}
	}

	public static String getHomeDir()
	{
		return System.getProperty("user.home") + File.separatorChar + ".bookkeeping";
	}

	private static String getConfigFilename()
	{
		return getHomeDir() + File.separatorChar + "accounting.ini";
	}

	private static String getDefaultConnectionString()
	{
		return ("jdbc:sqlite:/" + getHomeDir() + File.separatorChar + "bookkeeping.db");
	}
}