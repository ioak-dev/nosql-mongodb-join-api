package com.codesunday.ceres.sample.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.codesunday.ceres.core.client.CeresClient;
import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.domain.Result;

public class SampleClient {

	private static ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) {

		// Get an instance of baseclient by passing the query resource path and
		// database identifier
		// database driver examples = mongo, cassandra, oracle, etc
		// database name examples = employeedb, shoppingdb

		int displaySize = 1;

		BasicConfigurator.configure();

		ObjectNode queryJson = mapper.createObjectNode();
		queryJson.put(QueryElements.ELEMENT_TABLE, "queries a");
		queryJson.put(QueryElements.ELEMENT_DROP_ALIAS, "true");

		String appContextFilepath = "resources/application-context.json";
		String queryPath = "resources/queries";
		String templatePath = "resources/templates";
		// String queryPath =
		// "/run/media/arun/f2677a9b-a9cc-4b68-b4f5-78cb9c797efc/workspace/201609/ceres-examples/resources/queries";
		String databaseDriver = "mongo";
		String databaseName = "sakila_uat_instance";

		CeresClient client = CeresClient.getInstance(appContextFilepath, databaseDriver, databaseName);
		client.addQueriesFromFilesystem(queryPath);
		// client.addQueriesFromDatabase(queryJson);
		client.addTemplatesFromFilesystem(templatePath);

		Map<String, Object> parameters = new HashMap();
		parameters.put("city", "Lethbridge");
		parameters.put("country", "Canada");
		// parameters.put("actor_first_name", "PENELOPE");
		// parameters.put("actor_last_name", "GUINESS");
		// parameters.put("film_title", "ACADEMY DINOSAUR");

		parameters.put("actor_first_name", "CARMEN");
		parameters.put("actor_last_name", "GUINESS");
		parameters.put("film_title", "MOONSHINE CABIN,BEDAZZLED MARRIED,ACADEMY DINOSAUR");

		// client.find("default", "basic3", parameters);
		Result result = client.find("default", "test5", parameters);

		for (String viewName : result.getViewNames()) {
			List<ObjectNode> list = result.getView(viewName);

			System.out.println(viewName + " - " + list.size());

			for (int i = 0; i < displaySize; i++) {
				System.out.println(list.get(i).toString());
			}
		}

		for (String viewName : result.getViewNames()) {
			List<ObjectNode> list = result.getView(viewName);

			System.out.println(viewName + " - " + list.size());
		}
	}

}
