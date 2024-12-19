package com.ss.wealthcare.util.dd.operation;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;

public class AlterOperationUtil {

	private static final Logger LOGGER = Logger.getLogger(AlterOperationUtil.class.getName());

	public static void alterTable(Table table, Connection connection, List<Column> existColums) throws Exception {

		List<String> removedColumns = new ArrayList<String>();
		List<Column> addedColumns = table.getColumns();
		List<String> existColumnName = new ArrayList<String>();
		for (Column column : existColums)
			existColumnName.add(column.getName());

		boolean remove = addedColumns.size() < existColums.size() ? true : false;

		int last = addedColumns.size() < existColums.size() ? addedColumns.size() : existColums.size();

		for (int i = 0; i < last; i++) {
			if (addedColumns.get(i).getName().equals(existColums.get(i).getName())) {
				if (!constraintChecker(addedColumns.get(i), existColums.get(i))) {
					modifyConstraint(table.getName(), addedColumns.get(i), connection);
				}
				addedColumns.remove(i);
			} else if (addedColumns.get(i).getOldName().equals(existColums.get(i).getName())) {
				rename(table.getName(), addedColumns.get(i), connection);
				if (!constraintChecker(addedColumns.get(i), existColums.get(i))) {
					modifyConstraint(table.getName(), addedColumns.get(i), connection);
				}
				addedColumns.remove(i);
			} else if (existColumnName.contains(addedColumns.get(i).getName())) {
//				changeOrder();
				if (!constraintChecker(addedColumns.get(i), existColums.get(i))) {
					modifyConstraint(table.getName(), addedColumns.get(i), connection);
				}
				addedColumns.remove(i);
			} else if (existColumnName.contains(addedColumns.get(i).getOldName())) {
//				changeOrder();
				rename(table.getName(), addedColumns.get(i), connection);
				if (!constraintChecker(addedColumns.get(i), existColums.get(i))) {
					modifyConstraint(table.getName(), addedColumns.get(i), connection);
				}
				addedColumns.remove(i);
			}
		}

		if (!addedColumns.isEmpty()) {
//			addColumns(table.getName(),addedColumns);
		}

		if (remove) {
			for (int i = last; i < existColums.size(); i++) {
				removedColumns.add(existColums.get(i).getName());
			}
//			removeColumns(table.getName(),removedColumns);
		}

	}

	public static boolean constraintChecker(Column added, Column exist) {
		if (!added.getDataType().equals(exist.getDataType()))
			return false;
		if (!added.getMaxSize().equals(exist.getMaxSize()))
			return false;
		if (!added.getNullable().equals(exist.getNullable()))
			return false;
		if (!added.getAutoIncrement().equals(exist.getAutoIncrement()))
			return false;
		if (!added.getPrimaryKey().equals(exist.getPrimaryKey()))
			return false;

		return true;
	}

	public static void modifyConstraint(String tableName, Column column, Connection connection) throws Exception {
		StringBuffer query = new StringBuffer();
		query.append("ALTER TABLE " + tableName + " \nMODIFY COLUMN ");
		query.append(formatQuery(column, false));

		String revisedQuery = query.substring(0, query.length() - 2);
		try {
			Statement statement = connection.createStatement();
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
		query.append(formatQuery(column, false));

		String revisedQuery = query.substring(0, query.length() - 2);
		try {
			Statement statement = connection.createStatement();
			statement.execute(revisedQuery);
			LOGGER.log(Level.INFO, "{0} Column successfully!", tableName);
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred while Renaming Column", e);
			throw e;
		}

	}

	public static String formatQuery(Column column, boolean drop) throws Exception {
		StringBuffer query = new StringBuffer();
		if (drop) {
			query.append(column.getName() + ", /n");
			return query.toString();
		}
		if (column.getOldName() != null) {
			query.append(column.getOldName() + ' ');
		}
		if (column.getName() == null) {
			throw new Exception("Table name must not be empty");
		}
		query.append(column.getName() + ' ');
		if (column.getDataType() == null) {
			throw new Exception("Datatype must not be empty");
		}
		query.append(column.getDataType());
		if (column.getMaxSize() != null) {
			query.append('(' + column.getMaxSize() + ')' + ' ');
		} else {
			query.append(' ');
		}
		if (column.getNullable() != null) {
			query.append(column.getNullable() + ' ');
		}
		if (column.getAutoIncrement() != null) {
			query.append(column.getAutoIncrement() + ' ');
		}
		if (column.getPrimaryKey() != null) {
			query.append(column.getPrimaryKey() + ' ');
		}
		if (query.charAt(query.length() - 1) != ',') {
			query.append(',');
		}
		query.append('\n');

		return query.toString();

	}

}
