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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.codesunday.ceres.core.domain.Table;

public class CoreUtils {

	private static ObjectMapper mapper = new ObjectMapper();

	public static Table jsonToTable(List<ObjectNode> list) {

		Table table = new Table();

		if (list != null && list.size() > 0) {
			for (ObjectNode row : list) {
				table.addRow(row);
			}
		}

		return table;

	}

	public static int[] indexOfAll(String word, String match) {

		int[] indexes = new int[StringUtils.countMatches(word, match)];

		if (word == null) {
			return indexes;
		}

		int index = word.indexOf(match);
		int i = 0;

		while (index >= 0) {
			indexes[i] = index;
			index = word.indexOf(match, index + 1);
			i++;
		}

		return indexes;
	}

	public static int findNextIndex(int[] openParenthesis, int[] closeParenthesis, int[] parenthesis, int startIndex) {
		int start = 0;
		int match = 0;
		for (int i = 0; i < parenthesis.length; i++) {
			if (parenthesis[i] == startIndex) {
				start = i;
				break;
			}
		}

		int deduct = 0;
		for (int i = start + 1; i < parenthesis.length; i++) {
			if (ArrayUtils.contains(closeParenthesis, parenthesis[i])) {
				if (deduct == 0) {
					match = parenthesis[i];
					break;
				} else {
					deduct = deduct - 1;
				}
			} else {
				deduct = deduct + 1;
			}
		}
		return match;
	}

	public static List<JsonNode> concatenate(List<JsonNode> list1, List<JsonNode> list2) {
		List<JsonNode> returnList = new ArrayList();

		try {

			if (list1.size() == 0) {
				return list2;
			} else if (list2.size() == 0) {
				return list1;
			} else if ((list1.size() > 1 && list2.size() > 1 && list1.size() != list2.size())) {
				returnList.add(mapper.readTree(list1.get(0).toString() + list2.get(0).toString()));
			} else if (list1.size() == list2.size()) {
				for (int i = 0; i < list1.size(); i++) {
					returnList.add(mapper.readTree(list1.get(i).toString() + list2.get(i).toString()));
				}
			} else if (list1.size() == 1 && list2.size() > 1) {
				for (int i = 0; i < list2.size(); i++) {
					returnList.add(mapper.readTree(list1.get(0).toString() + list2.get(i).toString()));
				}
			} else if (list1.size() > 1 && list2.size() == 1) {
				for (int i = 0; i < list1.size(); i++) {
					returnList.add(mapper.readTree(list1.get(i).toString() + list2.get(0).toString()));
				}
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnList;

	}
}
