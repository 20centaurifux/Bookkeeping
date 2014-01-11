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

import java.lang.reflect.Field;
import java.util.*;
import org.picocontainer.annotations.Inject;
import accounting.application.annotation.*;
import accounting.data.*;

public abstract class AEntity<T>
{
	private T id;
	private List<IEntityListener> entityListener;
	private HashMap<String, FieldDefinition> attributes;

	private class FieldDefinition
	{
		private Field field;
		private Attribute attribute;
		private StringValidator stringValidator;
		private ObjectValidator objectValidator;
		private DoubleValidator doubleValidator;
	};

	@Inject protected IProvider provider;

	protected abstract void update() throws ProviderException;
	protected abstract void remove() throws ProviderException, ReferenceException;

	protected AEntity()
	{
		Attribute attribute;
		FieldDefinition definition;

		entityListener = new LinkedList<IEntityListener>();
		attributes = new HashMap<String, AEntity<T>.FieldDefinition>();

		// search for private members declared as "Attribute"
		for(Field field : this.getClass().getDeclaredFields())
		{
			if(field.isAnnotationPresent(Attribute.class))
			{
				attribute = field.getAnnotation(Attribute.class);
				definition = new FieldDefinition();
				definition.field = field;
				definition.attribute = attribute;

				// found attribute => search for validators
				if(field.isAnnotationPresent(StringValidator.class))
				{
					definition.stringValidator = field.getAnnotation(StringValidator.class);
				}

				if(field.isAnnotationPresent(ObjectValidator.class))
				{
					definition.objectValidator = field.getAnnotation(ObjectValidator.class);
				}

				if(field.isAnnotationPresent(DoubleValidator.class))
				{
					definition.doubleValidator = field.getAnnotation(DoubleValidator.class);
				}

				// save found attribute & related validators in hashtable:
				this.attributes.put(attribute.name(), definition);
			}
		}
	}

	public T getId()
	{
		return this.id;
	}

	public void setId(T id)
	{
		this.id = id;
	}

	public void save() throws ProviderException
	{
		update();

		for(IEntityListener l : entityListener)
		{
			l.entityUpdated(new EntityEvent((AEntity<?>)this));
		}		
	}

	public void delete() throws ProviderException, ReferenceException
	{
		remove();

		for(IEntityListener l : entityListener)
		{
			l.entityDeleted(new EntityEvent((AEntity<?>)this));
		}
	}
	
	public void addEntityListener(IEntityListener actionListener)
	{
		this.entityListener.add(actionListener);
	}

	public void removeEntityListener(IEntityListener actionListener)
	{
		this.entityListener.remove(actionListener);
	}	

	public Object getAttribute(String name) throws AttributeNotFoundException, IllegalAttributeAccessException
	{
		FieldDefinition definition;

		if(!attributes.containsKey(name))
		{
			throw new AttributeNotFoundException();
		}

		definition = attributes.get(name);
		definition.field.setAccessible(true);

		if(definition.attribute.readable())
		{
			try
			{
				return definition.field.get(this);
			}
			catch(Exception e)
			{
				throw new IllegalAttributeAccessException(e);
			}
		}
		else
		{
			throw new IllegalAttributeAccessException();
		}
	}

	public void setAttribute(String name, Object value) throws AttributeNotFoundException, IllegalAttributeAccessException, AttributeValidatorException
	{
		FieldDefinition definition;

		if(!attributes.containsKey(name))
		{
			throw new AttributeNotFoundException();
		}

		definition = attributes.get(name);
		definition.field.setAccessible(true);

		if(definition.attribute.writeable())
		{
			if(definition.stringValidator != null)
			{
				validateString(definition.stringValidator, (String)value);
			}

			if(definition.objectValidator != null)
			{
				validateObject(definition.objectValidator, value);
			}

			if(definition.doubleValidator != null)
			{
				validateDouble(definition.doubleValidator, (Double)value);
			}

			try
			{
				definition.field.set(this, value);
			}
			catch(Exception e)
			{
				throw new IllegalAttributeAccessException(e);
			}
		}
		else
		{
			throw new IllegalAttributeAccessException();
		}
	}

	protected String getString(String attribute)
	{
		try
		{
			return (String)getAttribute(attribute);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	protected boolean getBool(String attribute)
	{
		try
		{
			return (Boolean)getAttribute(attribute);
		}
		catch(Exception e)
		{
			return false;
		}
	}

	protected Integer getInt(String attribute)
	{
		try
		{
			return (Integer)getAttribute(attribute);
		}
		catch(Exception e)
		{
			return 0;
		}
	}
	
	protected Object getObject(String attribute)
	{
		try
		{
			return getAttribute(attribute);
		}
		catch(Exception e)
		{
			return null;
		}
	}	

	private void validateString(StringValidator validator, String value) throws AttributeValidatorException
	{
		if(value == null)
		{
			if(!validator.allowNull())
			{
				throw new AttributeNullException();
			}
		}
		else
		{
			if(value.length() > validator.maxLength())
			{
				throw new AttributeValidatorException("String length exceeds maximum.");
			}
			else if(value.length() < validator.minLength())
			{
				throw new AttributeValidatorException("String length exceeds minimum.");
			}
		}
	}

	private void validateObject(ObjectValidator validator, Object value) throws AttributeValidatorException
	{
		if(value == null && !validator.allowNull())
		{
				throw new AttributeNullException();
		}
	}

	private void validateDouble(DoubleValidator validator, Double value) throws AttributeValidatorException
	{
		if(value == null)
		{
			if(!validator.allowNull())
			{
				throw new AttributeNullException();
			}
		}
		else
		{
			if(value > validator.max())
			{
				throw new AttributeValidatorException("Float length exceeds maximum.");
			}
			else if(value < validator.min())
			{
				throw new AttributeValidatorException("Float length exceeds minimum.");
			}
		}
	}
}