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
package accounting.application;

public class ReferenceException extends Exception
{
	private static final long serialVersionUID = -2918736262512082975L;

	public ReferenceException() { }

	public ReferenceException(String message)
	{
		super(message);
	}

	public ReferenceException(Throwable cause)
	{
		super(cause);
	}

	public ReferenceException(String message, Throwable cause)
	{
		super(message, cause);
	}
}