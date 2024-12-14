package com.ss.wealthcare.util.dd.operation;

import static com.ss.wealthcare.util.dd.DDUtil.buildQuery;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;

public class CreateOperationUtil
{
    private static final Logger LOGGER = Logger.getLogger(CreateOperationUtil.class.getName());

    public static void createTable(Table table, Connection connection) throws Exception
    {
	// Query Construction
	String tableName = table.getName();
	List<Column> columns = table.getColumns();

	StringBuilder query = new StringBuilder();
	query.append(buildQuery("CREATE TABLE", false, true, false));
	query.append(buildQuery(tableName, false, true, false));
	query.append(buildQuery("(", false, false, true));

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
	    query.append(buildQuery(name, false, true, false));
	    if (dataType == null)
	    {
		throw new Exception("Datatype must not be empty");
	    }
	    query.append(dataType);
	    if (maxSize != null)
	    {
		query.append(buildQuery("(" + maxSize + ")", false, true, false));
	    }
	    else
	    {
		query.append(buildQuery("", false, true, false));
	    }
	    if (nullable != null)
	    {
		query.append(buildQuery(nullable, false, true, false));
	    }
	    if (autoIncrement != null)
	    {
		query.append(buildQuery(autoIncrement, false, true, false));
	    }
	    if (primaryKey != null)
	    {
		query.append(buildQuery(primaryKey, false, true, false));
	    }
	    if (query.charAt(query.length() - 1) != ',')
	    {
		query.append(',');
	    }
	    query.append("\n");
	}
	String revisedQuery = query.substring(0, query.length() - 2);
	revisedQuery = buildQuery(revisedQuery, false, false, true) + ")" + ";";
	LOGGER.log(Level.INFO, "SQL CREATE QUERY: {0}", revisedQuery);

	// Query Execution
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
