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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.utils.TextUtils;

public class QueryInstance {

	private static ObjectMapper mapper = new ObjectMapper();

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

	public List<ObjectNode> inMemoryComplexFilterPre = new ArrayList();
	public List<ObjectNode> inMemoryComplexFilterPost = new ArrayList();

	public boolean dropAlias = false;
	public boolean projectPresent = false;
	public boolean jsonBasedProjection = false;

	public List<ObjectNode> projectionList = new ArrayList();
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

		context = queryTemplate.get(QueryElements.ELEMENT_CONTEXT).getTextValue();
		id = queryTemplate.get(QueryElements.ELEMENT_ID).getTextValue();

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

		dropAlias = queryTemplate.get(QueryElements.ELEMENT_DROP_ALIAS).getBooleanValue();

	}

	private void prepareProject() {

		if (queryTemplate.projectPresent) {

			ArrayNode list = (ArrayNode) queryTemplate.get(QueryElements.ELEMENT_PROJECT);

			if (list != null && list.size() > 0) {

				Map<String, ObjectNode> projectionMap = new HashMap();

				jsonBasedProjection = queryTemplate.jsonBasedProjection;
				projectPresent = queryTemplate.projectPresent;

				if (projectPresent) {
					if (jsonBasedProjection) {
						for (JsonNode obj : list) {
							if (obj.isTextual()) {

								String templateName = obj.getTextValue();

								projectionMap.put(templateName,
										applicationContext.templateContainer.getProjectTemplate(templateName));

							} else if (obj.isObject()) {

								projectionMap.put(obj.get(QueryElements._TEMPLATE).getTextValue(), (ObjectNode) obj);
							}
						}
					} else {

						ObjectNode projectObject = mapper.createObjectNode();

						for (JsonNode obj : list) {
							if (obj.isTextual()) {
								String fieldPair[] = (obj.getTextValue()).split(QueryElements.AS_SPACE);

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

	}

	private void prepareJoinOrder() {

		ArrayNode list = (ArrayNode) queryTemplate.get(QueryElements.ELEMENT_JOIN);

		if (list != null) {

			for (JsonNode itemNode : list) {

				String item = itemNode.getTextValue();

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

		ArrayNode list = (ArrayNode) queryTemplate.get(QueryElements.ELEMENT_TABLE);

		for (JsonNode itemNode : list) {

			String item = itemNode.getTextValue();

			String setArray[] = item.split(QueryElements.SPACE);
			aliasToTable.put(setArray[1], setArray[0]);
			tableToAlias.put(setArray[0], setArray[1]);

			aliaslist.add(setArray[1]);
		}

	}

	private void prepareFilterInDb() {

		ArrayNode list = (ArrayNode) queryTemplate.get(QueryElements.ELEMENT_FILTER_IN_DB);

		if (list != null) {

			for (JsonNode itemNode : list) {

				String item = itemNode.getTextValue();

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

		ArrayNode list = (ArrayNode) queryTemplate.get(QueryElements.ELEMENT_FILTER_IN_MEMORY);

		if (list != null) {

			for (JsonNode obj : list) {

				if (obj.isTextual()) {

					String item = obj.getTextValue();

					// item = StringUtils.replaceParameters(item, parameters);

					String alias = item.substring(0, item.indexOf(QueryElements.DOT));

					if (aliasToInMemoryfilter.containsKey(alias)) {
						aliasToInMemoryfilter.get(alias).add(item);
					} else {
						List<String> itemlist = new ArrayList();
						itemlist.add(item);
						aliasToInMemoryfilter.put(alias, itemlist);
					}
				} else if (obj.isObject()) {

					if (containsMultipleTableFilters((ObjectNode) obj, null)) {
						inMemoryComplexFilterPost.add((ObjectNode) obj);
					} else {
						inMemoryComplexFilterPre.add((ObjectNode) obj);
					}
				}
			}
		}

	}

	private boolean containsMultipleTableFilters(ObjectNode input, String startingTableAlias) {

		boolean outcome = false;

		ArrayNode operands = (ArrayNode) input.get(QueryElements.OPERAND);

		for (JsonNode operandNode : operands) {

			if (operandNode.isTextual()) {

				String operand = operandNode.getTextValue();

				String currentAlias = operand.substring(0, operand.indexOf(QueryElements.DOT));

				if (startingTableAlias != null && !currentAlias.equals(startingTableAlias)) {
					outcome = true;
				} else {
					startingTableAlias = currentAlias;
				}

			} else if (operandNode.isObject()) {
				outcome = containsMultipleTableFilters((ObjectNode) operandNode, startingTableAlias);
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

	private KeyValuePair searchForAlternateKeys(ObjectNode json, String... possibleKeys) {

		for (String key : possibleKeys) {
			if (json.has(key)) {
				return new KeyValuePair(key, json.get(key));
			}
		}

		return null;

	}

}
