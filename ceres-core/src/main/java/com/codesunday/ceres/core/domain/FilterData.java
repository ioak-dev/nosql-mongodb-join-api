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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.utils.TextUtils;

public class FilterData {
	String column;
	List<String> values;
	boolean equality = false;

	int numberOfConstantsToShow;

	public FilterData(int numberOfConstantsToShow) {
		super();
		this.numberOfConstantsToShow = numberOfConstantsToShow;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public List<String> getValues() {
		return values;
	}

	// public void setValues(List<Object> values) {
	// this.values = values;
	// }

	public void setValues(String valueText, Map<String, Object> parameters) {

		valueText = TextUtils.replaceParameters(valueText, parameters);

		List<String> constants = Arrays.asList(valueText.split(QueryElements.COMMA));
		this.values = constants;
	}

	public boolean isEquality() {
		return equality;
	}

	public void setEquality(boolean equality) {
		this.equality = equality;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder(column);

		if (equality) {
			output.append(QueryElements.IN);
		} else {
			output.append(QueryElements.NOT_IN);
		}

		int size = values.size();
		if (numberOfConstantsToShow >= 0 && size > numberOfConstantsToShow) {
			size = numberOfConstantsToShow;
		}

		output.append("(");

		for (int i = 0; i < size; i++) {
			output.append(values.get(i));

			if (i < size - 1) {
				output.append(",");
			}
		}

		if (size != values.size()) {
			output.append(",..");
		}

		output.append(")");

		return output.toString();
	}
}
