package com.codesunday.ceres.core.logging;

import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.codesunday.ceres.core.domain.TransactionContext;

public class LogCapsule {

	private static final String CONTEXT = "context";
	private static final String ID = "id";
	private static final String ACTION = "action";
	private static final String UUID = "uuid";
	private static final String RECORDS = "records";
	private static final String START_TIME = "start_time";
	private static final String TIME = "response_time";

	private boolean isJsonFormat = false;

	private boolean logToDatabase = false;

	private List<JSONObject> databaseBuffer;

	public LogCapsule(boolean isJsonFormat) {
		super();
		this.isJsonFormat = isJsonFormat;
	}

	public void logTime(Class clazz, TransactionContext transactionContext, long startTime, int records,
			String action) {

		String logText = null;

		try {

			if (isJsonFormat) {
				JSONObject log = computeTimeJson(transactionContext.context, transactionContext.id,
						transactionContext.uuid, action, records, startTime);
				logText = log.toString(3);
			} else {
				logText = computeTimeText(transactionContext.context, transactionContext.id, transactionContext.uuid,
						action, records, startTime);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		Logger.getLogger(clazz).info(logText);
	}

	private JSONObject computeTimeJson(String context, String id, UUID uuid, String action, int records,
			long startTime) {

		JSONObject json = new JSONObject();

		try {

			json.put(CONTEXT, context);
			json.put(ID, id);
			json.put(ACTION, action);
			json.put(UUID, uuid);
			json.put(RECORDS, records);

			long endTime = System.currentTimeMillis();

			json.put(START_TIME, startTime);

			long responseTime = endTime - startTime;
			if (responseTime < 1000) {
				json.put(TIME, responseTime + "ms");
			} else {
				json.put(TIME, responseTime / 1000 + "s");
			}

		} catch (JSONException e) {
			e.printStackTrace();
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
