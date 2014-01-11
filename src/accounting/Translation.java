/***************************************************************************
    begin........: February 2012
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

import java.util.*;
import java.io.*;

public final class Translation
{
	private ResourceBundle resource;

	public Translation()
	{
		try
		{
			resource = getResource();
		}
		catch(TranslationException e)
		{
			e.printStackTrace();
		}
	}

	public String translate(String text)
	{
		if(resource != null)
		{
			if(resource.containsKey(text))
			{
				try
				{
					return new String(resource.getString(text).getBytes(), "UTF-8");
				}
				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}
			}
		}

		return text;
	}

	private ResourceBundle getResource() throws TranslationException
	{
		Properties props;

		if((props = Configuration.getProperties()) != null)
		{
			if(props.containsKey(Configuration.LANGUAGE))
			{	
				return getResource(props.getProperty(Configuration.LANGUAGE));
			}
		}

		return null;
	}

	private ResourceBundle getResource(String language) throws TranslationException
	{
		FileInputStream in;

		try
		{
			in = new FileInputStream(buildFilename(language));
			return new PropertyResourceBundle(in);
		}
		catch(FileNotFoundException e)
		{
			throw new TranslationException("Couldn't find translation file.", e);
		}
		catch(IOException e)
		{
			new TranslationException("Couldn't open translation file.", e);
		}

		return null;
	}

	private String buildFilename(String language)
	{
		return System.getProperty("user.dir") + File.separatorChar + "locale" + File.separatorChar + language + ".properties";
	}
}