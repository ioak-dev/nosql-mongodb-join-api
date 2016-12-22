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
import java.util.List;

import org.codehaus.jackson.node.ObjectNode;

public class Table {

	private List<ObjectNode> rows;

	public Table() {
		super();
		rows = new ArrayList();
	}

	public void addRow(ObjectNode row) {
		rows.add(row);
	}

	public int size() {
		return rows.size();
	}

	public List<ObjectNode> getRows() {
		return rows;
	}

	public void setRows(List<ObjectNode> rows) {
		this.rows = rows;
	}

}
