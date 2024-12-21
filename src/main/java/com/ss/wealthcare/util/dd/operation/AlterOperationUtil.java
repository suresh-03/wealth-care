package com.ss.wealthcare.util.dd.operation;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.dd.DDUtil;

public class AlterOperationUtil {

	private static final Logger LOGGER = Logger.getLogger(AlterOperationUtil.class.getName());

	public static void alterTable(Table table, Connection connection, List<Column> existColumns, String database)
			throws Exception {

		List<Column> newColumns = new ArrayList<Column>(table.getColumns());
		List<String> newColumnName = new ArrayList<String>();
		List<Column> removedColumns = new ArrayList<Column>();
		List<Column> addedColumns = new ArrayList<Column>(table.getColumns());
		List<String> existColumnName = new ArrayList<String>();

		for (Column column : existColumns)
			existColumnName.add(column.getName());

		for (Column column : newColumns)
			newColumnName.add(column.getName());

		int size = addedColumns.size();

		for (int i = 0; i < size; i++) {
			if (i < existColumns.size() && newColumns.get(i).getName().equals(existColumns.get(i).getName())) {
				if (constraintChecker(newColumns.get(i), existColumns.get(i))) {
					modifyConstraint(table.getName(), newColumns.get(i), connection);
				}
				addedColumns.remove(addedColumns.indexOf(newColumns.get(i)));
			} else if (i < existColumns.size() && newColumns.get(i).getOldName() != null
					&& newColumns.get(i).getOldName().equals(existColumns.get(i).getName())) {
				rename(table.getName(), newColumns.get(i), connection);
				if (constraintChecker(newColumns.get(i), existColumns.get(i))) {
					modifyConstraint(table.getName(), newColumns.get(i), connection);
				}
				addedColumns.remove(addedColumns.indexOf(newColumns.get(i)));
			} else if (i < existColumns.size() && existColumnName.contains(newColumns.get(i).getName())) {
				changeOrder(table.getName(), connection,
						existColumns.get(existColumnName.indexOf(newColumns.get(i).getName())),
						i == 0 ? "FIRST" : existColumnName.get(i - 1));
				existColumns = DDUtil.tableExists(table.getName(), database, connection);
				if (constraintChecker(newColumns.get(i), existColumns.get(i))) {
					modifyConstraint(table.getName(), newColumns.get(i), connection);
				}
				addedColumns.remove(addedColumns.indexOf(newColumns.get(i)));

			} else if (i < existColumns.size() && newColumns.get(i).getOldName() != null
					&& existColumnName.contains(newColumns.get(i).getOldName())) {
				changeOrder(table.getName(), connection,
						existColumns.get(existColumnName.indexOf(newColumns.get(i).getOldName())),
						i == 0 ? "FIRST" : existColumnName.get(i - 1));
				rename(table.getName(), newColumns.get(i), connection);
				existColumns = DDUtil.tableExists(table.getName(), database, connection);
				if (constraintChecker(newColumns.get(i), existColumns.get(i))) {
					modifyConstraint(table.getName(), newColumns.get(i), connection);
				}
				addedColumns.remove(addedColumns.indexOf(newColumns.get(i)));
			}
		}

		if (!addedColumns.isEmpty()) {
			addColumn(table.getName(), addedColumns, connection);
		}

		for (String column : existColumnName) {
			if (!newColumnName.contains(column)) {
				removedColumns.add(existColumns.get(existColumnName.indexOf(column)));
			}
		}
		if (!removedColumns.isEmpty()) {
			removeColumn(table.getName(), removedColumns, connection);
		}

	}

	public static boolean constraintChecker(Column added, Column exist) {
		if (added.getDataType() != null && exist.getDataType() != null) {
			if (!added.getDataType().equalsIgnoreCase(exist.getDataType()))
				return true;
		}
		if (added.getMaxSize() != null && exist.getMaxSize() != null) {
			if (!added.getMaxSize().equalsIgnoreCase(exist.getMaxSize()))
				return true;
		}
		if (added.getNullable() != null && exist.getNullable() != null) {
			if (!added.getNullable().equalsIgnoreCase(exist.getNullable()))
				return true;
		}
		if (added.getAutoIncrement() != null && exist.getAutoIncrement() != null) {
			if (!added.getAutoIncrement().equalsIgnoreCase(exist.getAutoIncrement()))
				return true;
		}
		if (added.getPrimaryKey() != null && exist.getPrimaryKey() != null) {
			if (!added.getPrimaryKey().equalsIgnoreCase(exist.getPrimaryKey()))
				return true;
		}

		return false;
	}

	public static void modifyConstraint(String tableName, Column column, Connection connection) throws Exception {
		StringBuffer query = new StringBuffer();
		query.append("ALTER TABLE " + tableName + " \nMODIFY COLUMN ");
		column.setPrimaryKey(null);
		query.append(DDUtil.formatQuery(column, false));

		String revisedQuery = query.substring(0, query.length() - 2);
		try {
			Statement statement = connection.createStatement();
			System.out.println(revisedQuery);
			statement.execute(revisedQuery);
			LOGGER.log(Level.INFO, "{0} Column Modified successfully!", tableName);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred while Modifying Column", e);
			throw e;
		}

	}

	public static void rename(String tableName, Column column, Connection connection) throws Exception {
		StringBuffer query = new StringBuffer();
		query.append("ALTER TABLE " + tableName + " \nCHANGE ");
		query.append(column.getOldName() + ' ' + column.getName() + " " + column.getDataType());
		if (column.getMaxSize() != null) {
			query.append('(' + column.getMaxSize() + ')');
		}

		String revisedQuery = query.toString();
		try {
			Statement statement = connection.createStatement();
			statement.execute(revisedQuery);
			LOGGER.log(Level.INFO, "{0} Column successfully!", tableName);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred while Renaming Column", e);
			throw e;
		}
	}

	public static void changeOrder(String tableName, Connection connection, Column column, String Previous)
			throws Exception {
		StringBuffer query = new StringBuffer();
		query.append("ALTER TABLE " + tableName + " \nCHANGE COLUMN ");
		query.append(column.getName() + ' ' + column.getName() + ' ' + column.getDataType().toUpperCase());
		if (column.getMaxSize() != null) {
			query.append('(' + column.getMaxSize() + ')');
		}
		query.append(" AFTER " + Previous);

		String revisedQuery = query.toString();
		System.out.println(revisedQuery);
		try {
			Statement statement = connection.createStatement();
			statement.execute(revisedQuery);
			LOGGER.log(Level.INFO, "{0} Column order Changed successfully!", tableName);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred while Changing Column order", e);
			throw e;
		}
	}

	public static void addColumn(String tableName, List<Column> columns, Connection connection) throws Exception {
		// Query Construction
		StringBuilder query = new StringBuilder();
		query.append("ALTER TABLE " + tableName + '\n');
		for (Column column : columns) {
			query.append("ADD COLUMN ");
			query.append(DDUtil.formatQuery(column, false));
		}
		String revisedQuery = query.substring(0, query.length() - 3);

		LOGGER.log(Level.INFO, "SQL ADD QUERY: {0}", revisedQuery);

		// Query Execution
		try {
			Statement statement = connection.createStatement();
			statement.execute(revisedQuery);
			LOGGER.log(Level.INFO, "{0} Column Added successfully!", tableName);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred while Adding Column", e);
			throw e;
		}

	}

	public static void removeColumn(String tableName, List<Column> columns, Connection connection) throws Exception {
		// Query Construction
		StringBuilder query = new StringBuilder();
		query.append("ALTER TABLE " + tableName + '\n');

		for (Column column : columns) {
			query.append("DROP COLUMN ");
			query.append(DDUtil.formatQuery(column, true));
		}

		String revisedQuery = query.substring(0, query.length() - 2);

		LOGGER.log(Level.INFO, "SQL ADD QUERY: {0}", revisedQuery);

		// Query Execution
		try {
			Statement statement = connection.createStatement();
			statement.execute(revisedQuery);
			LOGGER.log(Level.INFO, "{0} Column Removed successfully!", tableName);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred while Removing Column", e);
			throw e;
		}

	}

}
