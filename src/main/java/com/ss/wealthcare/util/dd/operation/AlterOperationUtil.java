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

public class AlterOperationUtil
{

    private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());

    public static boolean alterTable(Table table, Connection connection, String database) throws Exception
    {
	boolean isAltered = false;
	DatabaseMetaData metaData = connection.getMetaData();
	if (isRenameColumn(table, metaData))
	{
	    isAltered = true;
	}
	if (isNewColumn(table, metaData, connection, database))
	{
	    isAltered = true;
	}

	return isAltered;
    }

    public static boolean isNewColumn(Table table, DatabaseMetaData metaData, Connection connection, String database)
	    throws Exception
    {
	List<Column> columns = table.getColumns();
	ResultSet rs = metaData.getColumns(null, null, table.getName(), null);
	if (rs.getRow() < columns.size())
	{
	    List<String> existColumn = new ArrayList<>();
	    while (rs.next())
	    {
		existColumn.add(rs.getString("COLUMN_NAME"));
	    }
	    addColumn(table, connection, database, existColumn);
	    return true;
	}
	if (rs.getRow() > columns.size())
	{
	    List<String> existColumn = new ArrayList<>();
	    while (rs.next())
	    {
		existColumn.add(rs.getString(1));
	    }
	    removeColumn(table, connection, database, existColumn);
	    return true;
	}

	return false;
    }

    public static boolean isRenameColumn(Table table, DatabaseMetaData metaData) throws SQLException
    {
	boolean isAltered = false;
	List<Column> columns = table.getColumns();
	ResultSet rs = metaData.getColumns(null, null, table.getName(), null);
	int i = 0;
	while (rs.next())
	{
	    if (rs.getString(1) != columns.get(i).getName())
	    {

	    }
	}
	return false;
    }

    public static boolean isModifyColumn()
    {
	return true;
    }

    public static void addColumn(Table table, Connection connection, String database, List<String> existColumn)
	    throws Exception
    {
	String tableName = table.getName();
	List<Column> columns = table.getColumns();

	StringBuilder query = new StringBuilder();
	query.append("ALTER TABLE ");
	query.append(tableName + '\n');
	query.append(DDUtil.formatQuery(columns, existColumn));

	String revisedQuery = query.substring(0, query.length() - 2);
	revisedQuery = revisedQuery + ';';
	LOGGER.log(Level.INFO, "SQL ALTER QUERY: {0}", revisedQuery);

	try
	{
	    Statement statement = connection.createStatement();
	    statement.execute(revisedQuery);
	    LOGGER.log(Level.INFO, "{0} Column added successfully!", table.getDisplayName());
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
	    throw e;
	}

    }

    public static void removeColumn(Table table, Connection connection, String database, List<String> existColumn)
	    throws Exception
    {
	String tableName = table.getName();
	List<Column> columns = table.getColumns();

	StringBuilder query = new StringBuilder();
	query.append("ALTER TABLE ");
	query.append(tableName + '\n');

	for (int i = existColumn.size() - 1; i > columns.size() - 1; i--)
	{

	    query.append("DROP COLUMN ");
	    query.append(existColumn.get(i));

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
	    LOGGER.log(Level.INFO, "{0}  column removed successfully!", table.getDisplayName());
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
	    throw e;
	}

    }

    public static void renameColumn(Table table, Connection connection, String database) throws Exception
    {

    }

}
