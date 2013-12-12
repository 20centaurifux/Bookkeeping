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
package accounting.application;

import org.picocontainer.annotations.Inject;

import accounting.data.*;

public class ExchangeUtil
{
	@Inject protected IProvider provider;

	public double getExchangeRate(Currency from, Currency to) throws ExchangeRateUtilException
	{
		try
		{
			if(!provider.exchangeRateExists(from, to))
			{
				throw new ExchangeRateNotFoundException("Couldn't find exchange rate.");
			}
			
			return provider.getExchangeRate(from, to);
		}
		catch(ProviderException e)
		{
			throw new ExchangeRateUtilException("Couldn't get exchange rate.", e);
		}
	}
	
	public void updateExchangeRate(Currency from, Currency to, double rate) throws ExchangeRateUtilException
	{
		try
		{
			provider.updateExchangeRate(from, to, rate);
		}
		catch(ProviderException e)
		{
			throw new ExchangeRateUtilException("Couldn't update exchange rate.", e);
		}
	}
	
	public double exchange(Currency from, Currency to, double amount) throws ExchangeRateUtilException
	{
		if(from.equals(to))
		{
			return amount;
		}
		
		return getExchangeRate(from, to) * amount;
	}
}