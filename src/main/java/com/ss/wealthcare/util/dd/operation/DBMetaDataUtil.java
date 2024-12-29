package com.ss.wealthcare.util.dd.operation;

import static com.ss.wealthcare.util.dd.DDUtil.isNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.ForeignKeys;
import com.ss.wealthcare.schema.builder.ForeignKeys.ForeignKey;
import com.ss.wealthcare.schema.builder.ForeignKeys.ForeignKey.Reference;
import com.ss.wealthcare.schema.builder.PrimaryKey;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.dd.ConnectionUtil;
import com.ss.wealthcare.util.dd.DDUtil;

public class DBMetaDataUtil
{
    private static final Logger LOGGER = Logger.getLogger(DBMetaDataUtil.class.getName());
    private static final String DB = "wealthcare_db";

    private DBMetaDataUtil()
    {
	DDUtil.throwUOE();
    }

    public static Table getTableMetaInfo(String tableName)
    {
	if (isNull(tableName))
	{
	    return null;
	}

	try (Connection connection = ConnectionUtil.getConnection())
	{
	    DatabaseMetaData metaData = connection.getMetaData();
	    ResultSet columns = metaData.getColumns(null, DB, tableName, null);
	    ResultSet primaryKeys = metaData.getPrimaryKeys(null, DB, tableName);
	    ResultSet foreignKeys = metaData.getImportedKeys(null, DB, tableName);
	    List<Column> columnInfo = getColumnInfo(columns, primaryKeys, foreignKeys);
	    if (isNull(columnInfo) || columnInfo.isEmpty())
	    {
		return null;
	    }
	    Table table = new Table();
	    table.setName(tableName);
	    PrimaryKey pk = getPrimaryKeyInfo(primaryKeys);
	    table.setPrimaryKey((isNull(pk.getKeyColumns()) || pk.getKeyColumns().isEmpty()) ? null : pk);
	    ForeignKeys fk = getForeignKeyInfo(foreignKeys);
	    table.setForeignKey((isNull(fk.getForeignKeys()) || fk.getForeignKeys().isEmpty()) ? null : fk);
	    table.setColumns(columnInfo);
	    return table;

	}
	catch (Exception e)
	{
	    LOGGER.log(Level.SEVERE, "Exception occurred when get table meta info", e);
	}
	return null;

    }

    private static List<Column> getColumnInfo(ResultSet columns, ResultSet primaryKeys, ResultSet foreignKeys)
	    throws Exception
    {
	List<Column> columnInfo = new ArrayList<>();

	while (columns.next())
	{
	    String columnName = columns.getString("COLUMN_NAME");
	    String columnType = columns.getString("TYPE_NAME");
	    String columnSize = Integer.toString(columns.getInt("COLUMN_SIZE"));
	    String nullable = columns.getString("IS_NULLABLE");
	    String defaultValue = columns.getString("COLUMN_DEF");
	    String autoIncrement = columns.getString("IS_AUTOINCREMENT");

	    Column column = new Column();

	    column.setName(columnName);
	    column.setDataType(columnType);
	    column.setMaxSize(columnSize);
	    column.setNullable("YES".equals(nullable) ? "NULL" : "NOT NULL");
	    column.setDefaultValue(defaultValue);
	    column.setAutoIncrement("YES".equals(autoIncrement) ? "AUTO_INCREMENT" : "");

	    columnInfo.add(column);
	}

	return columnInfo;
    }

    private static PrimaryKey getPrimaryKeyInfo(ResultSet primaryKeys) throws Exception
    {
	PrimaryKey primaryKeyInfo = new PrimaryKey();
	List<String> pkColumns = new ArrayList<>();
	String pkName = "";

	while (primaryKeys.next())
	{
	    String pkColumnName = primaryKeys.getString("COLUMN_NAME");
	    pkName = primaryKeys.getString("PK_NAME");
	    pkColumns.add(pkColumnName);
	}
	primaryKeyInfo.setPkName(pkName);
	primaryKeyInfo.setKeyColumns(pkColumns);
	return primaryKeyInfo;

    }

    private static ForeignKeys getForeignKeyInfo(ResultSet foreignKeys) throws Exception
    {

	ForeignKeys foreignKeysList = new ForeignKeys();
	Map<String, ForeignKey> fkNameVsKeyColumn = new HashMap<>();

	while (foreignKeys.next())
	{

	    String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
	    String fkName = foreignKeys.getString("FK_NAME");
	    String primaryTable = foreignKeys.getString("PKTABLE_NAME");
	    String primaryColumn = foreignKeys.getString("PKCOLUMN_NAME");

	    if (fkNameVsKeyColumn.containsKey(fkName))
	    {
		ForeignKey fk = fkNameVsKeyColumn.get(fkName);
		Reference reference = fk.getFKReference();
		reference.setReferenceTable(primaryTable);
		reference.getKeyColumns().add(primaryColumn);
		fk.setFKReference(reference);
		fk.getKeyColumns().add(fkColumnName);
	    }
	    else
	    {
		ForeignKey fk = new ForeignKey();
		Reference reference = new Reference();
		reference.setReferenceTable(primaryTable);
		reference.setKeyColumns(new ArrayList<String>()
		{
		    {
			add(primaryColumn);
		    }
		});
		fk.setFkName(fkName);
		fk.setKeyColumns(new ArrayList<String>()
		{
		    {
			add(fkColumnName);
		    }
		});
		fk.setFKReference(reference);

		fkNameVsKeyColumn.put(fkName, fk);
	    }

	}

	foreignKeysList.setFkNameVsForeignKey(fkNameVsKeyColumn);

	return foreignKeysList;
    }

    public static void main(String args[]) throws Exception
    {
	System.out.println(getTableMetaInfo("order_items"));
    }
}
