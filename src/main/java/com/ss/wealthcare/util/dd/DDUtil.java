package com.ss.wealthcare.util.dd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class DDUtil
{

    private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());

    private static final Map<String, Object> MYSQL_INFO = YamlParserUtil
	    .loadYamlFile(DirectoryUtil.CONF_DIR + DirectoryUtil.MYSQL + "conf.yaml");

    private DDUtil()
    {
	throwUOE();
    }

    public static class SQLWrapper
    {
	private Connection connection;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private int result;

	public SQLWrapper(Connection connection, PreparedStatement pstmt, ResultSet rs, int result)
	{
	    this.connection = connection;
	    this.pstmt = pstmt;
	    this.rs = rs;
	    this.result = result;
	}

	public ResultSet getResultSet()
	{
	    return rs;
	}

	public int getIntResult()
	{
	    return result;
	}

	public static void closeAll(SQLWrapper wrapper) throws Exception
	{
	    if (wrapper == null)
	    {
		return;
	    }
	    if (checkNotNull(wrapper.connection))
	    {
		wrapper.connection.close();
	    }
	    if (checkNotNull(wrapper.pstmt))
	    {
		wrapper.pstmt.close();
	    }
	    if (checkNotNull(wrapper.rs))
	    {
		wrapper.rs.close();
	    }
	}
    }

    public static void xmlParser(Table table) throws Exception
    {
	DDTemplateUtil.createDDTemplate(table);
	try (Connection connection = ConnectionUtil.getConnection())
	{

//			Retries Table Attributes If Table Exist / It Is NULL
	    List<Column> existTable = tableExists(table.getName(), (String) MYSQL_INFO.get("database"));

//		    If table NOT NULL it Moves To Alter Operation / It Moves To Create Operation
	    if (checkNotNull(existTable))
	    {
		AlterOperationUtil.alterTable(table, existTable, (String) MYSQL_INFO.get("database"));
	    }
	    else
	    {
		CreateOperationUtil.createTable(table);
	    }
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.INFO, "Exception occured while parsing xml", e);
	}
    }

    public static String formatQuery(Column column, boolean drop) throws Exception
    {
	StringBuffer query = new StringBuffer();
	if (drop)
	{
	    query.append(column.getName() + ",\n");
	    return query.toString();
	}
	if (!checkNotNull(column.getName()))
	{
	    throw new Exception("Table name must not be empty");
	}
	query.append(column.getName() + ' ');
	if (!checkNotNull(column.getDataType()))
	{
	    throw new Exception("Datatype must not be empty");
	}
	query.append(column.getDataType());
	if (checkNotNull(column.getMaxSize()))
	{
	    query.append('(' + column.getMaxSize() + ')' + ' ');
	}
	else
	{
	    query.append(' ');
	}
	if (checkNotNull(column.getNullable()))
	{
	    query.append(column.getNullable() + ' ');
	}
	if (checkNotNull(column.getAutoIncrement()))
	{
	    query.append(column.getAutoIncrement() + ' ');
	}
	if (checkNotNull(column.getPrimaryKey()))
	{
	    query.append(column.getPrimaryKey() + ' ');
	}
	if (query.charAt(query.length() - 1) != ',')
	{
	    query.append(',');
	}
	query.append('\n');

	return query.toString();

    }

    public static List<Column> tableExists(String tableName, String database) throws Exception
    {

//		Checking Whether the table is Exist Are Not
	String query = "SELECT COUNT(*) AS EXIST FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + database
		+ "' AND TABLE_NAME = '" + tableName + "'";
	SQLWrapper wrapper = null;
	try
	{
	    wrapper = executeQuery(query);
	    ResultSet rs = wrapper.getResultSet();
	    if (rs.next())
	    {
		if (!(rs.getInt("EXIST") > 0))
		{
		    return null;
		}
	    }
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.SEVERE, "Exception occurred while executing query", e);
	}
	finally
	{
	    SQLWrapper.closeAll(wrapper);
	}

//		Retrieving Existing Columns and their Attributes from Database
	List<Column> existColumns = new ArrayList<Column>();

	query = "SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, EXTRA FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '"
		+ database + "' AND TABLE_NAME = '" + tableName + "'";

	try
	{
	    wrapper = executeQuery(query);
	    ResultSet rs = wrapper.getResultSet();
	    while (rs.next())
	    {
		Column column = new Column();
		if (checkNotNull(rs.getString("COLUMN_NAME")))
		{
		    column.setName(rs.getString("COLUMN_NAME"));
		}
		if (checkNotNull(rs.getString("COLUMN_TYPE")))
		{
		    StringBuffer datatype = new StringBuffer(rs.getString("COLUMN_TYPE"));
		    if (datatype.charAt(datatype.length() - 1) == ')')
		    {
			int index = datatype.indexOf("(");
			column.setDataType(datatype.substring(0, index));
			column.setMaxSize(datatype.substring(index + 1, datatype.length() - 1));
		    }
		    else
		    {
			column.setDataType(datatype.toString());
		    }
		}
		if (checkNotNull(rs.getString("IS_NULLABLE")))
		{
		    if (rs.getString("IS_NULLABLE").equals("NO"))
		    {
			column.setNullable("NOT NULL");
		    }
		}
		if (checkNotNull(rs.getString("COLUMN_KEY")))
		{
		    if (rs.getString("COLUMN_KEY").equals("PRI"))
		    {
			column.setPrimaryKey("PRIMARY KEY");
		    }
		}
		if (checkNotNull(rs.getString("EXTRA")))
		{
		    column.setAutoIncrement("AUTO_INCREMENT");
		}

		existColumns.add(column);
	    }

	}
	catch (Exception e)
	{
	    LOGGER.log(Level.SEVERE, "Exception occurred while executing query", e);
	}
	finally
	{
	    SQLWrapper.closeAll(wrapper);
	}

	return existColumns;
    }

    public static SQLWrapper executeQuery(String query) throws Exception
    {
	Connection connection = ConnectionUtil.getConnection();
	PreparedStatement pstmt = connection.prepareStatement(query);

	ResultSet rs = null;
	int result = -1;
	if (query.startsWith("SELECT"))
	{
	    rs = pstmt.executeQuery();
	}
	else
	{
	    result = pstmt.executeUpdate();
	}

	return new SQLWrapper(connection, pstmt, rs, result);

    }

    public static boolean checkNotNull(Object obj)
    {
	return obj != null;
    }

    public static void throwUOE() throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException("Util class can't be instantiated");
    }

}
