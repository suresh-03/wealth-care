package com.ss.wealthcare.util.dd.operation;

import static com.ss.wealthcare.util.dd.DDUtil.isNull;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.ForeignKeys;
import com.ss.wealthcare.schema.builder.ForeignKeys.ForeignKey;
import com.ss.wealthcare.schema.builder.PrimaryKey;
import com.ss.wealthcare.schema.builder.Table;
import com.ss.wealthcare.schema.builder.UniqueKeys;
import com.ss.wealthcare.schema.builder.UniqueKeys.UniqueKey;
import com.ss.wealthcare.util.dd.DDUtil;

public class CreateOperationUtil
{
    private static final Logger LOGGER = Logger.getLogger(CreateOperationUtil.class.getName());

    private CreateOperationUtil()
    {
	DDUtil.throwUOE();

    }

    public static void createTable(Table table) throws Exception
    {
	// TODO Auto-generated method stub
	String query = constructCreateQuery(table);
	DDUtil.executeDDLQuery(query);
    }

    private static String constructCreateQuery(Table table)
    {
	StringBuilder sb = new StringBuilder();

	sb.append("CREATE TABLE").append(' ').append(table.getName()).append(' ').append('(').append('\n');

	for (Column column : table.getColumns())
	{
	    String name = column.getName();
	    String dataType = column.getDataType();
	    String maxSize = column.getMaxSize();
	    String autoIncrement = column.getAutoIncrement();
	    String defaultValue = column.getDefaultValue();
	    String nullable = column.getNullable();

	    if (isNull(name) || isNull(dataType))
	    {
		LOGGER.log(Level.INFO, "column name and datatype is mandatory!");
		return "";
	    }
	    sb.append(name).append(' ').append(dataType);
	    if (!isNull(maxSize))
	    {
		sb.append('(').append(maxSize).append(')');
	    }
	    sb.append(' ');
	    if (!isNull(autoIncrement))
	    {
		sb.append(autoIncrement).append(' ');
	    }
	    if (!isNull(nullable))
	    {
		sb.append(nullable).append(' ');
	    }
	    if (!isNull(defaultValue))
	    {
		sb.append(defaultValue).append(' ');
	    }
	    sb.append(',');
	    sb.append('\n');
	}
	PrimaryKey primaryKey = table.getPrimaryKey();
	ForeignKeys foreignKey = table.getForeignKey();
	UniqueKeys uniqueKey = table.getUniqueKeys();
	if (isNull(primaryKey) && isNull(foreignKey) && isNull(uniqueKey))
	{
	    sb.delete(sb.length() - 2, sb.length());
	}

	if (!isNull(primaryKey))
	{

	    sb.append("PRIMARY KEY").append(' ').append('(');
	    for (String keyColumn : primaryKey.getKeyColumns())
	    {
		sb.append(keyColumn).append(',');
	    }
	    sb.deleteCharAt(sb.length() - 1);
	    sb.append(')');
	}
	if (!isNull(foreignKey))
	{
	    sb.append(',').append('\n');
	    for (Map.Entry<String, ForeignKey> map : foreignKey.getFkNameVsForeignKey().entrySet())
	    {
		String fkName = map.getKey();
		ForeignKey fk = map.getValue();

		sb.append("CONSTRAINT")
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
			.append('(');

		for (String referenceColumn : fk.getFKReference().getKeyColumns())
		{
		    sb.append(referenceColumn).append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(')').append(',').append('\n');

	    }
	}
	if (!isNull(uniqueKey))
	{
	    sb.append(',').append('\n');
	    for (Map.Entry<String, UniqueKey> map : uniqueKey.getUkNameVsUniqueKey().entrySet())
	    {
		String ukName = map.getKey();
		UniqueKey uk = map.getValue();
		// CONSTRAINT unique_email UNIQUE (email),
		sb.append("CONSTRAINT").append(' ').append(ukName).append(' ').append("UNIQUE").append(' ').append('(');

		for (String keyColumn : uk.getKeyColumns())
		{
		    sb.append(keyColumn).append(',');
		}

		sb.deleteCharAt(sb.length() - 1);
		sb.append(')').append(',').append('\n');

	    }
	}
	if (!isNull(foreignKey) || !isNull(uniqueKey))
	{
	    sb.delete(sb.length() - 2, sb.length());
	}
	sb.append('\n').append(')').append(';');

	return sb.toString();
    }

}
