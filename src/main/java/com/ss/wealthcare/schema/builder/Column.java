package com.ss.wealthcare.schema.builder;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Column
{
    @XmlAttribute(name = "name")
    String name;
    @XmlElement(name = "nullable")
    String nullable;
    @XmlElement(name = "auto-increment")
    String autoIncrement;
    @XmlElement(name = "max-size")
    String maxSize;
    @XmlElement(name = "data-type")
    String dataType;
    @XmlElement(name = "primary-key")
    String primaryKey;

    public String getName()
    {
	return name;
    }

    public String getNullable()
    {
	return nullable;
    }

    public String getAutoIncrement()
    {
	return autoIncrement;
    }

    public String getMaxSize()
    {
	return maxSize;
    }

    public String getDataType()
    {
	return dataType;
    }

    public String getPrimaryKey()
    {
	return primaryKey;
    }
}
