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
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.codesunday.ceres.core.logging.LogCapsule;

public class ApplicationContext {

	public static final String CONTEXT = "context";
	public static final String TYPE = "type";
	public static final String CONTEXT_DEFAULT = "default";

	public static final String KEY = "key";

	private static final String VALUE = "value";

	private Map<String, Map<String, ObjectNode>> map = new HashMap();
	private static Map<String, Map<String, ObjectNode>> globalMap = new HashMap();

	public QueryContainer queryContainer;
	public TemplateContainer templateContainer;
	public LogCapsule logCapsule;

	public int numberOfConstantsToShow = 2;

	/**
	 * Private Constructor
	 * 
	 * @param file
	 */
	private ApplicationContext(List<ObjectNode> list) {

		this();

		append(list);
	}

	private ApplicationContext() {

		this.queryContainer = new QueryContainer();
		this.templateContainer = new TemplateContainer();

	}

	/**
	 * Static instance provider
	 * 
	 * @param directoryName
	 * @return
	 */
	public static ApplicationContext getInstance(List<ObjectNode> list) {

		ApplicationContext appContext = new ApplicationContext(list);

		return appContext;
	}

	/**
	 * Static instance provider
	 * 
	 * @param directoryName
	 * @return
	 */
	public static ApplicationContext getInstance() {

		ApplicationContext appContext = new ApplicationContext();

		return appContext;
	}

	/**
	 * Add a global property, accessible across the instance of CeresClient
	 * 
	 * @param json
	 */
	public void append(ObjectNode json) {

		if (json != null) {

			String context = CONTEXT_DEFAULT;
			String key = null;

			if (json.has(CONTEXT)) {
				context = json.get(CONTEXT).getTextValue();
			} else if (json.has(TYPE)) {
				context = json.get(TYPE).getTextValue();
			}

			if (json.has(KEY)) {
				key = json.get(KEY).getTextValue();
			} else {
				// ERROR
			}

			if (map.containsKey(context)) {
				Map<String, ObjectNode> queryMap = map.get(context);

				queryMap.put(context, json);

			} else {
				Map<String, ObjectNode> queryMap = new HashMap();

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
	public void append(List<ObjectNode> list) {

		for (ObjectNode json : list) {
			append(json);
		}

	}

	/**
	 * Add a list of global properties, accessible across the instance of
	 * CeresClient
	 * 
	 * @param json
	 */
	public void append(ArrayNode array) {

		for (JsonNode node : array) {
			append((ObjectNode) node);
		}

	}

	/**
	 * Add a global property, accessible across the instance of CeresClient
	 * 
	 * @param json
	 */
	public static void appendGlobalScope(ObjectNode json) {

		if (json != null) {

			String context = CONTEXT_DEFAULT;
			String key = null;

			if (json.has(CONTEXT)) {
				context = json.get(CONTEXT).getTextValue();
			} else if (json.has(TYPE)) {
				context = json.get(TYPE).getTextValue();
			}

			if (json.has(KEY)) {
				key = json.get(KEY).getTextValue();
			} else {
				// ERROR
			}

			if (globalMap.containsKey(context)) {
				Map<String, ObjectNode> queryMap = globalMap.get(context);

				queryMap.put(key, json);

			} else {
				Map<String, ObjectNode> queryMap = new HashMap();

				queryMap.put(key, json);

				globalMap.put(context, queryMap);
			}
		}

	}

	/**
	 * Add a list of global properties, accessible across the instance of
	 * CeresClient
	 * 
	 * @param json
	 */
	public static void appendGlobalScope(ArrayNode array) {

		for (JsonNode node : array) {
			appendGlobalScope((ObjectNode) node);
		}

	}

	/**
	 * Get whole value object for a given property context/type and property key
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public ObjectNode getValue(String context, String key) {

		ObjectNode json = null;

		if (map.containsKey(context) && map.get(context).containsKey(key)) {
			json = (ObjectNode) map.get(context).get(key).get(VALUE);
		} else if (globalMap.containsKey(context) && globalMap.get(context).containsKey(key)) {
			json = (ObjectNode) globalMap.get(context).get(key).get(VALUE);
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
			ObjectNode json = (ObjectNode) map.get(context).get(key).get(VALUE);

			if (json.has(value)) {
				returnValue = json.get(value).getTextValue();
			}

		}

		if (returnValue == null && globalMap.containsKey(context) && globalMap.get(context).containsKey(key)) {
			ObjectNode json = (ObjectNode) globalMap.get(context).get(key).get(VALUE);

			if (json.has(value)) {
				returnValue = json.get(value).getTextValue();
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

		int returnValue = -1;

		if (map.containsKey(context) && map.get(context).containsKey(key)) {
			ObjectNode json = (ObjectNode) map.get(context).get(key).get(VALUE);

			if (json.has(value)) {
				returnValue = json.get(value).getIntValue();
			}

		}

		if (returnValue < 0 && globalMap.containsKey(context) && globalMap.get(context).containsKey(key)) {
			ObjectNode json = (ObjectNode) globalMap.get(context).get(key).get(VALUE);

			if (json.has(value)) {
				returnValue = json.get(value).getIntValue();
			} else {
				returnValue = 0;
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
			ObjectNode json = (ObjectNode) map.get(context).get(key).get(VALUE);

			if (json.has(value)) {
				returnValue = json.get(value).getBooleanValue();
			}

		}

		return returnValue;
	}

	public static Map<String, Map<String, ObjectNode>> getGlobalMap() {
		return globalMap;
	}

	public static void clearGlobalMap() {
		globalMap.clear();
	}

	public Map<String, Map<String, ObjectNode>> getMap() {
		return map;
	}

	public void clearMap() {
		map.clear();
	}

	public Map<String, Map<String, QueryTemplate>> getQueries() {
		return this.queryContainer.getAllQueries();
	}

	@Override
	public String toString() {
		return map.toString();
	}

}
