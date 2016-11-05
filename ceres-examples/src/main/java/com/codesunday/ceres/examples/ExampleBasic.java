package com.codesunday.ceres.examples;

import java.util.HashMap;
import java.util.Map;

import com.codesunday.ceres.core.client.CeresClient;
import com.codesunday.ceres.core.domain.Result;

public class ExampleBasic {

	private CeresClient client;

	private static final String CONTEXT_BASIC_EXAMPLES = "basic-examples";

	private static final String QUERY_SINGLE_TABLE = "single-table";
	private static final String QUERY_MULTI_TABLE_JOIN = "multi-table-join";

	// number of records to print from the output, for testing in development
	// system.
	// Negative number will print all records.
	// Zero prints no records. Just the count.
	// Positive number prints the specified number of records or all records,
	// whichever is lesser.
	private static int NUMBER_OF_RECORDS_TO_PRINT = 1;

	public ExampleBasic(CeresClient client) {
		super();
		this.client = client;
	}

	public void run() {

		singleTableQuery().print(NUMBER_OF_RECORDS_TO_PRINT);
		twoTableJoinQuery().print(NUMBER_OF_RECORDS_TO_PRINT);

	}

	private Result singleTableQuery() {
		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters.put("country", "United States");

		return client.find(CONTEXT_BASIC_EXAMPLES, QUERY_SINGLE_TABLE, parameters);
	}

	private Result twoTableJoinQuery() {

		return client.find(CONTEXT_BASIC_EXAMPLES, QUERY_MULTI_TABLE_JOIN, null);
	}
}
