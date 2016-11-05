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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.utils.TextUtils;

public class QueryInstance {

	private ApplicationContext applicationContext;

	private QueryTemplate queryTemplate;
	private Map<String, Object> parameters;

	public String context;
	public String id;
	public UUID uuid;

	public Map<String, String> aliasToTable = new HashMap();
	public Map<String, String> tableToAlias = new HashMap();

	public Map<String, List<String>> aliasToDbfilter = new HashMap();

	public Map<String, List<String>> aliasToInMemoryfilter = new HashMap();

	public List<JSONObject> inMemoryComplexFilterPre = new ArrayList();
	public List<JSONObject> inMemoryComplexFilterPost = new ArrayList();

	public boolean dropAlias = false;
	public boolean projectPresent = false;
	public boolean jsonBasedProjection = false;

	public List<JSONObject> projectionList = new ArrayList();
	public Map<Integer, String> projectionMapKey = new HashMap();

	// aliaslist is a temporary variable and will not have any data. it remains
	// transient in the pre-processing stages
	private List<String> aliaslist = new ArrayList();

	public List<String> joinOrder = new ArrayList();
	public Map<String, List<String>> joinMap = new HashMap();

	public QueryInstance(ApplicationContext applicationContext, QueryTemplate queryTemplate,
			Map<String, Object> parameters) {

		this.applicationContext = applicationContext;

		this.queryTemplate = queryTemplate;
		this.parameters = parameters;

		context = (String) queryTemplate.get(QueryElements.ELEMENT_CONTEXT);
		id = (String) queryTemplate.get(QueryElements.ELEMENT_ID);

		uuid = UUID.randomUUID();

		preprocess();
	}

	private void preprocess() {
		prepareTable();
		prepareFilterInDb();
		prepareFilterInMemory();
		prepareJoinOrder();
		prepareProject();
		prepareDropAlias();
	}

	private void prepareDropAlias() {

		dropAlias = (Boolean) queryTemplate.get(QueryElements.ELEMENT_DROP_ALIAS);

	}

	private void prepareProject() {

		List<Object> list = (List<Object>) queryTemplate.get(QueryElements.ELEMENT_PROJECT);

		if (list != null && list.size() > 0) {

			Map<String, JSONObject> projectionMap = new HashMap();

			jsonBasedProjection = queryTemplate.jsonBasedProjection;
			projectPresent = queryTemplate.projectPresent;

			if (projectPresent) {
				if (jsonBasedProjection) {
					for (Object obj : list) {
						if (obj instanceof String) {

							String templateName = (String) obj;

							projectionMap.put(templateName,
									applicationContext.templateContainer.getProjectTemplate(templateName));

						} else if (obj instanceof JSONObject) {

							JSONObject json = (JSONObject) obj;

							projectionMap.put(json.optString(QueryElements._TEMPLATE), json);
						}
					}
				} else {

					JSONObject projectObject = new JSONObject();

					for (Object obj : list) {
						if (obj instanceof String) {
							String fieldPair[] = ((String) obj).split(QueryElements.AS_SPACE);

							if (fieldPair.length == 2) {
								projectObject.put(fieldPair[1], fieldPair[0]);
							} else {
								projectObject.put(fieldPair[0], fieldPair[0]);
							}
						}
					}

					projectionMap.put(QueryElements._DEFAULT, projectObject);
				}
			}

			int index = 0;

			for (String key : projectionMap.keySet()) {
				projectionMapKey.put(index, key);
				projectionList.add(projectionMap.get(key));
				index = index + 1;
			}
		}

	}

	private void prepareJoinOrder() {

		List<String> list = (List<String>) queryTemplate.get(QueryElements.ELEMENT_JOIN);

		if (list != null) {

			for (String item : list) {

				List<String> aliasList = new ArrayList();

				String alias1 = item.substring(0, item.indexOf(QueryElements.DOT));
				String alias2 = item.substring(item.indexOf(QueryElements.EQUAL_TO) + 1,
						item.indexOf(QueryElements.DOT, item.indexOf(QueryElements.EQUAL_TO)));

				aliasList.add(alias1);
				aliasList.add(alias2);

				joinMap.put(item, aliasList);

				if (aliaslist.contains(alias1)) {
					joinOrder.add(alias1);
					aliaslist.remove(alias1);
				}

				if (aliaslist.contains(alias2)) {
					joinOrder.add(alias2);
					aliaslist.remove(alias2);
				}

			}
		}

		for (String alias : aliaslist) {
			joinOrder.add(alias);
		}

	}

	private void prepareTable() {

		List<String> list = (List) queryTemplate.get(QueryElements.ELEMENT_TABLE);

		for (String item : list) {
			String setArray[] = item.split(QueryElements.SPACE);
			aliasToTable.put(setArray[1], setArray[0]);
			tableToAlias.put(setArray[0], setArray[1]);

			aliaslist.add(setArray[1]);
		}

	}

	private void prepareFilterInDb() {

		List<String> list = (List<String>) queryTemplate.get(QueryElements.ELEMENT_FILTER_IN_DB);

		if (list != null) {

			for (String item : list) {

				item = TextUtils.replaceParameters(item, parameters);

				String alias = item.substring(0, item.indexOf(QueryElements.DOT));

				if (aliasToDbfilter.containsKey(alias)) {
					aliasToDbfilter.get(alias).add(item);
				} else {
					List<String> itemlist = new ArrayList();
					itemlist.add(item);
					aliasToDbfilter.put(alias, itemlist);
				}
			}
		}

	}

	private void prepareFilterInMemory() {

		List<String> list = (List<String>) queryTemplate.get(QueryElements.ELEMENT_FILTER_IN_MEMORY);

		if (list != null) {

			for (Object obj : list) {

				if (obj instanceof String) {

					String item = (String) obj;

					// item = StringUtils.replaceParameters(item, parameters);

					String alias = item.substring(0, item.indexOf(QueryElements.DOT));

					if (aliasToInMemoryfilter.containsKey(alias)) {
						aliasToInMemoryfilter.get(alias).add(item);
					} else {
						List<String> itemlist = new ArrayList();
						itemlist.add(item);
						aliasToInMemoryfilter.put(alias, itemlist);
					}
				} else if (obj instanceof JSONObject) {

					JSONObject json = (JSONObject) obj;

					if (containsMultipleTableFilters(json, null)) {
						inMemoryComplexFilterPost.add(json);
					} else {
						inMemoryComplexFilterPre.add(json);
					}
				}
			}
		}

	}

	private boolean containsMultipleTableFilters(JSONObject input, String startingTableAlias) {

		boolean outcome = false;

		JSONArray operands = input.optJSONArray(QueryElements.OPERAND);

		for (int i = 0; i < operands.length(); i++) {

			Object obj = operands.opt(i);

			if (obj instanceof String) {

				String operand = (String) obj;

				String currentAlias = operand.substring(0, operand.indexOf(QueryElements.DOT));

				if (startingTableAlias != null && !currentAlias.equals(startingTableAlias)) {
					outcome = true;
				} else {
					startingTableAlias = currentAlias;
				}

			} else if (obj instanceof JSONObject) {
				outcome = containsMultipleTableFilters((JSONObject) obj, startingTableAlias);
			}

			if (outcome) {
				return outcome;
			}

		}

		return outcome;

	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	private KeyValuePair searchForAlternateKeys(JSONObject json, String... possibleKeys) {

		for (String key : possibleKeys) {
			if (json.has(key)) {
				return new KeyValuePair(key, json.opt(key));
			}
		}

		return null;

	}

}
