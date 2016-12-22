package com.codesunday.ceres.examples;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.codesunday.ceres.core.client.CeresClient;

public class ExampleMain {

	private static final String DATABASE_DRIVER_KEY = "mongo";
	private static final String DATABASE_INSTANCE_KEY = "sakila_uat_instance";

	private static CeresClient client;

	private static final Logger logger = Logger.getLogger(ExampleMain.class);

	/**
	 * To run these examples, import the Json files from
	 * resources/import_this_to_mongo to your mongo database
	 * 
	 * MANUALLY import to mongo <- resources/import_this_to_mongo/customers.json
	 * MANUALLY import to mongo <- resources/import_this_to_mongo/films.json
	 * MANUALLY import to mongo <- resources/import_this_to_mongo/stores.json
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Simple property less log4j configuration for the examples
		BasicConfigurator.configure();

		logger.warn(
				"PREREQUISITE: To run these examples, import the Json files from resources/import_this_to_mongo to your mongo database");
		logger.warn("PREREQUISITE: Import to mongo <- resources/import_this_to_mongo/customers.json");
		logger.warn("PREREQUISITE: Import to mongo <- resources/import_this_to_mongo/films.json");
		logger.warn("PREREQUISITE: Import to mongo <- resources/import_this_to_mongo/stores.json");

		// application context file that will have database driver and instance
		// details. To see an explanation on the content of this file, refer to
		// application-context(annotated).json
		String applicationContextPath = "resources/application-context.json";

		// Get an instance that is tied to the database
		client = CeresClient.getInstance(applicationContextPath, DATABASE_DRIVER_KEY, DATABASE_INSTANCE_KEY);

		// Load queries and templates into client from file system
		loadFromFileSystem();

		// Load queries and templates into client from database. You can try
		// this by uncommenting the below line
		// loadFromDatabase();

		runBasicExamples();

	}

	private static void runBasicExamples() {

		ExampleBasic exampleBasic = new ExampleBasic(client);

		exampleBasic.run();

	}

	private static void loadFromDatabase() {

		logger.info(
				"This example uses queries from database. To see the demo of loading from file system, call loadFromFileSystem() instead of loadFromDatabase()");

		// JSONObject queryJsonQuery = new JSONObject();
		// queryJsonQuery.put(QueryElements.ELEMENT_TABLE, "queries a");
		// queryJsonQuery.put(QueryElements.ELEMENT_DROP_ALIAS, "true");
		// client.addQueriesFromDatabase(queryJsonQuery);
		//
		// JSONObject templateJsonQuery = new JSONObject();
		// queryJsonQuery.put(QueryElements.ELEMENT_TABLE, "queries a");
		// queryJsonQuery.put(QueryElements.ELEMENT_DROP_ALIAS, "true");
		// client.addTemplatesFromDatabase(templateJsonQuery);

	}

	private static void loadFromFileSystem() {

		logger.info(
				"This example uses queries from database. To see the demo of loading from database, call loadFromDatabase() instead of loadFromFileSystem()");

		// root path where the queries are maintained as .json files. all
		// sub-directories are inclusive
		String queryPath = "resources/queries";

		// root path where the remplates are maintained as .json files. all
		// sub-directories are inclusive
		String templatePath = "resources/templates";

		client.addQueriesFromFilesystem(queryPath);
		client.addTemplatesFromFilesystem(templatePath);

	}

}
