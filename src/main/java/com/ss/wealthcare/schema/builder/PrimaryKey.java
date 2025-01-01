package com.ss.wealthcare.schema.builder;

import java.util.List;
import java.util.Objects;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "primary-key")
public class PrimaryKey
{
    @XmlElement(name = "key-column")
    private List<String> keyColumn;

    public List<String> getKeyColumns()
    {
	return keyColumn;
    }

    public void setKeyColumns(List<String> keyColumn)
    {
	this.keyColumn = keyColumn;
    }

    @Override
    public String toString()
    {
	return "\n\tPKColumns: " + keyColumn.toString();
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
	PrimaryKey other = (PrimaryKey) obj;

	return Objects.equals(keyColumn, other.getKeyColumns());
    }

    @Override
    public int hashCode()
    {
	return Objects.hash(keyColumn);
    }

}
