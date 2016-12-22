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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.domain.FilterData;
import com.codesunday.ceres.core.domain.JoinData;
import com.codesunday.ceres.core.domain.Table;
import com.codesunday.ceres.core.domain.TransactionContext;
import com.codesunday.ceres.core.utils.CoreUtils;
import com.codesunday.proteus.core.utils.JSONUtils;

public class QueryOperations {

	private static ObjectMapper mapper = new ObjectMapper();

	private TransactionContext transactionContext;

	public QueryOperations(TransactionContext transactionContext) {
		this.transactionContext = transactionContext;
	}

	public Table filter(Table input, List<Object> filterDataList) {

		long startTime = System.currentTimeMillis();

		List<ObjectNode> rows = input.getRows();

		Table result = new Table();

		if (rows.size() > 0) {

			for (ObjectNode row : rows) {

				if (validateRow(row, filterDataList)) {
					result.addRow(row);
				}
			}
		}

		transactionContext.logCapsule.logTime(this.getClass(), transactionContext, startTime, result.size(),
				filterDataList.toString());

		return result;
	}

	private boolean validateRow(ObjectNode row, List<Object> filterDataList) {

		int truth = 0;
		int filterSize = filterDataList.size() - 1;

		Object obj = filterDataList.get(0);

		boolean disjunction = false;

		if (obj instanceof String && ((String) obj).equals(QueryElements.OR)) {
			disjunction = true;
		}

		for (int i = 1; i < filterDataList.size(); i++) {

			Object filterObj = filterDataList.get(i);

			boolean innerTruth = true;

			if (filterObj instanceof FilterData) {

				FilterData filterData = (FilterData) filterObj;

				List<String> vauesRight = filterData.getValues();
				List<String> vauesLeft = JSONUtils.getValueAsTextual(row, filterData.getColumn());

				if (Collections.disjoint(vauesLeft, vauesRight)) {
					innerTruth = false;
				}

				if (filterData.isEquality() && innerTruth) {
					truth += 1;
				} else if (!filterData.isEquality() && !innerTruth) {
					truth += 1;
				}
			} else if (filterObj instanceof List) {
				if (validateRow(row, (List) filterObj)) {
					truth += 1;
				}
			}

		}
		if (disjunction) {
			if (filterSize == 0 || truth > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if (filterSize == 0 || truth == filterSize) {
				return true;
			} else {
				return false;
			}
		}

	}

	public Table join(Table table1, Table table2, List<JoinData> joinDataList, String stage) {

		long startTime = System.currentTimeMillis();

		List<ObjectNode> tuples1;
		List<ObjectNode> tuples2;
		if (table1.size() < table2.size()) {
			tuples1 = table1.getRows();
			tuples2 = table2.getRows();
		} else {
			tuples1 = table2.getRows();
			tuples2 = table1.getRows();
			for (JoinData jc : joinDataList) {
				jc.swap();
			}
		}

		Table result = new Table();

		if (tuples1.size() == 0 || tuples2.size() == 0) {

			if (stage != null) {
				// logger.log(Level.INFO,
				// LoggerUtils.queryLog(stage, 0, startTime));
			}

			return result;
		}

		// Multimap<String, Row> mtuples1 = ArrayListMultimap.create();
		// Multimap<String, Row> mtuples2 = ArrayListMultimap.create();

		Map<Object, List<ObjectNode>> mtuples1 = new HashMap();
		Map<Object, List<ObjectNode>> mtuples2 = new HashMap();

		if (joinDataList != null && joinDataList.size() > 0) {
			for (ObjectNode tup : tuples1) {
				List<JsonNode> keyList = new ArrayList();
				boolean allKeyJsonNodesPresent = true;
				for (JoinData JoinData : joinDataList) {
					List<JsonNode> localKeyList = JSONUtils.getValue(tup, JoinData.getColumn1());
					// System.out.println("KI-1::" + keyList);
					if (localKeyList.size() > 0) {

						keyList = CoreUtils.concatenate(keyList, localKeyList);

						// System.out.println("keyStringList-1::" + keyList);

					} else {
						allKeyJsonNodesPresent = false;
						break;
					}
				}

				if (allKeyJsonNodesPresent) {

					for (Object obj : keyList) {

						if (mtuples1.containsKey(obj)) {
							mtuples1.get(obj).add(tup);
						} else {
							List<ObjectNode> list = new ArrayList();
							list.add(tup);
							mtuples1.put(obj, list);
						}
					}

				}
			}

			for (ObjectNode tup : tuples2) {
				List<JsonNode> keyList = new ArrayList();
				boolean allKeyJsonNodesPresent = true;
				for (JoinData JoinData : joinDataList) {
					List<JsonNode> localKeyList = JSONUtils.getValue(tup, JoinData.getColumn2());
					// System.out.println("KI-2::" + keyList);
					if (localKeyList.size() > 0) {

						keyList = CoreUtils.concatenate(keyList, localKeyList);

						// System.out.println("keyStringList-2::" + keyList);

					} else {
						allKeyJsonNodesPresent = false;
						break;
					}
				}

				if (allKeyJsonNodesPresent) {

					for (Object obj : keyList) {

						if (mtuples2.containsKey(obj)) {
							mtuples2.get(obj).add(tup);
						} else {
							List<ObjectNode> list = new ArrayList();
							list.add(tup);
							mtuples2.put(obj, list);
						}
					}
				}
			}
		} else {
			List<ObjectNode> list = new ArrayList();
			for (ObjectNode tup : tuples1) {
				list.add(tup);
			}
			mtuples1.put("NOKEYDEFINED", list);

			list = new ArrayList();
			for (ObjectNode tup : tuples2) {
				list.add(tup);
			}
			mtuples2.put("NOKEYDEFINED", list);
		}

		List<ObjectNode> rows1 = null;
		List<ObjectNode> rows2 = null;
		for (Object key : mtuples1.keySet()) {
			rows1 = mtuples1.get(key);
			rows2 = mtuples2.get(key);
			if (rows1 != null && rows2 != null) {
				for (ObjectNode row1 : rows1) {
					for (ObjectNode row2 : rows2) {
						ObjectNode row = mapper.createObjectNode();
						JSONUtils.putAll(row, row1);
						JSONUtils.putAll(row, row2);
						result.addRow(row);
					}
				}
			}
		}

		if (stage != null) {
			// logger.log(Level.INFO,
			// LoggerUtils.queryLog(stage, result.size(), startTime));
		}
		return result;
	}

	protected static Object nvl(Object source, String onNull) {
		if (source != null) {
			return source;
		} else {
			return onNull;
		}

	}

}
