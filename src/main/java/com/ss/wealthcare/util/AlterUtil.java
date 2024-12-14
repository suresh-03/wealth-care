package com.ss.wealthcare.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;

public class AlterUtil {
	
	private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());
	
	public static boolean alter(Table table,Connection connection,String database) throws Exception {
		boolean isAltered = false;
		DatabaseMetaData metaData = connection.getMetaData();
		if(isRenameColumn(table,metaData)) {
			isAltered = true;
		}
		if(isNewColumn(table,metaData,connection,database)) {
			isAltered = true;
		}
		
		return isAltered;
	}
	public static boolean isNewColumn(Table table,DatabaseMetaData metaData,Connection connection,String database) throws Exception {
		List<Column> columns = table.getColumns(); 
		ResultSet rs = metaData.getColumns(null, null, table.getName(), null);
		if(rs.getRow()<columns.size()) {
			List<String> existColumn = new ArrayList<>();
			while(rs.next()) {
				existColumn.add(rs.getString("COLUMN_NAME"));
			}
			addColumn(table, connection, database,existColumn);
			return true;
		}
		else if(rs.getRow()>columns.size()) {
			List<String> existColumn = new ArrayList<>();
			while(rs.next()) {
				existColumn.add(rs.getString(1));
			}
			removeColumn(table, connection, database,existColumn);
			return true;
		}
		
		return false;
	}
	
	public static boolean isRenameColumn(Table table,DatabaseMetaData metaData) throws SQLException {
		boolean isAltered = false;
		List<Column> columns = table.getColumns(); 
		ResultSet rs = metaData.getColumns(null, null, table.getName(), null);
		int i = 0;
		while(rs.next()) {
			if(rs.getString(1) != columns.get(i).getName()){
				
			}
		}
		return false;
	}
	
	public static boolean isModifyColumn() {
		return true;
	}
	
	public static void addColumn(Table table ,Connection connection,String database,List<String> existColumn) throws Exception
    {
    	String tableName = table.getName();
    	List<Column> columns = table.getColumns();

    	StringBuilder query = new StringBuilder();
    	query.append(DDUtil.formatQuery("ALTER TABLE",false,true,false));
    	query.append(DDUtil.formatQuery(tableName,false,false,true));
    	
		for (Column column : columns)
		{
		    String name = column.getName();
		    if(existColumn.contains(name)) continue;
		    String dataType = column.getDataType();
		    String maxSize = column.getMaxSize();
		    String nullable = column.getNullable();
		    String autoIncrement = column.getAutoIncrement();
		    String primaryKey = column.getPrimaryKey();

		    if (name == null)
		    {

			throw new Exception("Table name must not be empty");
		    }
		    query.append(DDUtil.formatQuery("ADD COLUMN",false,true,false)+DDUtil.formatQuery(name,false,true,false));
		    if (dataType == null)
		    {
			throw new Exception("Datatype must not be empty");
		    }
		    query.append(dataType);
		    if (maxSize != null)
		    {
			query.append(DDUtil.formatQuery("(" + maxSize + ")",false,true,false));
		    }
		    else
		    {
			query.append(" ");
		    }
		    if (nullable != null)
		    {
			query.append(DDUtil.formatQuery(nullable, false, true, false));
		    }
		    if (autoIncrement != null)
		    {
			query.append(DDUtil.formatQuery(autoIncrement, false, true, false));
		    }
		    if (primaryKey != null)
		    {
			query.append(DDUtil.formatQuery(primaryKey, false, true, false));
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
    	    LOGGER.log(Level.INFO, "{0} Column added successfully!", table.getDisplayName());
    	}
    	catch (Exception e)
    	{
    	    LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
    	    throw e;
    	}
    	
    }
	
	public static void removeColumn(Table table ,Connection connection,String database,List<String> existColumn) throws Exception
    {
    	String tableName = table.getName();
    	List<Column> columns = table.getColumns();

    	StringBuilder query = new StringBuilder();
    	query.append(DDUtil.formatQuery("ALTER TABLE",false,true,false));
    	query.append(DDUtil.formatQuery(tableName,false,false,true));
    	
		for (int i=existColumn.size()-1;i>columns.size()-1;i--){
			
		    query.append(DDUtil.formatQuery("DROP COLUMN",false,true,false)+DDUtil.formatQuery(existColumn.get(i),false,true,false));
		    
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
	
	public static void renameColumn(Table table ,Connection connection,String database) throws Exception
    {
		
    }
    
	

}
