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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.codesunday.ceres.core.logging.LogCapsule;
import com.codesunday.ceres.core.utils.FileUtils;

public class ApplicationContext {

	public static final String CONTEXT = "context";
	public static final String TYPE = "type";
	public static final String CONTEXT_DEFAULT = "default";

	public static final String KEY = "key";

	private static final String VALUE = "value";

	private Map<String, Map<String, JSONObject>> map = new HashMap();

	public QueryContainer queryContainer;
	public TemplateContainer templateContainer;
	public LogCapsule logCapsule;
	
	public int numberOfConstantsToShow = 2;

	/**
	 * Private Constructor
	 * 
	 * @param file
	 */
	private ApplicationContext(File file) {

		this.queryContainer = new QueryContainer();
		this.templateContainer = new TemplateContainer();

		List<JSONObject> list = FileUtils.read(file);

		append(list);
	}

	/**
	 * Static instance provider
	 * 
	 * @param directoryName
	 * @return
	 */
	public static ApplicationContext getInstance(String directoryName) {
		File file = new File(directoryName);

		ApplicationContext appContext = new ApplicationContext(file);

		return appContext;
	}

	/**
	 * Add a global property, accessible across the instance of CeresClient
	 * 
	 * @param json
	 */
	private void append(JSONObject json) {

		if (json != null) {

			String context = CONTEXT_DEFAULT;
			String key = null;

			if (json.has(CONTEXT)) {
				context = json.optString(CONTEXT);
			} else if (json.has(TYPE)) {
				context = json.optString(TYPE);
			}

			if (json.has(KEY)) {
				key = json.optString(KEY);
			} else {
				// ERROR
			}

			if (map.containsKey(context)) {
				Map<String, JSONObject> queryMap = map.get(context);

				queryMap.put(context, json);

			} else {
				Map<String, JSONObject> queryMap = new HashMap();

				queryMap.put(key, json);

				map.put(context, queryMap);
			}
		}

	}

	/**
	 * Add a list of global properties, accessible across the instance of
	 * CeresClient
	 * 
	 * @param json
	 */
	private void append(List<JSONObject> list) {

		for (JSONObject json : list) {
			append(json);
		}

	}

	/**
	 * Get whole value object for a given property context/type and property key
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public JSONObject getValue(String context, String key) {

		JSONObject json = null;

		if (map.containsKey(context) && map.get(context).containsKey(key)) {
			json = map.get(context).get(key).optJSONObject(VALUE);
		}

		return json;
	}

	/**
	 * Get a specific value from the value object for a given property
	 * context/type, property key and value key
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public String getValue(String context, String key, String value) {

		String returnValue = null;

		if (map.containsKey(context) && map.get(context).containsKey(key)) {
			JSONObject json = map.get(context).get(key).optJSONObject(VALUE);

			if (json.has(value)) {
				returnValue = json.optString(value);
			}

		}

		return returnValue;
	}

	/**
	 * Get a specific value from the value object for a given property
	 * context/type, property key and value key
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public int getNumber(String context, String key, String value) {

		int returnValue = 0;

		if (map.containsKey(context) && map.get(context).containsKey(key)) {
			JSONObject json = map.get(context).get(key).optJSONObject(VALUE);

			if (json.has(value)) {
				returnValue = json.optInt(value, 0);
			}

		}

		return returnValue;
	}

	/**
	 * Get a specific value from the value object for a given property
	 * context/type, property key and value key
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String context, String key, String value) {

		boolean returnValue = false;

		if (map.containsKey(context) && map.get(context).containsKey(key)) {
			JSONObject json = map.get(context).get(key).optJSONObject(VALUE);

			if (json.has(value)) {
				returnValue = json.optBoolean(value, false);
			}

		}

		return returnValue;
	}

	@Override
	public String toString() {
		return map.toString();
	}

}
