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
package accounting.application;

public class IllegalAttributeAccessException extends AttributeException
{
	private static final long serialVersionUID = 4970944980813462286L;

	public IllegalAttributeAccessException() { }

	public IllegalAttributeAccessException(Throwable throwable)
	{
		super(throwable);
	}
}