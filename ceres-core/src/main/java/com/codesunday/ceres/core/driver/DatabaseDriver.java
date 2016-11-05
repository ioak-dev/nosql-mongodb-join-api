package com.codesunday.ceres.core.driver;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.json.JSONObject;

import com.codesunday.ceres.core.constants.Constants;
import com.codesunday.ceres.core.domain.ApplicationContext;
import com.codesunday.ceres.core.domain.TransactionContext;
import com.codesunday.ceres.core.exception.CeresException;

public abstract class DatabaseDriver {

	private JSONObject driverProperty;
	protected JSONObject databaseInstanceProperty;

	protected ApplicationContext applicationContext;

	private static final String DRIVER_CLASS_NOT_FOUND = "DRIVER_CLASS_NOT_FOUND";
	private static final String ERROR_INITIALIZING_DATABASE_DRIVER = "ERROR_INITIALIZING_DATABASE_DRIVER";

	/**
	 * Constructor
	 * 
	 * @param driverProperty
	 */
	protected DatabaseDriver(JSONObject driverProperty, JSONObject databaseInstanceProperty,
			ApplicationContext applicationContext) {
		super();
		this.driverProperty = driverProperty;
		this.databaseInstanceProperty = databaseInstanceProperty;
		this.applicationContext = applicationContext;
	}

	/**
	 * Static instance provider
	 * 
	 * @param driverProperty
	 * @param databaseInstanceProperty
	 * @return
	 */
	public static DatabaseDriver getInstance(JSONObject driverProperty, JSONObject databaseInstanceProperty,
			ApplicationContext applicationContext) {
		DatabaseDriver driver = null;
		try {
			driver = (DatabaseDriver) Class.forName(driverProperty.optString(Constants.CLASSPATH))
					.getConstructor(JSONObject.class, JSONObject.class, ApplicationContext.class)
					.newInstance(new Object[] { driverProperty, databaseInstanceProperty, applicationContext });
		} catch (ClassNotFoundException e) {
			throw new CeresException(DRIVER_CLASS_NOT_FOUND, e);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new CeresException(ERROR_INITIALIZING_DATABASE_DRIVER, e);
		}

		return driver;

	}

	public List<JSONObject> find(String table, String alias, TransactionContext transactionContext,
			List<String> conditionlist) {
		return findImpl(table, alias, transactionContext, conditionlist);
	}

	protected abstract List<JSONObject> findImpl(String table, String alias, TransactionContext transactionContext,
			List<String> conditionlist);

}
