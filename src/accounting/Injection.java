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

import java.util.Properties;
import org.picocontainer.*;
import org.picocontainer.behaviors.OptInCaching;
import org.picocontainer.parameters.ConstantParameter;

import accounting.application.*;

public final class Injection
{
	public static MutablePicoContainer getContainer() throws ClassNotFoundException
	{
		Properties props;
		Class<?> providerClass;
		MutablePicoContainer pico = new DefaultPicoContainer(new OptInCaching());

		// get properties:
		props = Configuration.getProperties();

		// load specified data provider class:
		providerClass = Class.forName(props.getProperty(Configuration.DB_PROVIDER));

		// add provider component to container:
		Parameter[] providerParams =  { new ConstantParameter(props.getProperty(Configuration.DB_CONNECTIONSTRING)) };
		pico.addComponent(providerClass, providerClass, providerParams);

		// add entities to container:
		pico.addComponent(Factory.class);
		pico.addComponent(Currency.class);
		pico.addComponent(Category.class);
		pico.addComponent(Account.class);
		pico.addComponent(Transaction.class);
		pico.addComponent(Template.class);
		pico.as(Characteristics.CACHE).addComponent(ExchangeUtil.class);

		return pico;
	}	
}