package com.codesunday.ceres.core.logging;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.codesunday.ceres.core.domain.TransactionContext;

public class LogCapsule {

	private static ObjectMapper mapper = new ObjectMapper();

	private static final String CONTEXT = "context";
	private static final String ID = "id";
	private static final String ACTION = "action";
	private static final String UUID = "uuid";
	private static final String RECORDS = "records";
	private static final String START_TIME = "start_time";
	private static final String TIME = "response_time";

	private boolean isJsonFormat = false;

	private boolean logToDatabase = false;

	private List<ObjectNode> databaseBuffer;

	public LogCapsule(boolean isJsonFormat) {
		super();
		this.isJsonFormat = isJsonFormat;
	}

	public void logTime(Class clazz, TransactionContext transactionContext, long startTime, int records,
			String action) {

		String logText = null;

		if (isJsonFormat) {
			ObjectNode log = computeTimeJson(transactionContext.context, transactionContext.id, transactionContext.uuid,
					action, records, startTime);
			logText = log.toString();
		} else {
			logText = computeTimeText(transactionContext.context, transactionContext.id, transactionContext.uuid,
					action, records, startTime);
		}

		Logger.getLogger(clazz).info(logText);
	}

	private ObjectNode computeTimeJson(String context, String id, UUID uuid, String action, int records,
			long startTime) {

		ObjectNode json = mapper.createObjectNode();

		json.put(CONTEXT, context);
		json.put(ID, id);
		json.put(ACTION, action);
		json.put(UUID, uuid.toString());
		json.put(RECORDS, records);

		long endTime = System.currentTimeMillis();

		json.put(START_TIME, startTime);

		long responseTime = endTime - startTime;
		if (responseTime < 1000) {
			json.put(TIME, responseTime + "ms");
		} else {
			json.put(TIME, responseTime / 1000 + "s");
		}

		return json;

	}

	private String computeTimeText(String context, String id, UUID uuid, String action, int records, long startTime) {

		StringBuilder sb = new StringBuilder();

		long endTime = System.currentTimeMillis();

		sb.append("[");
		sb.append(context);
		sb.append("/");
		sb.append(id);
		// sb.append("/");
		// sb.append(uuid);
		sb.append("] ");
		sb.append(records);
		sb.append(" records in ");
		long responseTime = endTime - startTime;
		if (responseTime < 1000) {
			sb.append(responseTime);
			sb.append("ms");
		} else {
			sb.append((responseTime) / 1000);
			sb.append("s");
		}

		sb.append(" - ");
		sb.append(action);

		return sb.toString();

	}

}
