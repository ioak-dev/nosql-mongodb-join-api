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

package com.codesunday.ceres.core.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.domain.ApplicationContext;
import com.codesunday.ceres.core.domain.FilterData;
import com.codesunday.ceres.core.domain.JoinData;
import com.codesunday.ceres.core.domain.QueryInstance;
import com.codesunday.ceres.core.domain.QueryTemplate;
import com.codesunday.ceres.core.domain.Result;
import com.codesunday.ceres.core.domain.Table;
import com.codesunday.ceres.core.domain.TransactionContext;
import com.codesunday.ceres.core.utils.CoreUtils;
import com.codesunday.ceres.core.utils.TextUtils;
import com.codesunday.proteus.core.client.ProteusClient;

public class QueryProcessor {

	private Logger logger = LogManager.getLogger(QueryProcessor.class);

	private ApplicationContext applicationContext;
	private TransactionContext transactionContext;

	private Map<String, Object> parameters;

	private QueryOperations queryOperations;

	public QueryProcessor(ApplicationContext applicationContext, TransactionContext transactionContext,
			Map<String, Object> parameters) {

		this.applicationContext = applicationContext;
		this.transactionContext = transactionContext;

		this.parameters = parameters;

		queryOperations = new QueryOperations(transactionContext);

	}

	private void filterInDb(QueryInstance queryInstance) {

		for (String alias : queryInstance.aliasToTable.keySet()) {

			long startTime = System.currentTimeMillis();

			List<String> condition = null;

			if (queryInstance.aliasToDbfilter.containsKey(alias)) {
				condition = queryInstance.aliasToDbfilter.get(alias);
			}

			Table table = CoreUtils.jsonToTable(transactionContext.driver.find(queryInstance.aliasToTable.get(alias),
					alias, transactionContext, condition));

			// messenger.logCapsule.logTime(this.getClass(), messenger,
			// startTime, table.size(),
			// alias + "/" + queryInstance.aliasToTable.get(alias) + "
			// filter-in-db " + condition);

			transactionContext.addTable(alias, table);
		}

	}

	private void filterInMemory(QueryInstance queryInstance) {

		for (String alias : queryInstance.aliasToTable.keySet()) {

			List<String> condition = null;

			if (queryInstance.aliasToInMemoryfilter.containsKey(alias)) {
				condition = queryInstance.aliasToInMemoryfilter.get(alias);
			}

			if (condition != null && !condition.isEmpty()) {

				// long startTime = System.currentTimeMillis();

				Table table = transactionContext.getTable(alias);

				table = simpleFilter(table, condition);

				transactionContext.addTable(alias, table);

				// messenger.logCapsule.logTime(this.getClass(), messenger,
				// startTime, table.size(),
				// alias + "/" + queryInstance.aliasToTable.get(alias) + "
				// filter-in-memory " + condition);

			}
		}
		//
		// for (JSONObject filterDefinition :
		// queryInstance.inMemoryComplexFilterPre) {
		//
		// // long startTime = System.currentTimeMillis();
		//
		// Map<String, List<Object>> filterMap =
		// prepareComplexFilter(filterDefinition);
		//
		// String alias = null;
		//
		// if (filterMap != null && !filterMap.isEmpty()) {
		// alias = filterMap.keySet().iterator().next();
		// }
		//
		// Table table = transactionContext.getTable(alias);
		//
		// table = executeComplexFilter(table, filterMap.get(alias));
		//
		// transactionContext.addTable(alias, table);
		//
		// // messenger.logCapsule.logTime(this.getClass(), messenger,
		// // startTime, table.size(),
		// // alias + "/" + queryInstance.aliasToTable.get(alias) + "
		// // filter-in-memory " + filterDefinition);
		// }

	}

	private void complexFilterInMemoryPre(QueryInstance queryInstance) {

		for (JSONObject filterDefinition : queryInstance.inMemoryComplexFilterPre) {

			// long startTime = System.currentTimeMillis();

			Map<String, List<Object>> filterMap = prepareComplexFilter(filterDefinition);

			String alias = null;

			if (filterMap != null && !filterMap.isEmpty()) {
				alias = filterMap.keySet().iterator().next();
			}

			Table table = transactionContext.getTable(alias);

			table = executeComplexFilter(table, filterMap.get(alias));

			transactionContext.addTable(alias, table);

			// messenger.logCapsule.logTime(this.getClass(), messenger,
			// startTime, table.size(),
			// alias + "/" + queryInstance.aliasToTable.get(alias) + "
			// filter-in-memory " + filterDefinition);
		}

	}

	private Table complexFilterInMemoryPost(Table table, QueryInstance queryInstance) {

		for (JSONObject filterDefinition : queryInstance.inMemoryComplexFilterPost) {

			// long startTime = System.currentTimeMillis();

			Map<String, List<Object>> filterMap = prepareComplexFilter(filterDefinition);

			String alias = null;

			if (filterMap != null && !filterMap.isEmpty()) {
				alias = filterMap.keySet().iterator().next();
			}

			table = executeComplexFilter(table, filterMap.get(alias));

			// messenger.logCapsule.logTime(this.getClass(), messenger,
			// startTime, table.size(),
			// alias + "/" + queryInstance.aliasToTable.get(alias) + "
			// filter-in-memory " + filterDefinition);
		}

		return table;

	}

	private Map<String, List<Object>> prepareComplexFilter(JSONObject filterDefinition) {

		Map<String, List<Object>> returnMap = new HashMap();
		List<Object> filters = new ArrayList();

		String alias = null;

		if (filterDefinition.optString(QueryElements.OPERATOR).equals(QueryElements.OR)) {
			filters.add(QueryElements.OR);
		} else {
			filters.add(QueryElements.AND);
		}

		JSONArray operands = filterDefinition.optJSONArray(QueryElements.OPERAND);

		for (int i = 0; i < operands.length(); i++) {
			if (operands.opt(i) instanceof JSONObject) {
				Map<String, List<Object>> map = prepareComplexFilter(operands.optJSONObject(i));

				if (map != null && !map.isEmpty()) {
					alias = map.keySet().iterator().next();
					filters.add(map.get(alias));
				}

			} else {
				String condition = operands.optString(i);
				condition = TextUtils.trim(condition, true, QueryElements.EQUAL_TO, QueryElements.NOT_EQUAL_TO,
						QueryElements.IN, QueryElements.NOT_IN);
				alias = condition.substring(0, condition.indexOf(QueryElements.DOT));
				filters.addAll(constructFilterData(condition));
			}
		}

		if (alias == null) {
			// throw new Exception();
			// TO-DO
		}

		returnMap.put(alias, filters);

		return returnMap;

	}

	private Table executeComplexFilter(Table table, List<Object> filters) {

		table = queryOperations.filter(table, filters);

		return table;

	}

	private Table simpleFilter(Table table, List<String> conditionList) {
		// TODO Auto-generated method stub

		List<Object> filters = new ArrayList();

		filters.add(QueryElements.AND);

		for (String condition : conditionList) {

			filters.addAll(constructFilterData(condition));

		}

		table = queryOperations.filter(table, filters);
		// table = queryOperations.notIn(table, unequalfilters);

		return table;
	}

	private List<Object> constructFilterData(String condition) {

		List<Object> filters = new ArrayList();

		FilterData filter = new FilterData(applicationContext.numberOfConstantsToShow);

		List<Object> constants = new ArrayList();

		if (condition.contains(QueryElements.NOT_EQUAL_TO)) {
			String[] parts = condition.split(QueryElements.NOT_EQUAL_TO);
			filter.setColumn(parts[0]);
			// constants.add(parts[1]);
			filter.setValues(parts[1], parameters);
			filter.setEquality(false);
			filters.add(filter);

		} else if (condition.contains(QueryElements.EQUAL_TO)) {
			String[] parts = condition.split(QueryElements.EQUAL_TO);
			filter.setColumn(parts[0]);
			// constants.add(parts[1]);
			filter.setValues(parts[1], parameters);
			filter.setEquality(true);
			filters.add(filter);

		} else if (condition.contains(QueryElements.NOT_IN)) {
			String[] parts = condition.split(QueryElements.NOT_IN);
			filter.setColumn(parts[0]);
			// constants = Arrays.asList(parts[1].split(QueryElements.COMMA));
			filter.setValues(parts[1], parameters);
			filter.setEquality(false);
			filters.add(filter);

		} else if (condition.contains(QueryElements.IN)) {
			String[] parts = condition.split(QueryElements.IN);
			filter.setColumn(parts[0]);
			// constants = Arrays.asList(parts[1].split(QueryElements.COMMA));
			filter.setValues(parts[1], parameters);
			filter.setEquality(true);
			filters.add(filter);

		}

		return filters;

	}

	private Table join(QueryInstance queryInstance) {

		List<String> leftAliasList = new ArrayList();

		Table result = null;

		if (queryInstance.joinOrder != null && queryInstance.joinOrder.size() == 1) {

			result = transactionContext.getTable(queryInstance.joinOrder.get(0));

		} else if (queryInstance.joinOrder != null && queryInstance.joinOrder.size() > 1) {

			String leftAlias = queryInstance.joinOrder.get(0);

			leftAliasList.add(leftAlias);

			result = transactionContext.getTable(leftAlias);

			for (int i = 1; i < queryInstance.joinOrder.size(); i++) {

				String rightAlias = queryInstance.joinOrder.get(i);

				Table rightTable = transactionContext.getTable(rightAlias);

				List<JoinData> joinDataList = new ArrayList();

				for (String condition : queryInstance.joinMap.keySet()) {
					List<String> aliasesInCondition = queryInstance.joinMap.get(condition);
					// for (String leftAlias : leftAliasList){
					// if(aliasesInCondition.con)
					// }

					if (!Collections.disjoint(leftAliasList, aliasesInCondition)
							&& aliasesInCondition.contains(rightAlias)) {

						String[] parts = condition.split(QueryElements.EQUAL_TO);

						JoinData joinData;

						if (parts[0].startsWith(rightAlias + QueryElements.DOT)) {
							joinData = new JoinData(parts[1], parts[0]);
						} else {
							joinData = new JoinData(parts[0], parts[1]);
						}

						if (joinData != null) {
							joinDataList.add(joinData);
						}

					}

				}

				long startTime = System.currentTimeMillis();

				result = queryOperations.join(result, rightTable, joinDataList, "");

				transactionContext.logCapsule.logTime(this.getClass(), transactionContext, startTime, result.size(),
						queryInstance.aliasToTable.get(leftAlias) + " " + leftAlias + " JOIN "
								+ queryInstance.aliasToTable.get(rightAlias) + " " + rightAlias + " ON "
								+ joinDataList);

				leftAliasList.add(rightAlias);
				leftAlias = rightAlias;

			}
		}

		return result;

	}

	public Result find(QueryTemplate queryTemplate, Map<String, Object> parameters) {

		QueryInstance queryInstance = queryTemplate.getInstance(applicationContext, parameters);

		transactionContext.context = queryInstance.context;
		transactionContext.id = queryInstance.id;
		transactionContext.uuid = queryInstance.uuid;

		List list = null;

		filterInDb(queryInstance);

		filterInMemory(queryInstance);

		complexFilterInMemoryPre(queryInstance);

		Table resultTable = join(queryInstance);

		resultTable = complexFilterInMemoryPost(resultTable, queryInstance);

		Result result = project(queryInstance, resultTable);

		dropAliasInResult(queryInstance, result);

		return result;
	}

	private void dropAliasInResult(QueryInstance queryInstance, Result result) {

		if (queryInstance.dropAlias && !queryInstance.projectPresent) {

			List<JSONObject> view = result.getView();

			List<JSONObject> newview = new ArrayList();

			for (JSONObject json : view) {

				JSONObject newObject = new JSONObject();

				for (String alias : queryInstance.aliasToTable.keySet()) {
					if (json.has(alias)) {
						for (String key : JSONObject.getNames(json.optJSONObject(alias))) {
							newObject.put(key, json.optJSONObject(alias).opt(key));
						}
					}
				}

				newview.add(newObject);
			}

			result.addView(newview);
		}

	}

	private Result project(QueryInstance queryInstance, Table joinResult) {

		Result result = new Result(transactionContext.context, transactionContext.id);

		if (queryInstance.projectPresent) {

			ProteusClient proteus = ProteusClient.getInstance();

			List<List<JSONObject>> views = proteus.transform(joinResult.getRows(),
					queryInstance.projectionList.toArray(new JSONObject[queryInstance.projectionList.size()]));

			for (int i = 0; i < views.size(); i++) {
				result.addView(queryInstance.projectionMapKey.get(i), views.get(i));
			}

		} else {
			result.addView(joinResult.getRows());
		}

		return result;
	}

}
