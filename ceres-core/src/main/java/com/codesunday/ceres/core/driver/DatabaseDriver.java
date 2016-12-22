package com.codesunday.ceres.core.driver;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.codehaus.jackson.node.ObjectNode;

import com.codesunday.ceres.core.constants.Constants;
import com.codesunday.ceres.core.domain.ApplicationContext;
import com.codesunday.ceres.core.domain.TransactionContext;
import com.codesunday.ceres.core.exception.CeresException;

public abstract class DatabaseDriver {

	private ObjectNode driverProperty;
	protected ObjectNode databaseInstanceProperty;

	protected ApplicationContext applicationContext;

	private static final String DRIVER_CLASS_NOT_FOUND = "DRIVER_CLASS_NOT_FOUND";
	private static final String ERROR_INITIALIZING_DATABASE_DRIVER = "ERROR_INITIALIZING_DATABASE_DRIVER";

	/**
	 * Constructor
	 * 
	 * @param driverProperty
	 */
	protected DatabaseDriver(ObjectNode driverProperty, ObjectNode databaseInstanceProperty,
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
	public static DatabaseDriver getInstance(ObjectNode driverProperty, ObjectNode databaseInstanceProperty,
			ApplicationContext applicationContext) {
		DatabaseDriver driver = null;
		try {
			driver = (DatabaseDriver) Class.forName(driverProperty.get(Constants.CLASSPATH).getTextValue())
					.getConstructor(ObjectNode.class, ObjectNode.class, ApplicationContext.class)
					.newInstance(new Object[] { driverProperty, databaseInstanceProperty, applicationContext });
		} catch (ClassNotFoundException e) {
			throw new CeresException(DRIVER_CLASS_NOT_FOUND, e);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new CeresException(ERROR_INITIALIZING_DATABASE_DRIVER, e);
		}

		return driver;

	}

	public List<ObjectNode> find(String table, String alias, TransactionContext transactionContext,
			List<String> conditionlist) {
		return findImpl(table, alias, transactionContext, conditionlist);
	}

	protected abstract List<ObjectNode> findImpl(String table, String alias, TransactionContext transactionContext,
			List<String> conditionlist);

}
