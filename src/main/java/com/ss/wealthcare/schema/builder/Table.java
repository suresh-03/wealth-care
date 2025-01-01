package com.ss.wealthcare.schema.builder;

import java.util.List;
import java.util.Objects;

import com.ss.wealthcare.util.dd.DDUtil;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "table")
public class Table
{
    @XmlAttribute(name = "name")
    private String tableName;
    @XmlAttribute(name = "old-name")
    private String tableOldName;
    @XmlAttribute(name = "display-name")
    private String tableDisplayName;
    @XmlElement(name = "column")
    private List<Column> tableColumns;
    @XmlElement(name = "primary-key")
    private PrimaryKey tablePrimaryKey;
    @XmlElement(name = "foreign-keys")
    private ForeignKeys tableForeignKeys;
    @XmlElement(name = "unique-keys")
    private UniqueKeys tableUniqueKeys;
    @XmlAttribute(name = "drop-table")
    private boolean isDropTable;

    private Table typeCast(Object obj)
    {
	return (Table) obj;
    }

    public String getName()
    {
	return tableName;
    }

    public void setName(String name)
    {
	this.tableName = name;
    }

    public String getOldName()
    {
	return tableOldName;
    }

    public void setOldName(String oldName)
    {
	this.tableOldName = oldName;
    }

    public String getDisplayName()
    {
	return tableDisplayName;
    }

    public List<Column> getColumns()
    {
	return tableColumns;
    }

    public boolean getDropTable()
    {
	return isDropTable;
    }

    public void setDropTable(boolean dropTable)
    {
	this.isDropTable = dropTable;
    }

    public void setColumns(List<Column> columns)
    {
	this.tableColumns = columns;
    }

    public PrimaryKey getPrimaryKey()
    {
	return tablePrimaryKey;
    }

    public void setPrimaryKey(PrimaryKey primaryKey)
    {
	this.tablePrimaryKey = primaryKey;
    }

    public ForeignKeys getForeignKey()
    {
	return tableForeignKeys;
    }

    public void setForeignKey(ForeignKeys foreignKeys)
    {
	this.tableForeignKeys = foreignKeys;
    }

    public UniqueKeys getUniqueKeys()
    {
	return tableUniqueKeys;
    }

    public void setUniqueKeys(UniqueKeys uniqueKeys)
    {
	this.tableUniqueKeys = uniqueKeys;
    }

    @Override
    public String toString()
    {
	return "\ntableName: " + tableName + "\noldName: " + tableOldName + "\ncolumns:\n"
		+ (DDUtil.isNull(tableColumns) ? "" : tableColumns.toString()) + "\nprimaryKey:\n"
		+ (DDUtil.isNull(tablePrimaryKey) ? "" : tablePrimaryKey.toString()) + "\nforeignKey:\n"
		+ (DDUtil.isNull(tableForeignKeys) ? "" : tableForeignKeys.toString()) + "\nuniqueKey:\n"
		+ (DDUtil.isNull(tableUniqueKeys) ? "" : tableUniqueKeys.toString());
    }

    public boolean isNameEquals(Object obj)
    {

	return Objects.equals(typeCast(obj).getName(), tableName);
    }

    public boolean isOldNameEquals(Object obj)
    {
	Table other = (Table) obj;
	return Objects.equals(typeCast(obj).getName(), tableOldName);
    }

    @Override
    public boolean equals(Object obj)
    {
	if (this == obj)
	{
	    return true;
	}
	if (obj == null || getClass() != obj.getClass())
	{
	    return false;
	}
	Table other = (Table) obj;

	return Objects.equals(tableColumns, other.getColumns()) && isNameEquals(other)
		&& Objects.equals(tablePrimaryKey, other.getPrimaryKey())
		&& Objects.equals(tableForeignKeys, other.getForeignKey())
		&& Objects.equals(tableUniqueKeys, other.getUniqueKeys());
    }

    @Override
    public int hashCode()
    {
	return Objects.hash(tableName, tableColumns, tablePrimaryKey, tableForeignKeys, tableUniqueKeys);
    }

    public boolean isPrimaryKeyEquals(Object obj)
    {
	return Objects.equals(typeCast(obj).getPrimaryKey(), tablePrimaryKey);

    }
}
