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

package com.codesunday.ceres.core.constants;

public class QueryElements {

	// default tag constants
	public static final String CONTEXT_DEFAULT = "default";
	public static final String ID_DEFAULT = "default";

	// CONTEXT tag
	public static final String ELEMENT_CONTEXT = "context";
	public static final String ELEMENT_GROUP = "group";

	// ID tag
	public static final String ELEMENT_ID = "id";
	protected static final String ELEMENT_NAME = "name";
	protected static final String ELEMENT_QUERYID = "queryid";

	// TABLE tag
	public static final String ELEMENT_TABLE = "table";
	protected static final String ELEMENT_TABLES = "tables";
	protected static final String ELEMENT_COLLECTION = "collection";
	protected static final String ELEMENT_COLLECTIONS = "collections";
	protected static final String ELEMENT_FROM = "from";

	// FILTER IN DB tag
	public static final String ELEMENT_FILTER_IN_DB = "filter-in-db";
	protected static final String ELEMENT_DB_FILTER = "db-filter";

	// FILTER IN MEMORY tag
	public static final String ELEMENT_FILTER_IN_MEMORY = "filter-in-memory";
	protected static final String ELEMENT_IN_MEMORY_FILTER = "in-memory-filter";

	// JOIN tag
	public static final String ELEMENT_JOIN = "join";
	public static final String ELEMENT_WHERE = "where";

	// PROJECT tag
	public static final String ELEMENT_PROJECT = "project";
	public static final String ELEMENT_PROJECTION = "projection";
	public static final String ELEMENT_SELECT = "select";
	public static final String ELEMENT_TEMPLATE = "template";
	public static final String ELEMENT_TEMPLATES = "templates";

	// ADDON filter tag
	public static final String ELEMENT_ADDON = "addon";
	public static final String ELEMENT_ADVANCED_FILTER = "advanced-filter";
	public static final String ELEMENT_COMPLEX_FILTER = "complex-filter";

	// DROP ALIAS
	public static final String ELEMENT_DROP_ALIAS = "drop-alias";
	public static final String ELEMENT_DROP_ALIASES = "drop-aliases";
	public static final String ELEMENT_DROP_TABLE_ALIAS = "drop-table-alias";
	public static final String ELEMENT_DROP_TABLE_ALIASES = "drop-table-aliases";

	public static final String DYNAMIC_PARAMETER_START = "\\{\\{";
	public static final String DYNAMIC_PARAMETER_END = "\\}\\}";

	public static final String PRECEDENCE_START = "[";
	public static final String PRECEDENCE_END = "]";

	public static final String COMMA = ",";
	public static final String SPACE = " ";
	public static final String DOT = ".";
	public static final String OR = "OR";
	public static final String AND = "AND";
	public static final String AND_SPACE = " AND ";
	public static final String EQUAL_TO = "=";
	public static final String NOT_EQUAL_TO = "!=";
	public static final String IN = " IN ";
	public static final String NOT_IN = " NOT IN ";

	public static final String TRUE = "true";
	public static final String YES = "yes";
	public static final String Y = "y";

	public static final String DOT_JSON = ".json";

	public static final String OPERAND = "operand";
	public static final String OPERATOR = "operator";

	public static final String _TEMPLATE = "_template";
	public static final String _TEMPLATE_NAME = "_template-name";
	public static final String _TEMPLATE_ID = "_template-id";
	public static final String _VIEW = "_view";

	public static final String AS_SPACE = " AS ";
	public static final String _DEFAULT = "_default";

}
