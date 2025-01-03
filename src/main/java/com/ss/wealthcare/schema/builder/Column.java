package com.ss.wealthcare.schema.builder;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "column")
@XmlAccessorType(XmlAccessType.FIELD)
public class Column
{
    @XmlAttribute(name = "name")
    private String name;
    @XmlElement(name = "nullable")
    private String nullable;
    @XmlElement(name = "auto-increment")
    private String autoIncrement;
    @XmlElement(name = "max-size")
    private String maxSize;
    @XmlElement(name = "data-type")
    private String dataType;
    @XmlAttribute(name = "old-name")
    private String oldName;
    @XmlElement(name = "default")
    private String defaultValue;

    private Column typeCast(Object obj)
    {
	return (Column) obj;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }

    public boolean isNameEquals(Object obj)
    {
	return Objects.equals(typeCast(obj).getName(), name);
    }

    public String getNullable()
    {
	return nullable;
    }

    public void setNullable(String nullable)
    {
	this.nullable = nullable;
    }

    public boolean isNullableEquals(Object obj)
    {
	return Objects.equals(typeCast(obj).getNullable(), nullable);
    }

    public String getAutoIncrement()
    {
	return autoIncrement;
    }

    public void setAutoIncrement(String autoIncrement)
    {
	this.autoIncrement = autoIncrement;
    }

    public boolean isAutoIncrementEquals(Object obj)
    {
	return Objects.equals(typeCast(obj).getAutoIncrement(), autoIncrement);
    }

    public String getMaxSize()
    {
	return maxSize;
    }

    public void setMaxSize(String maxSize)
    {
	this.maxSize = maxSize;
    }

    public boolean isMaxSizeEquals(Object obj)
    {
	return Objects.equals(typeCast(obj).getMaxSize(), maxSize);
    }

    public String getDataType()
    {
	return dataType;
    }

    public void setDataType(String dataType)
    {
	this.dataType = dataType;
    }

    public boolean isDataTypeEquals(Object obj)
    {
	return Objects.equals(typeCast(obj).getDataType(), dataType);
    }

    public String getOldName()
    {
	return oldName;
    }

    public void setOldName(String oldName)
    {
	this.oldName = oldName;
    }

    public boolean isOldNameEquals(Object obj)
    {
	return Objects.equals(typeCast(obj).getName(), oldName);
    }

    public String getDefaultValue()
    {
	return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
	this.defaultValue = defaultValue;
    }

    public boolean isDefaultValueEquals(Object obj)
    {
	return Objects.equals(typeCast(obj).getDefaultValue(), defaultValue);
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

	return isNameEquals(obj) && isNullableEquals(obj) && isAutoIncrementEquals(obj) && isMaxSizeEquals(obj)
		&& isDataTypeEquals(obj) && isDefaultValueEquals(obj);
    }

    @Override
    public int hashCode()
    {
	return Objects.hash(name, nullable, autoIncrement, maxSize, dataType, defaultValue);
    }

    @Override
    public String toString()
    {
	return "\ncolumnName: " + name + "\nisNullable: " + nullable + "\nisAutoIncrement: " + autoIncrement
		+ "\nmaxSize: " + maxSize + "\ndataType: " + dataType + "\noldName: " + oldName + "\ndefault: "
		+ defaultValue;
    }

}
