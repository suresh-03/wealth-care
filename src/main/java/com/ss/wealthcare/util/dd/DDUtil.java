package com.ss.wealthcare.util.dd;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.dd.operation.AlterOperationUtil;
import com.ss.wealthcare.util.dd.operation.CreateOperationUtil;
import com.ss.wealthcare.util.dd.operation.DBMetaDataUtil;

public class DDUtil
{

    private static final Logger LOGGER = Logger.getLogger(DDUtil.class.getName());

    private DDUtil()
    {
	throwUOE();
    }

    public static void xmlParser(Table table) throws Exception
    {
	try
	{
	    if (isNull(table))
	    {
		LOGGER.log(Level.INFO, "Table is null");
		return;
	    }
	    DDTemplateUtil.createDDTemplate(table);
	    Table currentTable = DBMetaDataUtil.getTableMetaInfo(table.getName());
	    Table oldTable = DBMetaDataUtil.getTableMetaInfo(table.getOldName());

	    if (isNull(currentTable) && isNull(oldTable))
	    {
		CreateOperationUtil.createTable(table);

	    }
	    else if (isNull(currentTable) && !isNull(oldTable))
	    {
		AlterOperationUtil.modifyTable(oldTable, table);
	    }
	    else
	    {
		AlterOperationUtil.modifyTable(currentTable, table);
	    }
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.SEVERE, "Exception occured while building schema", e);
	}

    }

    public static boolean isNull(Object obj)
    {
	return obj == null;
    }

    public static void throwUOE() throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException("Util class can't be instantiated");
    }

    public static Table getDBTable(String tableName)
    {
	return isNull(tableName) || tableName.isEmpty() ? null : DBMetaDataUtil.getTableMetaInfo(tableName);
    }

    public static void verifyDBStructure(Table dbTable, Table table)
    {

    }

}
