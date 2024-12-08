package com.ss.wealthcare.util;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;

public class DDUtil
{
    public static final String CREATE_TABLE = "CREATE TABLE";
    public static final char OPEN_BRACE = '(';
    public static final char CLOSE_BRACE = ')';
    public static final char SEMI_COLON = ';';
    public static final char COMMA = ',';
    public static final char SPACE = ' ';
    public static final String NEWLINE = "\n";

    private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());

    private static String constructCreateQuery(Table table) throws Exception
    {
	String tableName = table.getName();
	List<Column> columns = table.getColumns();

	StringBuilder query = new StringBuilder();
	query.append(CREATE_TABLE + SPACE + tableName + SPACE + OPEN_BRACE + NEWLINE);

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
	    query.append(name + SPACE);
	    if (dataType == null)
	    {
		throw new Exception("Datatype must not be empty");
	    }
	    query.append(dataType);
	    if (maxSize != null)
	    {
		query.append(OPEN_BRACE + maxSize + CLOSE_BRACE + SPACE);
	    }
	    else
	    {
		query.append(SPACE);
	    }
	    if (nullable != null)
	    {
		query.append(nullable + SPACE);
	    }
	    if (autoIncrement != null)
	    {
		query.append(autoIncrement + SPACE);
	    }
	    if (primaryKey != null)
	    {
		query.append(primaryKey + SPACE);
	    }
	    if (query.charAt(query.length() - 1) != COMMA)
	    {
		query.append(COMMA);
	    }
	    query.append(NEWLINE);
	}

	String revisedQuery = query.substring(0, query.length() - 2);
	revisedQuery = revisedQuery + NEWLINE + CLOSE_BRACE + SEMI_COLON;
	LOGGER.log(Level.INFO, "SQL CREATE QUERY: {0}", revisedQuery);
	return revisedQuery;

    }

    public static void createTable(Table table) throws Exception
    {
	String query = constructCreateQuery(table);

	try (Connection connection = ConnectionUtil.getConnection(); Statement statement = connection.createStatement())
	{
	    statement.execute(query);
	    LOGGER.log(Level.INFO, "{0} table created successfully!", table.getDisplayName());
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
	    throw e;
	}

    }

}
