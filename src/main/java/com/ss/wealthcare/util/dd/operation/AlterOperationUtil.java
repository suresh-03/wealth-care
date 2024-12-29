package com.ss.wealthcare.util.dd.operation;

import static com.ss.wealthcare.util.dd.DDUtil.isNull;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.ForeignKeys.ForeignKey;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.util.dd.ConnectionUtil;
import com.ss.wealthcare.util.dd.DDUtil;

public class AlterOperationUtil
{

    private static final Logger LOGGER = Logger.getLogger(AlterOperationUtil.class.getName());

    private AlterOperationUtil()
    {
	DDUtil.throwUOE();
    }

    public static void modifyTable(Table dbTable, Table table) throws Exception
    {
	if (table.isOldNameEquals(dbTable))
	{
	    renameTable(dbTable, table);
	}
	else
	{
	    modifyColumns(dbTable, table);
	}
    }

    private static void modifyColumns(Table dbTable, Table table) throws Exception
    {
	List<Column> dbTableColumns = dbTable.getColumns();
	List<Column> tableColumns = table.getColumns();

	for (int i = 0; i < dbTableColumns.size(); i++)
	{
	    for (int j = 0; j < tableColumns.size(); j++)
	    {

		modifyColumn(table, dbTableColumns.get(i), tableColumns.get(j));

	    }
	}
	checkAndAddColumn(table, dbTableColumns, tableColumns);
	checkAndDropColumn(table, dbTableColumns, tableColumns);
	checkAndModifyPrimaryKey(dbTable, table);
	checkAndModifyForeignKey(dbTable, table);
    }

    private static void checkAndModifyForeignKey(Table dbTable, Table table) throws Exception
    {
	if (isNull(table.getForeignKey()) && !isNull(dbTable.getForeignKey()))
	{
	    dropForeignKey(dbTable, table);
	}
	if (!isNull(table.getForeignKey()) && isNull(dbTable.getForeignKey()))
	{
	    addForeignKey(dbTable, table);
	}
	if (!isNull(table.getForeignKey()) && !isNull(dbTable.getForeignKey()))
	{
	    dropForeignKey(dbTable, table);
	    addForeignKey(dbTable, table);
	}
    }

    private static void addForeignKey(Table dbTable, Table table) throws Exception
    {
	// TODO Auto-generated method stub
	String query = constructAddForeignKeyQuery(dbTable, table);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbTable.setForeignKey(table.getForeignKey());
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);

	}

    }

    private static String constructAddForeignKeyQuery(Table dbTable, Table table)
    {
	// ALTER TABLE child_table
//	ADD CONSTRAINT constraint_name
//	FOREIGN KEY (child_column)
//	REFERENCES parent_table (parent_column)

	StringBuilder sb = new StringBuilder();
	table.getForeignKey().loadMap();
	sb.append("ALTER TABLE").append(' ').append(table.getName()).append(' ');
	for (Map.Entry<String, ForeignKey> map : table.getForeignKey().getFkNameVsForeignKey().entrySet())
	{
	    String fkName = map.getKey();
	    ForeignKey fk = map.getValue();

	    sb.append("ADD CONSTRAINT")
		    .append(' ')
		    .append(fkName)
		    .append(' ')
		    .append("FOREIGN KEY")
		    .append(' ')
		    .append('(');

	    for (String fkColumn : fk.getKeyColumns())
	    {
		sb.append(fkColumn).append(',');
	    }
	    sb.deleteCharAt(sb.length() - 1);
	    sb.append(')')
		    .append(' ')
		    .append("REFERENCES")
		    .append(' ')
		    .append('`')
		    .append(fk.getFKReference().getReferenceTable())
		    .append('`')
		    .append(' ')
		    .append('(');
	    for (String referenceColumn : fk.getFKReference().getKeyColumns())
	    {
		sb.append(referenceColumn).append(',');
	    }
	    sb.deleteCharAt(sb.length() - 1);
	    sb.append(')').append(',');
	}
	sb.deleteCharAt(sb.length() - 1);
	sb.append(';');
	return sb.toString();
    }

    private static void dropForeignKey(Table dbTable, Table table) throws Exception
    {
	// TODO Auto-generated method stub
	String query = constructDropForeignKeyQuery(dbTable, table);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbTable.setForeignKey(table.getForeignKey());
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);

	}

    }

    private static String constructDropForeignKeyQuery(Table dbTable, Table table)
    {
	// ALTER TABLE employees
//	DROP FOREIGN KEY fk_department,
//	DROP FOREIGN KEY fk_manager;

	StringBuilder sb = new StringBuilder();
	sb.append("ALTER TABLE").append(' ').append(table.getName()).append(' ');
	for (ForeignKey fk : table.getForeignKey().getForeignKeys())
	{
	    sb.append("DROP FOREIGN KEY").append(' ').append(fk.getFkName()).append(',');
	}
	sb.deleteCharAt(sb.length() - 1);
	sb.append(';');
	return sb.toString();
    }

    private static void checkAndModifyPrimaryKey(Table dbTable, Table table) throws Exception
    {
	if (isNull(table.getPrimaryKey()) && !isNull(dbTable.getPrimaryKey()))
	{
	    dropPrimaryKey(dbTable, table);
	}
	if (!isNull(table.getPrimaryKey()) && isNull(dbTable.getPrimaryKey()))
	{
	    addPrimaryKey(dbTable, table);
	}
	if (!isNull(table.getPrimaryKey()) && !isNull(dbTable.getPrimaryKey()))
	{
	    modifyPrimaryKey(dbTable, table);
	}

    }

    private static void modifyPrimaryKey(Table dbTable, Table table) throws Exception
    {
	for (String dbTablePk : dbTable.getPrimaryKey().getKeyColumns())
	{
	    if (!table.getPrimaryKey().getKeyColumns().contains(dbTablePk))
	    {
		dropPrimaryKey(dbTable, table);
	    }
	}
	for (String tablePk : table.getPrimaryKey().getKeyColumns())
	{
	    if (!dbTable.getPrimaryKey().getKeyColumns().contains(tablePk))
	    {
		addPrimaryKey(dbTable, table);
	    }
	}
    }

    private static void addPrimaryKey(Table dbTable, Table table) throws Exception
    {
	String query = constructAddPrimaryKeyQuery(dbTable, table);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbTable.setPrimaryKey(table.getPrimaryKey());
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);
	}

    }

    private static String constructAddPrimaryKeyQuery(Table dbTable, Table table)
    {
	// ALTER TABLE employees
//	ADD CONSTRAINT pk_employee_dept PRIMARY KEY (employee_id, department_id);
	StringBuilder sb = new StringBuilder();
	List<String> pkColumns = table.getPrimaryKey().getKeyColumns();
	sb.append("ALTER TABLE")
		.append(' ')
		.append(table.getName())
		.append(' ')
		.append("ADD CONSTRAINT")
		.append(' ')
		.append(table.getPrimaryKey().getPkName())
		.append(' ')
		.append("PRIMARY KEY")
		.append(' ')
		.append('(');
	for (String pkColumn : pkColumns)
	{
	    sb.append(pkColumn).append(',');
	}
	sb.deleteCharAt(sb.length() - 1);
	sb.append(')').append(';');
	return sb.toString();
    }

    private static void dropPrimaryKey(Table dbTable, Table table) throws Exception
    {
	String query = constructDropPrimaryKeyQuery(dbTable);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbTable.setPrimaryKey(table.getPrimaryKey());
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);

	}
    }

    private static String constructDropPrimaryKeyQuery(Table dbTable)
    {
	// ALTER TABLE employees DROP PRIMARY KEY;
	StringBuilder sb = new StringBuilder();
	sb.append("ALTER TABLE")
		.append(' ')
		.append(dbTable.getName())
		.append(' ')
		.append("DROP PRIMARY KEY")
		.append(';');
	return sb.toString();
    }

    private static void checkAndDropColumn(Table table, List<Column> dbTableColumns, List<Column> tableColumns)
	    throws Exception
    {
	for (Column dbColumn : dbTableColumns)
	{
	    boolean equals = false;
	    Column col = dbColumn;
	    for (Column column : tableColumns)
	    {
		if (column.equals(dbColumn))
		{
		    equals = true;
		    break;
		}
	    }
	    if (!equals)
	    {
		dropColumn(table, dbTableColumns, col);
	    }
	}
    }

    private static void dropColumn(Table table, List<Column> dbTableColumns, Column column) throws Exception
    {
	String query = constructDropColumnQuery(table, column);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbTableColumns.remove(column);
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);

	}
    }

    private static String constructDropColumnQuery(Table table, Column column)
    {
	// ALTER TABLE employees DROP COLUMN phone_number;
	StringBuilder sb = new StringBuilder();
	sb.append("ALTER TABLE")
		.append(' ')
		.append(table.getName())
		.append(' ')
		.append("DROP COLUMN")
		.append(' ')
		.append(column.getName())
		.append(';');
	return sb.toString();
    }

    private static void checkAndAddColumn(Table table, List<Column> dbTableColumns, List<Column> tableColumns)
	    throws Exception
    {
	for (Column column : tableColumns)
	{
	    boolean equals = false;
	    Column col = column;
	    for (Column dbColumn : dbTableColumns)
	    {
		if (column.equals(dbColumn))
		{
		    equals = true;
		    break;
		}
	    }
	    if (!equals)
	    {
		addColumn(table, dbTableColumns, col);
	    }
	}
    }

    private static void addColumn(Table table, List<Column> dbTableColumns, Column column) throws Exception
    {
	// TODO Auto-generated method stub
	String query = constructAddColumnQuery(table, column);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    Column dbColumn = new Column();
	    dbColumn.setName(column.getName());
	    dbColumn.setAutoIncrement(column.getAutoIncrement());
	    dbColumn.setDataType(column.getDataType());
	    dbColumn.setMaxSize(column.getMaxSize());
	    dbColumn.setNullable(column.getNullable());
	    dbColumn.setDefaultValue(column.getDefaultValue());
	    dbTableColumns.add(dbColumn);
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);
	}

    }

    private static String constructAddColumnQuery(Table table, Column column)
    {
	// ALTER TABLE employees ADD department_id INT NOT NULL DEFAULT 1;
	StringBuilder sb = new StringBuilder();
	sb.append("ALTER TABLE")
		.append(' ')
		.append(table.getName())
		.append(' ')
		.append("ADD")
		.append(' ')
		.append(column.getName())
		.append(' ')
		.append(isNull(column.getMaxSize()) ? column.getDataType()
			: column.getDataType() + '(' + column.getMaxSize() + ')')
		.append(' ')
		.append(isNull(column.getNullable()) ? "" : column.getNullable())
		.append(isNull(column.getAutoIncrement()) ? "" : column.getAutoIncrement())
		.append(' ');
	if (!isNull(column.getDefaultValue()))
	{
	    sb.append("DEFAULT").append(' ').append('\'').append(column.getDefaultValue()).append('\'');
	}
	sb.append(';');
	return sb.toString();

    }

    private static void modifyColumn(Table table, Column dbColumn, Column column) throws Exception
    {
	if (dbColumn.equals(column))
	{
	    return;
	}
	if (column.isOldNameEquals(dbColumn))
	{
	    renameColumn(table, dbColumn, column);
	}
	if (isNull(column.getDefaultValue()) && !isNull(dbColumn.getDefaultValue()))
	{

	    checkAndDropDefaultValueOfColumn(table, dbColumn, column);

	}
	if (column.isNameEquals(dbColumn))
	{
	    modifyAllColumnProperties(table, dbColumn, column);
	}

    }

    private static void checkAndDropDefaultValueOfColumn(Table table, Column dbColumn, Column column) throws Exception
    {
	String query = constructDropDefaultValueOfColumnQuery(table, column);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbColumn.setDefaultValue(column.getDefaultValue());
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);

	}

    }

    private static String constructDropDefaultValueOfColumnQuery(Table table, Column column)
    {
	// ALTER TABLE table_name ALTER COLUMN column_name DROP DEFAULT;
	StringBuilder sb = new StringBuilder();
	sb.append("ALTER TABLE")
		.append(' ')
		.append(table.getName())
		.append(' ')
		.append("ALTER COLUMN")
		.append(' ')
		.append(column.getName())
		.append(' ')
		.append("DROP DEFAULT")
		.append(';');

	return sb.toString();
    }

    private static void modifyAllColumnProperties(Table table, Column dbColumn, Column column) throws Exception
    {
	String query = constructQueryForModifyAllColumnProperties(table, column);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbColumn.setName(column.getName());
	    dbColumn.setAutoIncrement(column.getAutoIncrement());
	    dbColumn.setDataType(column.getDataType());
	    dbColumn.setMaxSize(column.getMaxSize());
	    dbColumn.setNullable(column.getNullable());
	    dbColumn.setDefaultValue(column.getDefaultValue());
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);
	}
    }

    private static String constructQueryForModifyAllColumnProperties(Table table, Column column)
    {
	// ALTER TABLE employees MODIFY email VARCHAR(100) NOT NULL DEFAULT
	// 'example@example.com';
	StringBuilder sb = new StringBuilder();

	sb.append("ALTER TABLE")
		.append(' ')
		.append(table.getName())
		.append(' ')
		.append("MODIFY")
		.append(' ')
		.append(column.getName())
		.append(' ')
		.append(isNull(column.getMaxSize()) ? column.getDataType()
			: column.getDataType() + '(' + column.getMaxSize() + ')')
		.append(' ')
		.append(isNull(column.getAutoIncrement()) ? "" : column.getAutoIncrement())
		.append(isNull(column.getNullable()) ? "" : column.getNullable())
		.append(' ');
	if (!isNull(column.getDefaultValue()))
	{
	    sb.append("DEFAULT").append(' ').append('\'').append(column.getDefaultValue()).append('\'');
	}
	sb.append(';');
	return sb.toString();
    }

    private static void renameColumn(Table table, Column dbColumn, Column column) throws Exception
    {
	// TODO Auto-generated method stub
	String query = constructRenameColumnQuery(table, column);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbColumn.setName(column.getName());
	    dbColumn.setDataType(column.getDataType());
	    dbColumn.setMaxSize(column.getMaxSize());
	    dbColumn.setNullable(column.getNullable());
	    dbColumn.setDefaultValue(column.getDefaultValue());
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);

	}

    }

    private static String constructRenameColumnQuery(Table table, Column column)
    {
	// ALTER TABLE employees CHANGE full_name name VARCHAR(100) NOT NULL DEFAULT
	// 'BYE';
	StringBuilder sb = new StringBuilder();
	sb.append("ALTER TABLE")
		.append(' ')
		.append(table.getName())
		.append(' ')
		.append("CHANGE")
		.append(' ')
		.append(column.getOldName())
		.append(' ')
		.append(column.getName())
		.append(' ')
		.append(isNull(column.getMaxSize()) ? column.getDataType()
			: column.getDataType() + '(' + column.getMaxSize() + ')')
		.append(' ')
		.append(isNull(column.getAutoIncrement()) ? "" : column.getAutoIncrement())
		.append(isNull(column.getNullable()) ? "" : column.getNullable())
		.append(' ');
	if (!isNull(column.getDefaultValue()))
	{
	    sb.append("DEFAULT").append(' ').append('\'').append(column.getDefaultValue()).append('\'');
	}
	sb.append(';');
	return sb.toString();
    }

    private static void renameTable(Table dbTable, Table table) throws Exception
    {
	// TODO Auto-generated method stub
	String query = constructRenameTableQuery(table);
	try (Connection connection = ConnectionUtil.getConnection())
	{
	    connection.createStatement().execute(query);
	    dbTable.setName(table.getName());
	    LOGGER.log(Level.INFO, "{0} is executed successfully", query);
	}

    }

    private static String constructRenameTableQuery(Table table)
    {
	// ALTER TABLE old_table_name RENAME TO new_table_name;
	StringBuilder sb = new StringBuilder();

	sb.append("ALTER TABLE")
		.append(' ')
		.append(table.getOldName())
		.append(' ')
		.append("RENAME TO")
		.append(' ')
		.append(table.getName())
		.append(';');

	return sb.toString();
    }

}
