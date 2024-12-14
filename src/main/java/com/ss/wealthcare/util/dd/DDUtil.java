package com.ss.wealthcare.util.dd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.conf.YamlParserUtil;
import com.ss.wealthcare.util.dd.operation.AlterOperationUtil;
import com.ss.wealthcare.util.dd.operation.CreateOperationUtil;
import com.ss.wealthcare.util.file.DirectoryUtil;

public class DDUtil
{

    private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());

    private static final Map<String, Object> MYSQL_INFO = YamlParserUtil
	    .loadYamlFile(DirectoryUtil.CONF_DIR + DirectoryUtil.MYSQL + "conf.yaml");

    public static void xmlParser(Table table) throws Exception
    {
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    if (tableExists(table, connection, (String) MYSQL_INFO.get("database")))
	    {
		if (AlterOperationUtil.alterTable(table, connection, (String) MYSQL_INFO.get("database")))
		{
		    return;
		}
	    }
	    else
	    {
		CreateOperationUtil.createTable(table, connection);
	    }
	}
    }

    public static boolean tableExists(Table table, Connection connection, String database) throws SQLException
    {

	String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?";

	PreparedStatement pstmt = connection.prepareStatement(query);
	pstmt.setString(1, database);
	pstmt.setString(2, table.getName());
	try (ResultSet rs = pstmt.executeQuery())
	{
	    if (rs.next())
	    {
		return rs.getInt(1) > 0;
	    }
	}
	return false;
    }

    public static String formatQuery(List<Column> columns, List<String> existingColumns) throws Exception
    {
	StringBuilder query = new StringBuilder();
	for (Column column : columns)
	{
	    String name = column.getName();
	    String dataType = column.getDataType();
	    String maxSize = column.getMaxSize();
	    String nullable = column.getNullable();
	    String autoIncrement = column.getAutoIncrement();
	    String primaryKey = column.getPrimaryKey();

	    if (name == null)
	    {

		throw new Exception("Table name must not be empty");
	    }
	    if (existingColumns != null && existingColumns.contains(name))
	    {
		continue;
	    }
	    query.append(name + ' ');
	    if (dataType == null)
	    {
		throw new Exception("Datatype must not be empty");
	    }
	    query.append(dataType);
	    if (maxSize != null)
	    {
		query.append('(' + maxSize + ')');
	    }
	    if (nullable != null)
	    {
		query.append(nullable + ' ');
	    }
	    if (autoIncrement != null)
	    {
		query.append(autoIncrement + ' ');
	    }
	    if (primaryKey != null)
	    {
		query.append(primaryKey + ' ');
	    }
	    if (query.charAt(query.length() - 1) != ',')
	    {
		query.append(',');
	    }
	    query.append('\n');
	}
	return query.toString();
    }

}
