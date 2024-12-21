package com.ss.wealthcare.util.dd.operation;

import static com.ss.wealthcare.util.dd.DDUtil.SQLWrapper.closeAll;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.dd.DDUtil;
import com.ss.wealthcare.util.dd.DDUtil.SQLWrapper;

public class CreateOperationUtil
{
    private static final Logger LOGGER = Logger.getLogger(CreateOperationUtil.class.getName());
    private static SQLWrapper wrapper;

    private CreateOperationUtil()
    {
	DDUtil.throwUOE();

    }

    public static void createTable(Table table) throws Exception
    {
	// Query Construction
	String tableName = table.getName();
	List<Column> columns = table.getColumns();

	StringBuilder query = new StringBuilder();
	query.append("CREATE TABLE" + ' ');
	query.append(tableName + ' ');
	query.append("(\n");

	for (Column column : columns)
	{
	    query.append(DDUtil.formatQuery(column, false));
	}

	String revisedQuery = query.substring(0, query.length() - 2);
	revisedQuery = revisedQuery + "\n);";
	LOGGER.log(Level.INFO, "SQL CREATE QUERY: {0}", revisedQuery);

	// Query Execution
	try
	{
	    wrapper = DDUtil.executeQuery(revisedQuery);
	    LOGGER.log(Level.INFO, "{0} table created successfully!", table.getDisplayName());
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
	    throw e;
	}
	finally
	{
	    closeAll(wrapper);
	}

    }
}
