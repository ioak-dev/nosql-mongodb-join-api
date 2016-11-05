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

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.codesunday.ceres.core.exception.CeresException;

public class Result {

	private Logger logger = Logger.getLogger(Result.class);

	private Map<String, List<JSONObject>> views;

	private String context;
	private String id;

	private List<String> viewNames;

	private static final String VIEW_NOT_FOUND = "view with the name specified not found";
	private static final String NO_VIEWS_EXIST = "no views exist";
	private static final String VIEW_NAME_NEEDED = "more than one view exists. specify the view name of interest";
	private static final String UNKNOWN_EXCEPTION = "unknown exception occurred";

	private static final String _DEFAULT = "_default";

	private static final String VIEW_NAME = "view_name";
	private static final String NUMBER_OF_RECORDS = "number_of_records";
	private static final String DATA = "data";

	public Result(String context, String id) {
		super();
		views = new HashMap();
		viewNames = new ArrayList();

		this.context = context;
		this.id = id;
	}

	public List<String> getViewNames() {
		return viewNames;
	}

	public boolean addView(String viewName, List<JSONObject> view) {

		if (!views.containsKey(viewName)) {
			viewNames.add(viewName);
		}

		views.put(viewName, view);

		return true;

	}

	public boolean addView(List<JSONObject> view) {

		addView(_DEFAULT, view);

		return true;

	}

	public List<JSONObject> getView(String viewName) {

		if (views.containsKey(viewName)) {
			return views.get(viewName);
		} else {
			throw new CeresException(VIEW_NOT_FOUND);
		}

	}

	public List<JSONObject> getView() {

		if (views.size() == 1) {
			return views.get(viewNames.get(0));
		} else if (views.size() == 0) {
			throw new CeresException(NO_VIEWS_EXIST);
		} else if (views.size() > 1) {
			throw new CeresException(VIEW_NAME_NEEDED);
		} else {
			throw new CeresException(UNKNOWN_EXCEPTION);
		}

	}

	public void print(int numberOfRowsToShow) {

		String queryIdentifierTag = "[" + context + "/" + id + "] ";

		JSONArray jsonToPrint = new JSONArray();

		for (String viewName : this.getViewNames()) {

			JSONObject json = new JSONObject();

			List<JSONObject> list = this.getView(viewName);

			json.put(VIEW_NAME, viewName);
			json.put(NUMBER_OF_RECORDS, list.size());

			if (numberOfRowsToShow != 0) {

				int rowsToShow = numberOfRowsToShow;

				if (rowsToShow < 0) {
					rowsToShow = list.size();
				} else if (rowsToShow > list.size()) {
					rowsToShow = list.size();
				}

				JSONArray rows = new JSONArray();

				for (int i = 0; i < rowsToShow; i++) {
					rows.put(list.get(i));
				}

				json.put(DATA, rows);
			}

			jsonToPrint.put(json);
		}

		logger.info(queryIdentifierTag + jsonToPrint.toString(3));
	}
}
