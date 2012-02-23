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

public class ProviderException extends Exception
{
	private static final long serialVersionUID = 2366062026230563281L;

	public ProviderException() { }

	public ProviderException(String message)
	{
		super(message);
	}

	public ProviderException(Throwable throwable)
	{
		super(throwable);
	}

	public ProviderException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}