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
package accounting.gui;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import accounting.application.*;

public class GenericListModel<T extends Comparable<? super T>> extends AbstractListModel implements IEntityListener
{
	protected List<T> items = new LinkedList<T>();
    protected List<ListDataListener> listener = new LinkedList<ListDataListener>();

	private static final long serialVersionUID = 1770095430426958733L;

	public void add(T item)
    {
    	items.add(item);

    	if(item instanceof AEntity<?>)
    	{
    		((AEntity<?>)item).addEntityListener(this);
    	}

    	sendListDataEvent();
    }

    public void add(List<T> items)
    {
    	for(T item : items)
    	{
    		add(item);
    	}
    }

    public void clear()
    {
    	items.clear();
    	sendListDataEvent();
    }

    public void remove(T item)
    {
    	items.remove(item);
    	sendListDataEvent();
    }

    public void sort()
    {
    	Collections.sort(items);
    	sendListDataEvent();
	}

    public int indexOf(T item)
    {
    	return items.indexOf(item);
    }

	@Override
	public Object getElementAt(int index)
	{
		if(index < 0 || index >= items.size())
		{
			return null;
		}
		
		return items.get(index);
	}

	@Override
	public int getSize()
	{
		return items.size();
	}

	@Override
	public void addListDataListener(ListDataListener listener)
	{
		this.listener.add(listener);
	}

	@Override
	public void removeListDataListener(ListDataListener listener)
	{
		this.listener.remove(listener);
	}

	private void sendListDataEvent()
	{
		for(ListDataListener listener : this.listener)
		{
			listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
		}
	}

	/*
	 * entity listener:
	 */
	@Override
	public void entityUpdated(EntityEvent event)
	{
		sendListDataEvent();
	}

	@Override
	public void entityDeleted(EntityEvent event)
	{
		items.remove(event.getSource());
		sendListDataEvent();
	}
}