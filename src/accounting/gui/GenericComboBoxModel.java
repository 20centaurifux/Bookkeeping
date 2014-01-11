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
package accounting.gui;

import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("rawtypes")
public class GenericComboBoxModel<T extends Comparable<? super T>> extends GenericListModel<T> implements ComboBoxModel
{
	private static final long serialVersionUID = 3830985086287173229L;
	private T selectedItem;

	@Override
	public Object getSelectedItem()
	{
		return selectedItem;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSelectedItem(Object item)
	{
		for(int i = 0; i < this.getSize(); ++i)
		{
			if(getElementAt(i).equals(item))
			{
				selectedItem = (T)getElementAt(i);
				break;
			}
		}

		for(ListDataListener listener : this.listener)
		{
			listener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
		}
	}

	public void selectFirst()
	{
		if(getSize() > 0)
		{
			setSelectedItem(getElementAt(0));
		}
	}
	
	public void selectLast()
	{
		if(getSize() > 0)
		{
			setSelectedItem(getElementAt(getSize() - 1));
		}
	}
}