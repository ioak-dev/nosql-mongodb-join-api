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

package com.codesunday.ceres.core.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.codesunday.ceres.core.driver.DatabaseDriver;
import com.codesunday.ceres.core.logging.LogCapsule;

public class TransactionContext {

	public DatabaseDriver driver;

	private Map<String, Table> tables;

	public LogCapsule logCapsule;

	public String context;
	public String id;
	public UUID uuid;

	/**
	 * Private Constructor
	 * 
	 * @param driver
	 */
	private TransactionContext(DatabaseDriver driver, ApplicationContext applicationContext) {
		super();
		this.driver = driver;
		this.tables = new HashMap();

		this.logCapsule = applicationContext.logCapsule;
	}

	public static TransactionContext getInstance(JSONObject driverProperty, JSONObject databaseInstanceProperty,
			ApplicationContext applicationContext) {

		DatabaseDriver driver = DatabaseDriver.getInstance(driverProperty, databaseInstanceProperty,
				applicationContext);

		TransactionContext context = new TransactionContext(driver, applicationContext);

		return context;

	}

	public void addTable(String alias, Table table) {
		tables.put(alias, table);
	}

	public Table getTable(String alias) {
		if (tables.containsKey(alias)) {
			return tables.get(alias);
		} else {
			return null;
		}
	}

}
