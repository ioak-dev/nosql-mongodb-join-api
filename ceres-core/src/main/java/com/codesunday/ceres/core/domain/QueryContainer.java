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

import org.apache.log4j.Logger;
import org.codehaus.jackson.node.ObjectNode;

import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.exception.CeresException;

public class QueryContainer {

	private static Logger logger = Logger.getLogger(QueryContainer.class);

	private Map<String, Map<String, QueryTemplate>> map;

	private static final String QUERY_NOT_FOUND = "QUERY_NOT_FOUND ";
	private static final String QUERY_LOADING_ERROR = "Exception while loading query";

	private static final String QUERY_LOADED_SUCCESS = "Query loaded - ";

	/**
	 * Constructor
	 * 
	 * @param files
	 */
	public QueryContainer() {
		map = new HashMap();
	}

	/**
	 * Add a new query
	 * 
	 * @param json
	 */
	public void append(ObjectNode json) {

		if (json != null) {

			try {

				QueryTemplate queryTemplate = new QueryTemplate(json);

				String context = queryTemplate.get(QueryElements.ELEMENT_CONTEXT).getTextValue();
				String id = queryTemplate.get(QueryElements.ELEMENT_ID).getTextValue();

				if (map.containsKey(context)) {
					Map<String, QueryTemplate> qtMap = map.get(context);

					if (qtMap.containsKey(id)) {
						// Warning
					} else {
						qtMap.put(id, queryTemplate);
					}

				} else {
					Map<String, QueryTemplate> qtMap = new HashMap();

					qtMap.put(id, queryTemplate);

					map.put(context, qtMap);
				}

				logger.info(QUERY_LOADED_SUCCESS + context + "/" + id);

			} catch (Exception e) {
				throw new CeresException(QUERY_LOADING_ERROR + json.toString());
			}
		}

	}

	/**
	 * Add a list of new queries
	 * 
	 * @param list
	 */
	public void append(List<ObjectNode> list) {

		for (ObjectNode json : list) {
			append(json);
		}

	}

	/**
	 * Get the query object corresponding to the context and query id given
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public QueryTemplate getQueryTemplate(String context, String id) {

		if (map.containsKey(context) && map.get(context).containsKey(id)) {
			return map.get(context).get(id);
		} else {
			throw new CeresException(QUERY_NOT_FOUND + context + "/" + id);
		}

	}

	public Map<String, Map<String, QueryTemplate>> getAllQueries() {
		return map;
	}

	@Override
	public String toString() {
		return map.toString();
	}

}
