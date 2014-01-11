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

public class AttributeValidatorException extends AttributeException
{
	private static final long serialVersionUID = 4121422966267955371L;

	public AttributeValidatorException() { }

	public AttributeValidatorException(String message)
	{
		super(message);
	}

	public AttributeValidatorException(Throwable throwable)
	{
		super(throwable);
	}

	public AttributeValidatorException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}