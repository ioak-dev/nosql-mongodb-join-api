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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.codesunday.ceres.core.domain.ApplicationContext;
import com.codesunday.ceres.core.domain.QueryContainer;
import com.codesunday.ceres.core.domain.QueryTemplate;
import com.codesunday.ceres.core.domain.Result;
import com.codesunday.ceres.core.domain.TemplateContainer;
import com.codesunday.ceres.core.logging.LogCapsule;
import com.codesunday.ceres.core.utils.FileUtils;

public class CeresClient {

	private ApplicationContext applicationContext;

	private CeresClientImpl clientImpl;

	private CeresClient(ApplicationContext applicationContext, String databaseDriver, String databaseName) {

		super();

		this.applicationContext = applicationContext;
		this.applicationContext.queryContainer = new QueryContainer();
		this.applicationContext.templateContainer = new TemplateContainer();

		JSONObject driverProperty = applicationContext.getValue("database-driver", databaseDriver);
		JSONObject databaseInstanceProperty = applicationContext.getValue("database-instance", databaseName);

		// Initialize logging settings

		int numberOfConstantsToShow = applicationContext.getNumber("logging", "log-properties",
				"number-of-constants-to-show");
		String format = applicationContext.getValue("logging", "log-properties", "format");

		applicationContext.numberOfConstantsToShow = numberOfConstantsToShow;

		boolean isJsonFormat = false;

		if (format.equalsIgnoreCase("json")) {
			isJsonFormat = true;
		}

		LogCapsule logCapsule = new LogCapsule(isJsonFormat);

		this.applicationContext.logCapsule = logCapsule;

		clientImpl = new CeresClientImpl(applicationContext, driverProperty, databaseInstanceProperty);
	}

	public static CeresClient getInstance(String appContextPath, String dbDriver, String dbName) {

		ApplicationContext applicationContext = ApplicationContext.getInstance(appContextPath);

		CeresClient client = new CeresClient(applicationContext, dbDriver, dbName);

		return client;

	}

	public void addQueriesFromFilesystem(String queryPath) {

		List<JSONObject> queries = readQueriesFromFileSystem(queryPath);

		applicationContext.queryContainer.append(queries);

	}

	public void addQueriesFromDatabase(JSONObject queryJson) {

		Result result = find(queryJson, null);

		applicationContext.queryContainer.append(result.getView());

	}

	public void addQueriesFromDatabase(JSONObject queryJson, Map<String, Object> parameters) {

		Result result = find(queryJson, parameters);

		applicationContext.queryContainer.append(result.getView());

	}

	public void addTemplatesFromFilesystem(String queryPath) {

		List<JSONObject> templates = readQueriesFromFileSystem(queryPath);

		applicationContext.templateContainer.append(templates);

	}

	public void addTemplatesFromDatabase(JSONObject queryJson) {

		Result result = find(queryJson, null);

		applicationContext.templateContainer.append(result.getView());

	}

	public void addTemplatesFromDatabase(JSONObject queryJson, Map<String, Object> parameters) {

		Result result = find(queryJson, parameters);

		applicationContext.templateContainer.append(result.getView());

	}

	private List<JSONObject> readQueriesFromFileSystem(String queryPath) {

		List<File> files = FileUtils.listf(queryPath);
		List<JSONObject> list = FileUtils.read(files);

		return list;
	}

	public Result find(String context, String queryid, Map<String, Object> parameters) {

		QueryTemplate queryTemplate = applicationContext.queryContainer.getQueryTemplate(context, queryid);
		return clientImpl.find(queryTemplate, parameters);

	}

	public Result find(JSONObject queryJson, Map<String, Object> parameters) {

		QueryTemplate queryTemplate = new QueryTemplate(queryJson);
		return clientImpl.find(queryTemplate, parameters);

	}
}
