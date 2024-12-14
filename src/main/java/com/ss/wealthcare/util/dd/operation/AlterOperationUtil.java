package com.ss.wealthcare.util.dd.operation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.dd.DDUtil;

public class AlterOperationUtil
{

    private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());

    public static void alterTable(Table table, Connection connection, String database) throws Exception
    {
	String tableName = table.getName();
	List<Column> columns = table.getColumns();

	StringBuilder query = new StringBuilder();
	query.append(DDUtil.buildQuery("ALTER TABLE", false, true, false));
	query.append(DDUtil.buildQuery(tableName, false, false, true));
	// Checking which Column Exist
	List<String> existColumn = new ArrayList<>();

	String column_query = "SELECT COLUMN_NAME FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";
	PreparedStatement pstmt = connection.prepareStatement(column_query);
	pstmt.setString(1, database);

	ResultSet rs = pstmt.executeQuery();
	while (rs.next())
	{
	    existColumn.add(rs.getString("COLUMN_NAME"));
	}

	for (Column column : columns)
	{
	    String name = column.getName();
	    if (existColumn.contains(name))
	    {
		continue;
	    }
	    String dataType = column.getDataType();
	    String maxSize = column.getMaxSize();
	    String nullable = column.getNullable();
	    String autoIncrement = column.getAutoIncrement();
	    String primaryKey = column.getPrimaryKey();

	    if (name == null)
	    {

		throw new Exception("Table name must not be empty");
	    }
	    query.append(DDUtil.buildQuery("ADD COLUMN", false, true, false) + DDUtil.buildQuery(name, false, true, false));
	    if (dataType == null)
	    {
		throw new Exception("Datatype must not be empty");
	    }
	    query.append(dataType);
	    if (maxSize != null)
	    {
		query.append(DDUtil.buildQuery("(" + maxSize + ")", false, true, false));
	    }
	    else
	    {
		query.append(" ");
	    }
	    if (nullable != null)
	    {
		query.append(DDUtil.buildQuery(nullable, false, true, false));
	    }
	    if (autoIncrement != null)
	    {
		query.append(DDUtil.buildQuery(autoIncrement, false, true, false));
	    }
	    if (primaryKey != null)
	    {
		query.append(DDUtil.buildQuery(primaryKey, false, true, false));
	    }
	    if (query.charAt(query.length() - 1) != ',')
	    {
		query.append(",");
	    }
	    query.append("\n");
	}
	String revisedQuery = query.substring(0, query.length() - 2);
	revisedQuery = revisedQuery + ';';
	LOGGER.log(Level.INFO, "SQL ALTER QUERY: {0}", revisedQuery);

	try
	{
	    Statement statement = connection.createStatement();
	    statement.execute(revisedQuery);
	    LOGGER.log(Level.INFO, "{0} table created successfully!", table.getDisplayName());
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
	    throw e;
	}

    }

}
