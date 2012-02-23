/***************************************************************************
    begin........: February 2012
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
package accounting.application;

import accounting.AEvent;

public class EntityEvent extends AEvent
{
	private AEntity<?> source;
	
	public EntityEvent(AEntity<?> source)
	{
		setSource(source);
	}

	public AEntity<?> getSource()
	{
		return source;
	}

	public void setSource(AEntity<?> source)
	{
		this.source = source;
	}
}