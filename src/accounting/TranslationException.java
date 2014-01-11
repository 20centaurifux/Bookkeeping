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
package accounting;

public class TranslationException extends Exception
{
	private static final long serialVersionUID = 4553555497414222961L;

	public TranslationException() { }

	public TranslationException(String message)
	{
		super(message);
	}

	public TranslationException(Throwable throwable)
	{
		super(throwable);
	}

	public TranslationException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}