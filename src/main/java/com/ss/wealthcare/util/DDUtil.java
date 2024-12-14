package com.ss.wealthcare.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.AlterUtil;

public class DDUtil {
	
    private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());
    
    private static final Map<String, Object> MYSQL_INFO = YamlParserUtil
    	    .loadYamlFile(DirectoryUtil.CONF_DIR + DirectoryUtil.MYSQL + "conf.yaml");
	
    public static void xmlParser(Table table)throws Exception
    {
    	try(Connection connection = ConnectionUtil.getConnection()){
    		String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?";
    		PreparedStatement pstmt = connection.prepareStatement(query);
    		Statement statement = connection.createStatement();
    		pstmt.setString(1,(String) MYSQL_INFO.get("database"));
    		pstmt.setString(2,table.getName());
    		
    		ResultSet rs = pstmt.executeQuery();
    		if(rs.next()) {
    			if(rs.getInt(1) > 0) {
    				AlterUtil.alterTable(table,connection,(String) MYSQL_INFO.get("database"));
    			}
    			else {
    				
    				createTable(table,connection);
    			}
    		}
    	}
    }
    
    
    
    public static void createTable(Table table,Connection connection) throws Exception
    {
    	//Query Construction
    	String tableName = table.getName();
    	List<Column> columns = table.getColumns();

    	StringBuilder query = new StringBuilder();
    	query.append(comQuery("CREATE TABLE", false,true,false));
    	query.append(comQuery(tableName, false,true,false));
    	query.append(comQuery("(", false,false,true));
    	
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
    	    query.append(comQuery(name, false,true,false));
    	    if (dataType == null)
    	    {
    		throw new Exception("Datatype must nost be empty");
    	    }
    	    query.append(dataType);
    	    if (maxSize != null)
    	    {
    	    	query.append(comQuery("(" + maxSize + ")", false,true,false));
    	    }
    	    else
    	    {
    		query.append(comQuery("",false,true,false));
    	    }
    	    if (nullable != null)
    	    {
    		query.append(comQuery(nullable,false,true,false));
    	    }
    	    if (autoIncrement != null)
    	    {
    	    query.append(comQuery(autoIncrement,false,true,false));
    	    }
    	    if (primaryKey != null)
    	    {
        	query.append(comQuery(primaryKey,false,true,false));
    	    }
    	    if (query.charAt(query.length() - 1) != ',')
    	    {
    		query.append(',');
    	    }
    	    query.append("\n");
    	}
    	String revisedQuery = query.substring(0, query.length() - 2);
    	revisedQuery = comQuery(revisedQuery,false,false,true)+")"+";";
    	LOGGER.log(Level.INFO, "SQL CREATE QUERY: {0}", revisedQuery);
    	
    	//Query Execution
		try{
			Statement statement = connection.createStatement();
		    statement.execute(revisedQuery);
		    LOGGER.log(Level.INFO, "{0} table created successfully!", table.getDisplayName());
		}catch (Exception e){
		    LOGGER.log(Level.INFO, "Exception occurred while creating table", e);
		    throw e;
		}

    }
    
    public static String comQuery(String field , boolean comma , boolean space , boolean newLine) {
    	StringBuilder str = new StringBuilder();
    	str.append(field);
    	if(space) {
    		str.append(" ");
    	}
    	if(comma) {
    		str.append(", ");
    	}
    	if(newLine) {
    		str.append("\n");
    	}
    	return str.toString();    	
    }

}
