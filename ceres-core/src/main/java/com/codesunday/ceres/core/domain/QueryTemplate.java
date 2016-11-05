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
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.exception.CeresException;
import com.codesunday.ceres.core.utils.TextUtils;

public class QueryTemplate extends QueryElements {

	private JSONObject json;

	private static final String EMPTY_QUERY = "EMPTY_QUERY_DEFINITION";
	private static final String NO_TABLE_DEFINED_IN_QUERY_DEFINITION = "NO_TABLE_DEFINED_IN_QUERY_DEFINITION";

	private static final String MALFORMED_PROJECTION_CLAUSE = "MALFORMED_PROJECTION_CLAUSE";

	public boolean jsonBasedProjection = false;
	public boolean projectPresent = false;

	/**
	 * Constructor with query json as argument
	 * 
	 * @param json
	 */
	public QueryTemplate(JSONObject json) {
		super();

		if (json != null && json.length() > 0) {

			this.json = json;
			format();

		} else {
			throw new CeresException(EMPTY_QUERY);
		}
	}

	private void format() {
		try {

			formatContext(QueryElements.ELEMENT_CONTEXT, QueryElements.ELEMENT_CONTEXT, QueryElements.ELEMENT_GROUP);

			formatId(QueryElements.ELEMENT_ID, QueryElements.ELEMENT_ID, QueryElements.ELEMENT_NAME,
					QueryElements.ELEMENT_QUERYID);

			formatTable(QueryElements.ELEMENT_TABLE, QueryElements.ELEMENT_TABLE, QueryElements.ELEMENT_TABLES,
					QueryElements.ELEMENT_FROM, QueryElements.ELEMENT_COLLECTION, QueryElements.ELEMENT_COLLECTIONS);

			formatDbFilter(QueryElements.ELEMENT_FILTER_IN_DB, QueryElements.ELEMENT_FILTER_IN_DB,
					QueryElements.ELEMENT_DB_FILTER);

			formatInMemoryFilter(QueryElements.ELEMENT_FILTER_IN_MEMORY, QueryElements.ELEMENT_FILTER_IN_MEMORY,
					QueryElements.ELEMENT_IN_MEMORY_FILTER);

			formatJoin(QueryElements.ELEMENT_JOIN, QueryElements.ELEMENT_JOIN, QueryElements.ELEMENT_WHERE);

			formatProject(QueryElements.ELEMENT_PROJECT, QueryElements.ELEMENT_PROJECT, QueryElements.ELEMENT_SELECT,
					QueryElements.ELEMENT_PROJECTION, QueryElements.ELEMENT_TEMPLATE, QueryElements.ELEMENT_TEMPLATES);

			formatDropAlias(QueryElements.ELEMENT_DROP_ALIAS, QueryElements.ELEMENT_DROP_ALIAS,
					QueryElements.ELEMENT_DROP_ALIASES, QueryElements.ELEMENT_DROP_TABLE_ALIAS,
					QueryElements.ELEMENT_DROP_TABLE_ALIASES);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void formatContext(String key, String... pseudoKeys) throws JSONException {

		KeyValuePair pair = searchForAlternateKeys(json, pseudoKeys);

		if (pair == null) {
			json.put(key, QueryElements.CONTEXT_DEFAULT);
		} else {
			json.remove(pair.key);
			json.put(key, pair.value);
		}

	}

	private void formatId(String key, String... pseudoKeys) throws JSONException {
		KeyValuePair pair = searchForAlternateKeys(json, pseudoKeys);

		if (pair == null) {
			json.put(key, QueryElements.ID_DEFAULT);
		} else {
			json.remove(pair.key);
			json.put(key, pair.value);
		}

	}

	private void formatTable(String key, String... pseudoKeys) throws JSONException {

		KeyValuePair pair = searchForAlternateKeys(json, pseudoKeys);

		pair = toArrayList(pair, QueryElements.COMMA, QueryElements.EQUAL_TO);

		if (pair != null) {
			json.remove(pair.key);
			json.put(key, pair.value);
		} else {
			throw new CeresException(NO_TABLE_DEFINED_IN_QUERY_DEFINITION);
		}

	}

	private void formatDbFilter(String key, String... pseudoKeys) throws JSONException {

		KeyValuePair pair = searchForAlternateKeys(json, pseudoKeys);

		pair = toArrayList(pair, QueryElements.AND_SPACE, QueryElements.EQUAL_TO);

		if (pair != null) {
			json.remove(pair.key);
			json.put(key, pair.value);

		}

	}

	private void formatInMemoryFilter(String key, String... pseudoKeys) throws JSONException {

		KeyValuePair pair = searchForAlternateKeys(json, pseudoKeys);

		pair = toArrayList(pair, QueryElements.AND_SPACE, QueryElements.EQUAL_TO);

		if (pair != null) {
			json.remove(pair.key);
			json.put(key, pair.value);

		}

	}

	private void formatJoin(String key, String... pseudoKeys) throws JSONException {

		KeyValuePair pair = searchForAlternateKeys(json, pseudoKeys);

		pair = toArrayList(pair, QueryElements.AND_SPACE, QueryElements.EQUAL_TO);

		if (pair != null) {
			json.remove(pair.key);
			json.put(key, pair.value);

		}

	}

	private void formatProject(String key, String... pseudoKeys) throws JSONException {

		KeyValuePair pair = searchForAlternateKeys(json, pseudoKeys);

		pair = toArrayList(pair, QueryElements.COMMA);

		if (pair != null) {

			List<Object> list = (List<Object>) pair.value;

			if (list != null && list.size() > 0) {

				projectPresent = true;

				boolean stringPresent = false;

				boolean allString = true;

				for (Object obj : list) {
					if (obj instanceof String && ((String) obj).trim().toLowerCase().endsWith(QueryElements.DOT_JSON)) {
						allString = false;
					} else if (!(obj instanceof String)) {
						allString = false;
					}
				}

				if (!allString) {

					for (Object obj : list) {

						if (obj instanceof String
								&& !((String) obj).trim().toLowerCase().endsWith(QueryElements.DOT_JSON)) {
							if (jsonBasedProjection) {
								throw new CeresException(MALFORMED_PROJECTION_CLAUSE);
							} else {
								// if (!stringPresent) {
								stringPresent = true;
							}
							// } else {
							// throw new
							// CeresException(MALFORMED_PROJECTION_CLAUSE);
							// }
						} else if (obj instanceof JSONArray) {
							throw new CeresException(MALFORMED_PROJECTION_CLAUSE);
						} else if (obj instanceof JSONObject || (obj instanceof String
								&& ((String) obj).trim().toLowerCase().endsWith(QueryElements.DOT_JSON))) {
							if (stringPresent) {
								throw new CeresException(MALFORMED_PROJECTION_CLAUSE);
							} else {
								jsonBasedProjection = true;
							}

							if (obj instanceof JSONObject) {
								JSONObject jsontemplate = (JSONObject) obj;
								KeyValuePair templateNamePair = searchForAlternateKeys(jsontemplate,
										QueryElements._TEMPLATE, QueryElements._VIEW, QueryElements._TEMPLATE_ID,
										QueryElements._TEMPLATE_NAME);

								if (templateNamePair != null) {
									jsontemplate.remove(templateNamePair.key);
									jsontemplate.put(QueryElements._TEMPLATE, templateNamePair.value);
								} else {
									jsontemplate.put(QueryElements._TEMPLATE, QueryElements._DEFAULT);
								}
							}
						}

					}

				}

			}

			// json.remove(pair.key);
			json.put(key, pair.value);

		}

	}

	private void formatDropAlias(String key, String... pseudoKeys) throws JSONException {

		KeyValuePair pair = searchForAlternateKeys(json, pseudoKeys);

		json.put(key, false);

		if (pair != null) {
			if (pair.value.equals(QueryElements.TRUE) || pair.value.equals(QueryElements.YES)
					|| pair.value.equals(QueryElements.Y)) {
				json.put(key, true);
			}
		}

	}

	private KeyValuePair toArrayList(KeyValuePair pair, String separator, String... trimmer) {

		if (pair != null) {

			List<Object> list = new ArrayList();

			if (pair.value instanceof String) {

				String value = (String) pair.value;

				if (trimmer.length > 0) {
					value = TextUtils.trim(value, true, trimmer);
				}

				list.addAll(TextUtils.stringToList(value, separator));

			} else if (pair.value instanceof JSONArray) {

				JSONArray array = (JSONArray) pair.value;

				for (int i = 0; i < array.length(); i++) {

					Object obj = array.opt(i);

					if (obj instanceof JSONObject) {
						list.add(obj);
					} else {

						String item = array.optString(i).replaceAll("\\s+", " ").trim();

						if (trimmer.length > 0) {
							item = TextUtils.trim(item, true, trimmer);
						}

						list.addAll(TextUtils.stringToList(item, separator));
					}
				}
			} else if (pair.value instanceof JSONObject) {
				list.add(pair.value);
			}

			pair.value = list;
		}

		return pair;
	}

	// private KeyValuePair toArrayList(KeyValuePair pair) {
	//
	// if (pair != null) {
	//
	// List<Object> list = new ArrayList();
	//
	// if (pair.value instanceof String) {
	//
	// String value = (String) pair.value;
	//
	// list.add(value);
	//
	// } else if (pair.value instanceof JSONArray) {
	//
	// JSONArray array = (JSONArray) pair.value;
	//
	// for (int i = 0; i < array.length(); i++) {
	//
	// Object obj = array.opt(i);
	//
	// if (obj instanceof JSONObject) {
	// list.add(obj);
	// } else if (obj instanceof String) {
	//
	// String item = array.optString(i).trim();
	// list.add(item);
	// }
	// }
	// } else if (pair.value instanceof JSONObject) {
	// list.add(pair.value);
	// }
	//
	// pair.value = list;
	// }
	//
	// return pair;
	// }

	private KeyValuePair toPlainText(KeyValuePair pair) {

		if (pair != null) {

			if (pair.value instanceof String) {

				String value = (String) pair.value;

				value = value.replaceAll("\\s+", " ").trim();
				value = TextUtils.trim(value, true, QueryElements.EQUAL_TO);

				pair.value = value;

			}

			if (pair.value instanceof JSONArray) {

				JSONArray array = (JSONArray) pair.value;

				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < array.length(); i++) {

					String item = array.optString(i);

					sb.append(item);

					if (i != array.length() - 1) {
						sb.append(QueryElements.AND_SPACE);
					}

				}

				String value = sb.toString();

				value = value.replaceAll("\\s+", " ").trim();
				value = TextUtils.trim(value, true, QueryElements.EQUAL_TO);

				pair.value = value;

			}
		}

		return pair;
	}

	public QueryInstance getInstance(ApplicationContext applicationContext, Map<String, Object> parameters) {
		QueryInstance queryInstance = new QueryInstance(applicationContext, this, parameters);
		return queryInstance;
	}

	/**
	 * Get a field/element value as a text/string from the query object. This
	 * will be used in most elements, that carry regular string information, as
	 * opposed to array/object fields
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {

		Object value = null;

		if (json.has(key)) {
			value = json.opt(key);
		}

		return value;

	}

	private KeyValuePair searchForAlternateKeys(JSONObject jsoninput, String... possibleKeys) {

		for (String key : possibleKeys) {
			if (jsoninput.has(key)) {
				return new KeyValuePair(key, jsoninput.opt(key));
			}
		}

		return null;

	}

	@Override
	public String toString() {
		return json.toString();
	}

}

class KeyValuePair {

	public KeyValuePair(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String key;
	public Object value;
}