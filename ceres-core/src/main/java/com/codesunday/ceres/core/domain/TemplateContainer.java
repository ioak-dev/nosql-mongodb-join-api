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

public class TemplateContainer {

	private static Logger logger = Logger.getLogger(QueryContainer.class);

	private Map<String, ObjectNode> map;

	private static final String TEMPLATE_NAME_NOT_DEFINED = "_template tag not defined. this is used to identify the template";
	private static final String TEMPLATE_NAME_NOT_VALID = "template identifier tag should end with .json";

	private static final String TEMPLATE_NOT_FOUND = "TEMPLATE_NOT_FOUND ";
	private static final String TEMPLATE_LOADING_ERROR = "Exception while loading template";

	private static final String TEMPLATE_LOADED_SUCCESS = "Template loaded - ";

	/**
	 * Constructor
	 * 
	 * @param files
	 */
	public TemplateContainer() {
		map = new HashMap();
	}

	/**
	 * Add a new template
	 * 
	 * @param json
	 */
	public void append(ObjectNode json) {

		if (json != null) {

			try {

				KeyValuePair pair = searchForAlternateKeys(json, QueryElements._TEMPLATE, QueryElements._VIEW,
						QueryElements._TEMPLATE_ID, QueryElements._TEMPLATE_NAME);

				if (pair != null) {

					String value = ((String) pair.value).trim();
					pair.value = value;

					if (value.endsWith(QueryElements.DOT_JSON)) {

						json.remove(pair.key);

						map.put(value, json);

					} else {
						throw new CeresException(TEMPLATE_NAME_NOT_VALID);
					}

				} else {
					throw new CeresException(TEMPLATE_NAME_NOT_DEFINED);
				}

				logger.info(TEMPLATE_LOADED_SUCCESS + pair.value);

			} catch (Exception e) {
				throw new CeresException(TEMPLATE_LOADING_ERROR + json.toString(), e);
			}
		}

	}

	/**
	 * Add a list of new templates
	 * 
	 * @param list
	 */
	public void append(List<ObjectNode> list) {

		for (ObjectNode json : list) {
			append(json);
		}

	}

	/**
	 * Get the template corresponding to the template identifier provided.
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public ObjectNode getProjectTemplate(String templateName) {

		if (map.containsKey(templateName)) {
			return map.get(templateName);
		} else {
			throw new CeresException(TEMPLATE_NOT_FOUND + templateName);
		}

	}

	@Override
	public String toString() {
		return map.toString();
	}

	private KeyValuePair searchForAlternateKeys(ObjectNode json, String... possibleKeys) {

		for (String key : possibleKeys) {
			if (json.has(key)) {
				return new KeyValuePair(key, json.get(key).getTextValue());
			}
		}

		return null;

	}

}
