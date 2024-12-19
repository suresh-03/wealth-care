package com.ss.wealthcare.util.dd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.conf.YamlParserUtil;
import com.ss.wealthcare.util.dd.operation.AlterOperationUtil;
import com.ss.wealthcare.util.dd.operation.CreateOperationUtil;
import com.ss.wealthcare.util.file.DirectoryUtil;

public class DDUtil {

	private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());

	private static final Map<String, Object> MYSQL_INFO = YamlParserUtil
			.loadYamlFile(DirectoryUtil.CONF_DIR + DirectoryUtil.MYSQL + "conf.yaml");

	public static void xmlParser(Table table) throws Exception {
		DDTemplateUtil.createDDTemplate(table);
		try (Connection connection = ConnectionUtil.getConnection()) {

//			Retries Table Attributes If Table Exist / It Is NULL
			List<Column> existTable = tableExists(table.getName(), (String) MYSQL_INFO.get("database"), connection);

//		    If table NOT NULL it Moves To Alter Operation / It Moves To Create Operation
			if (existTable != null) {
				AlterOperationUtil.alterTable(table, connection, existTable);
			} else {
				CreateOperationUtil.createTable(table, connection);
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Exception occured while parsing xml", e);
		}
	}

	public static String formatQuery(Column column, boolean drop) throws Exception {
		StringBuffer query = new StringBuffer();
		if (drop) {
			query.append(column.getName() + ", /n");
			return query.toString();
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

	public static List<Column> tableExists(String tableName, String database, Connection connection)
			throws SQLException {

//		Checking Whether the table is Exist Are Not		
		String query = "SELECT COUNT(*) AS EXIST FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = " + database
				+ " AND TABLE_NAME = " + tableName;
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(query);

		if (!rs.next()) {
			return null;
		}

//		Retrieving Existing Columns and their Attributes from Database
		List<Column> existColumns = new ArrayList<Column>();

		query = "SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, EXTRA FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = "
				+ database + " AND TABLE_NAME = " + tableName;
		rs = statement.executeQuery(query);

		while (rs.next()) {
			Column column = new Column();
			if (rs.getString("COLUMN_NAME") != null) {
				column.setName(rs.getString("COLUMN_NAME"));
			}
			if (rs.getString("COLUMN_TYPE") != null) {
				StringBuffer datatype = new StringBuffer(rs.getString("COLUMN_TYPE"));
				if (datatype.charAt(datatype.length() - 1) == ')') {
					int index = datatype.indexOf("(");
					column.setDataType(datatype.substring(0, index));
					column.setMaxSize(datatype.substring(index + 1, datatype.length() - 1));
				} else {
					column.setDataType(datatype.toString());
				}
			}
			if (rs.getString("IS_NULLABLE") != null) {
				if (rs.getString("IS_NULLABLE").equals("NO")) {
					column.setNullable("NOT NULL");
				}
			}
			if (rs.getString("COLUMN_KEY") != null) {
				if (rs.getString("COLUMN_KEY").equals("PRI")) {
					column.setNullable("PRIMARY KEY");
				}
			}
			if (rs.getString("EXTRA") != null) {
				column.setAutoIncrement("AUTO_INCREMENT");
			}
		}

		return existColumns;
	}

}
