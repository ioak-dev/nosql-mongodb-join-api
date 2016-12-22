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

package com.codesunday.ceres.core.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.node.ArrayNode;

import com.codesunday.ceres.core.constants.QueryElements;

public class TextUtils {

	public static String replaceParameters(String text, Map<String, Object> parameters) {

		if (text != null && parameters != null) {

			for (String key : parameters.keySet()) {

				Object parameterValue = parameters.get(key);

				if (parameterValue instanceof List) {
					List items = (List) parameterValue;

					StringBuilder sb = new StringBuilder();

					for (Object item : items) {
						sb = sb.append(item).append(",");
					}

					parameterValue = sb;
				} else if (parameterValue instanceof ArrayNode) {
					ArrayNode items = (ArrayNode) parameterValue;

					StringBuilder sb = new StringBuilder();

					for (Object item : items) {
						sb = sb.append(item).append(",");
					}

					parameterValue = sb;
				}

				text = text.replaceAll(
						QueryElements.DYNAMIC_PARAMETER_START + key + QueryElements.DYNAMIC_PARAMETER_END,
						parameterValue.toString());

			}

		}

		return text;

	}

	public static List<String> stringToList(String text, String seperator) {

		// List<String> list = new ArrayList();
		//
		// if (text != null && !text.isEmpty()) {
		// String[] parts = text.split(seperator);
		// list = Arrays.asList(parts);
		// }
		// return list;

		return Arrays.asList(text.split(seperator));

	}

	public static String trim(String text, boolean replaceMultipleSpaces, String... keys) {
		if (text != null) {
			text = text.trim();
			if (replaceMultipleSpaces) {
				text = text.replaceAll("\\s+", " ");
			}
			for (String key : keys) {
				text = text.replaceAll(" " + key + " ", key);
				text = text.replaceAll(key + " ", key);
				text = text.replaceAll(" " + key, key);
			}
		}

		return text;
	}

}
