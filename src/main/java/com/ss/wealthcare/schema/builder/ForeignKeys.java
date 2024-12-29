package com.ss.wealthcare.schema.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "foreign-keys")
public class ForeignKeys
{
    @XmlElement(name = "foreign-key")
    private List<ForeignKey> foreignKeysList;

    private Map<String, ForeignKey> fkNameVsForeignKey;

    public List<ForeignKey> getForeignKeys()
    {
	return foreignKeysList;
    }

    public void setForeignKeys(List<ForeignKey> foreignKeys)
    {
	this.foreignKeysList = foreignKeys;
    }

    public Map<String, ForeignKey> getFkNameVsForeignKey()
    {
	return fkNameVsForeignKey;
    }

    public void loadMap()
    {
	fkNameVsForeignKey = new HashMap<>();
	for (ForeignKey fk : foreignKeysList)
	{
	    fkNameVsForeignKey.put(fk.getFkName(), fk);
	}

    }

    public void setFkNameVsForeignKey(Map<String, ForeignKey> fkNameVsForeignKey)
    {
	this.fkNameVsForeignKey = fkNameVsForeignKey;
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

	ForeignKeys other = (ForeignKeys) obj;
	return Objects.equals(other.getForeignKeys(), foreignKeysList);
    }

    @Override
    public int hashCode()
    {
	return Objects.hash(foreignKeysList);
    }

    @Override
    public String toString()
    {
	return "\nForeignKeys:\n" + fkNameVsForeignKey.toString() + "\n";
    }

    @XmlRootElement(name = "foreign-key")
    public static class ForeignKey
    {
	@XmlElement(name = "reference")
	private Reference fkReference;
	@XmlElement(name = "key-column")
	private List<String> keyColumn;
	@XmlAttribute(name = "fk-name")
	private String tableFkName;

	public String getFkName()
	{
	    return tableFkName;
	}

	public void setFkName(String fkName)
	{
	    this.tableFkName = fkName;
	}

	public Reference getFKReference()
	{
	    return fkReference;
	}

	public List<String> getKeyColumns()
	{
	    return keyColumn;
	}

	public void setFKReference(Reference fkReference)
	{
	    this.fkReference = fkReference;
	}

	public void setKeyColumns(List<String> keyColumn)
	{
	    this.keyColumn = keyColumn;
	}

	@Override
	public String toString()
	{
	    return "\treference:\n" + fkReference.toString() + "\nfkName: " + tableFkName + "\n\tFKColumns:"
		    + keyColumn.toString() + "\n";
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

	    ForeignKey other = (ForeignKey) obj;

	    return Objects.equals(fkReference, other.getFKReference())
		    && Objects.equals(keyColumn, other.getKeyColumns())
		    && Objects.equals(other.tableFkName, tableFkName);
	}

	@Override
	public int hashCode()
	{
	    return Objects.hash(fkReference, keyColumn, tableFkName);
	}

	@XmlRootElement(name = "reference")
	public static class Reference
	{
	    @XmlAttribute(name = "table")
	    private String table;
	    @XmlElement(name = "key-column")
	    private List<String> keyColumn;

	    public String getReferenceTable()
	    {
		return table;
	    }

	    public List<String> getKeyColumns()
	    {
		return keyColumn;
	    }

	    public void setReferenceTable(String table)
	    {
		this.table = table;
	    }

	    public void setKeyColumns(List<String> keyColumn)
	    {
		this.keyColumn = keyColumn;
	    }

	    @Override
	    public String toString()
	    {
		return "\t\treferenceTable: " + table + "\n\t\treferenceColumns: " + keyColumn.toString();
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

		Reference other = (Reference) obj;

		return Objects.equals(table, other.getReferenceTable())
			&& Objects.equals(keyColumn, other.getKeyColumns());

	    }

	    @Override
	    public int hashCode()
	    {
		return Objects.hash(table, keyColumn);
	    }
	}

    }
}
