package com.ss.wealthcare.schema.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "unique-keys")
public class UniqueKeys
{
    @XmlElement(name = "unique-key")
    private List<UniqueKey> uniqueKeysList;

    private Map<String, UniqueKey> ukNameVsUniqueKey;

    public List<UniqueKey> getUniqueKeys()
    {
	return uniqueKeysList;
    }

    public void setUniqueKeys(List<UniqueKey> uniqueKeys)
    {
	this.uniqueKeysList = uniqueKeys;
    }

    public Map<String, UniqueKey> getUkNameVsUniqueKey()
    {
	return ukNameVsUniqueKey;
    }

    public void loadMap()
    {
	ukNameVsUniqueKey = new HashMap<>();
	for (UniqueKey uk : uniqueKeysList)
	{
	    ukNameVsUniqueKey.put(uk.getUkName(), uk);
	}

    }

    public void setUkNameVsUniqueKey(Map<String, UniqueKey> ukNameVsUniqueKey)
    {
	this.ukNameVsUniqueKey = ukNameVsUniqueKey;
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

	UniqueKeys other = (UniqueKeys) obj;
	return Objects.equals(other.getUniqueKeys(), uniqueKeysList);
    }

    @Override
    public int hashCode()
    {
	return Objects.hash(uniqueKeysList);
    }

    @Override
    public String toString()
    {
	return "\nUniqueKeys:\n" + ukNameVsUniqueKey.toString() + "\n";
    }

    @XmlRootElement(name = "unique-key")
    public static class UniqueKey
    {
	@XmlAttribute(name = "uk-name")
	private String keyName;
	@XmlElement(name = "key-column")
	private List<String> ukColumns;

	public String getUkName()
	{
	    return keyName;
	}

	public void setUkName(String keyName)
	{
	    this.keyName = keyName;
	}

	public List<String> getKeyColumns()
	{
	    return ukColumns;
	}

	public void setKeyColumns(List<String> keyColumns)
	{
	    this.ukColumns = keyColumns;
	}

	@Override
	public String toString()
	{
	    return "\nukName: " + keyName + "\n\tkeyColumns:" + ukColumns.toString() + "\n";
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

	    UniqueKey other = (UniqueKey) obj;

	    return Objects.equals(ukColumns, other.getKeyColumns()) && Objects.equals(other.getUkName(), keyName);
	}

	@Override
	public int hashCode()
	{
	    return Objects.hash(ukColumns, keyName);
	}
    }
}
