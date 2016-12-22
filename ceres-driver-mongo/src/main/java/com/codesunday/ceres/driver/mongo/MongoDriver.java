package com.codesunday.ceres.driver.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

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

	private static ObjectMapper mapper = new ObjectMapper();

	private MongoClient client;
	private MongoDatabase db;

	private static final String ERROR_INITIALIZING_MONGO_DRIVER = "ERROR_INITIALIZING_MONGO_DRIVER";

	private Logger logger = Logger.getLogger(MongoDriver.class);

	public MongoDriver(ObjectNode driverProperty, ObjectNode databaseInstanceProperty,
			ApplicationContext applicationContext) {

		super(driverProperty, databaseInstanceProperty, applicationContext);

		try {

			String dbName = databaseInstanceProperty.get(Constants.DATABASE).getTextValue();

			String uri = databaseInstanceProperty.get(Constants.URI).getTextValue();

			this.client = new MongoClient(new MongoClientURI(uri));

			this.db = client.getDatabase(dbName);

		} catch (Exception e) {
			throw new CeresException(ERROR_INITIALIZING_MONGO_DRIVER, e);
		}

	}

	@Override
	protected List<ObjectNode> findImpl(String table, String alias, TransactionContext transactionContext,
			List<String> conditionlist) {

		long startTime = System.currentTimeMillis();

		List<ObjectNode> returnList = new ArrayList();

		BasicDBObject query = new BasicDBObject();

		if (conditionlist != null) {

			for (String condition : conditionlist) {
				constructQuery(query, condition, alias);
			}

		}

		FindIterable<Document> documents = db.getCollection(table).find(query);

		for (Document document : documents) {
			ObjectNode json = mapper.createObjectNode();
			try {
				json.put(alias, mapper.readTree(document.toJson()));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
