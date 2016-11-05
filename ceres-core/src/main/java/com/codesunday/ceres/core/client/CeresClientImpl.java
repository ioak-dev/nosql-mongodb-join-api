/*
 * Copyright (C) 2016  Arun Kumar Selvaraj

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.codesunday.ceres.core.client;

import java.util.Map;

import org.json.JSONObject;

import com.codesunday.ceres.core.domain.ApplicationContext;
import com.codesunday.ceres.core.domain.QueryTemplate;
import com.codesunday.ceres.core.domain.Result;
import com.codesunday.ceres.core.domain.TransactionContext;
import com.codesunday.ceres.core.processor.QueryProcessor;

public class CeresClientImpl {

	private JSONObject driverProperty;
	private JSONObject databaseInstanceProperty;

	private ApplicationContext applicationContext;

	/**
	 * Constructor
	 * 
	 * @param applicationContext
	 */

	public CeresClientImpl(ApplicationContext applicationContext, JSONObject driverProperty,
			JSONObject databaseInstanceProperty) {
		super();
		this.driverProperty = driverProperty;
		this.databaseInstanceProperty = databaseInstanceProperty;

		this.applicationContext = applicationContext;
	}

	/**
	 * Select / Find the documents for the given query and conditions
	 * 
	 * @param query
	 * @param parameters
	 * @return
	 */
	public Result find(QueryTemplate queryTemplate, Map<String, Object> parameters) {

		TransactionContext transactionContext = TransactionContext.getInstance(driverProperty, databaseInstanceProperty,
				applicationContext);

		QueryProcessor processor = new QueryProcessor(applicationContext, transactionContext, parameters);

		return processor.find(queryTemplate, parameters);

	}

}
