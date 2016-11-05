package com.codesunday.ceres.driver.mongo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.codesunday.ceres.core.constants.Operators;
import com.codesunday.ceres.core.constants.QueryElements;
import com.codesunday.ceres.core.domain.ApplicationContext;
import com.codesunday.ceres.core.domain.TransactionContext;
import com.codesunday.ceres.core.driver.DatabaseDriver;
import com.codesunday.ceres.core.exception.CeresException;
import com.codesunday.ceres.core.utils.TextUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class MongoDriver extends DatabaseDriver {

	private MongoClient client;
	private MongoDatabase db;

	private static final String ERROR_INITIALIZING_MONGO_DRIVER = "ERROR_INITIALIZING_MONGO_DRIVER";

	private Logger logger = Logger.getLogger(MongoDriver.class);

	public MongoDriver(JSONObject driverProperty, JSONObject databaseInstanceProperty,
			ApplicationContext applicationContext) {

		super(driverProperty, databaseInstanceProperty, applicationContext);

		try {

			String dbName = databaseInstanceProperty.optString(Constants.DATABASE);

			String uri = databaseInstanceProperty.optString(Constants.URI);

			this.client = new MongoClient(new MongoClientURI(uri));

			this.db = client.getDatabase(dbName);

		} catch (Exception e) {
			throw new CeresException(ERROR_INITIALIZING_MONGO_DRIVER, e);
		}

	}

	@Override
	protected List<JSONObject> findImpl(String table, String alias, TransactionContext transactionContext,
			List<String> conditionlist) {

		long startTime = System.currentTimeMillis();

		List<JSONObject> returnList = new ArrayList();

		BasicDBObject query = new BasicDBObject();

		if (conditionlist != null) {

			for (String condition : conditionlist) {
				constructQuery(query, condition, alias);
			}

		}

		FindIterable<Document> documents = db.getCollection(table).find(query);

		for (Document document : documents) {
			JSONObject json = new JSONObject();
			try {
				json.put(alias, new JSONObject(document.toJson()));
			} catch (JSONException e) {
				e.printStackTrace();
			}

			returnList.add(json);
		}

		applicationContext.logCapsule.logTime(this.getClass(), transactionContext, startTime, returnList.size(),
				table + query.toJson());

		return returnList;
	}

	private void constructQuery(BasicDBObject query, String condition, String alias) {

		if (condition.contains(Operators.NOT_EQUAL)) {

			String[] parts = condition.split(Operators.NOT_EQUAL);

			query.append(parts[0].substring(alias.length() + 1, parts[0].length()),
					new BasicDBObject(MongoOperator.NOT_IN, TextUtils.stringToList(parts[1], QueryElements.COMMA)));

		} else if (condition.contains(Operators.EQUAL)) {

			String[] parts = condition.split(Operators.EQUAL);

			query.append(parts[0].substring(alias.length() + 1, parts[0].length()),
					new BasicDBObject(MongoOperator.IN, TextUtils.stringToList(parts[1], QueryElements.COMMA)));

		} else if (condition.contains(Operators.NOT_IN)) {

			String[] parts = condition.split(Operators.NOT_IN);

			query.append(parts[0].substring(alias.length() + 1, parts[0].length()),
					new BasicDBObject(MongoOperator.NOT_IN, TextUtils.stringToList(parts[1], QueryElements.COMMA)));

		} else if (condition.contains(Operators.IN)) {

			String[] parts = condition.split(Operators.IN);

			query.append(parts[0].substring(alias.length() + 1, parts[0].length()),
					new BasicDBObject(MongoOperator.IN, TextUtils.stringToList(parts[1], QueryElements.COMMA)));

		}

	}

}
