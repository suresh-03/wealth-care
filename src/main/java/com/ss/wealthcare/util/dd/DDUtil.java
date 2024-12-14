package com.ss.wealthcare.util.dd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.logging.Logger;

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
	    String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?";
	    PreparedStatement pstmt = connection.prepareStatement(query);
	    pstmt.setString(1, (String) MYSQL_INFO.get("database"));
	    pstmt.setString(2, table.getName());

	    ResultSet rs = pstmt.executeQuery();
	    if (rs.next())
	    {
		if (rs.getInt(1) > 0)
		{
		    AlterOperationUtil.alterTable(table, connection, (String) MYSQL_INFO.get("database"));
		}
		else
		{

		    CreateOperationUtil.createTable(table, connection);
		}
	    }
	}
    }

    public static String buildQuery(String field, boolean comma, boolean space, boolean newLine)
    {
	StringBuilder str = new StringBuilder();
	str.append(field);
	if (space)
	{
	    str.append(' ');
	}
	if (comma)
	{
	    str.append(',');
	}
	if (newLine)
	{
	    str.append('\n');
	}
	return str.toString();
    }

}
