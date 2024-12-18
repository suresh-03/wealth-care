package com.ss.wealthcare.util.dd.operation;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.dd.DDUtil;

public class AlterOperationUtil {

	private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());

	public static boolean alterTable(Table table, Connection connection, String database) throws Exception {
		boolean isAltered = false;
		DatabaseMetaData metaData = connection.getMetaData();
		if (isRenameColumn(table, metaData, connection, database)) {
			isAltered = true;
		}
		if (isNewColumn(table, metaData, connection, database)) {
			isAltered = true;
		}

		return isAltered;
	}

	public static boolean isNewColumn(Table table, DatabaseMetaData metaData, Connection connection, String database)
			throws Exception {

		List<Column> columns = table.getColumns();
		String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + database
				+ "' AND TABLE_NAME = '" + table.getName() + "'";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		int noColumns = 0;
		List<String> existColumn = new ArrayList<>();
		while (rs.next()) {
			existColumn.add(rs.getString("COLUMN_NAME"));
			noColumns++;
		}
		if (noColumns < columns.size()) {

			addColumn(table, connection, database, existColumn);
			return true;
		}
		if (noColumns > columns.size()) {
			removeColumn(table, connection, database, existColumn);
			return true;
		}

		return false;
	}

	public static boolean isRenameColumn(Table table, DatabaseMetaData metaData, Connection connection, String database)
			throws Exception {
		boolean isAltered = false;
		List<Column> columns = table.getColumns();
		String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + database
				+ "' AND TABLE_NAME = '" + table.getName() + "'";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		int i = 0;
		while (rs.next() && i < columns.size()) {
			if (!columns.get(i).getName().equals(rs.getString("COLUMN_NAME"))) {
				String renameQuery = renameColumn(table, rs.getString("COLUMN_NAME"), columns.get(i).getName(),
						columns.get(i).getDataType(), columns.get(i).getMaxSize());
				Statement stmt1 = connection.createStatement();
				stmt1.execute(renameQuery);
				isAltered = true;
			}
			i++;
		}
		return isAltered;
	}

	public static boolean isModifyColumn(Table table, DatabaseMetaData metaData, Connection connection, String database)
			throws SQLException {
		List<Column> columns = table.getColumns();
//		String query = "SELECT COLUMN_NAME, FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + database
//				+ "' AND TABLE_NAME = '" + table.getName() + "'";
//		Statement stmt = connection.createStatement();
//		ResultSet rs = stmt.executeQuery(query);
//		int i = 0;
//		while (rs.next() && i < columns.size()) {
//			}
//			i++;
//		}
		return true;
	}

	public static void addColumn(Table table, Connection connection, String database, List<String> existColumn)
			throws Exception {
		String tableName = table.getName();
		List<Column> columns = table.getColumns();

		StringBuilder query = new StringBuilder();
		query.append("ALTER TABLE ");
		query.append(tableName + '\n');
		query.append(DDUtil.formatQuery(columns, existColumn));

		String revisedQuery = query.substring(0, query.length() - 2);
		revisedQuery = revisedQuery + ';';
		LOGGER.log(Level.INFO, "SQL ALTER QUERY: {0}", revisedQuery);

		try {
			Statement statement = connection.createStatement();
			statement.execute(revisedQuery);
			LOGGER.log(Level.INFO, "{0} Column added successfully!", table.getDisplayName());
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
			throw e;
		}

	}

	public static void removeColumn(Table table, Connection connection, String database, List<String> existColumn)
			throws Exception {
		String tableName = table.getName();
		List<Column> columns = table.getColumns();

		StringBuilder query = new StringBuilder();
		query.append("ALTER TABLE ");
		query.append(tableName + '\n');
		List<String> removeColumn = new ArrayList<>();

		for (Column column : columns) {
			removeColumn.add(column.getName());
		}

		for (String exist : existColumn) {
			if (!removeColumn.contains(exist)) {
				query.append("DROP COLUMN " + exist + ",\n");
			}
		}

		String revisedQuery = query.substring(0, query.length() - 2);
		revisedQuery = revisedQuery + ';';
		LOGGER.log(Level.INFO, "SQL ALTER QUERY: {0}", revisedQuery);

		try {
			Statement statement = connection.createStatement();
			statement.execute(revisedQuery);
			LOGGER.log(Level.INFO, "{0} Column removed successfully!", table.getDisplayName());
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
			throw e;
		}

	}

	public static String renameColumn(Table table, String oldName, String newName, String dataType, String maxsize)
			throws Exception {
		StringBuilder query = new StringBuilder();
		query.append("ALTER TABLE ");
		query.append(table.getName() + "\n");
		query.append("CHANGE " + oldName + " " + newName + " " + dataType);
		if (maxsize != null) {
			query.append("(" + maxsize + ")");
		}
		return query.toString();

	}

}

class Constraints {
	String name;
	String dataType;
	String maxsize;
	String nullable;
	String autoIncrement;
	String primarykey;
}
