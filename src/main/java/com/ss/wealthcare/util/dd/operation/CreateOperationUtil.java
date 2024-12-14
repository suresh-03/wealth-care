package com.ss.wealthcare.util.dd.operation;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.dd.DDUtil;

public class CreateOperationUtil
{
    private static final Logger LOGGER = Logger.getLogger(CreateOperationUtil.class.getName());

    public static void createTable(Table table, Connection connection) throws Exception
    {
	// Query Construction
	String tableName = table.getName();
	List<Column> columns = table.getColumns();

	StringBuilder query = new StringBuilder();
	query.append("CREATE TABLE" + '\t');
	query.append(tableName + '\t');
	query.append("(\n");
	query.append(DDUtil.formatQuery(columns));

	String revisedQuery = query.substring(0, query.length() - 2);
	revisedQuery += revisedQuery + "\n);";
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
