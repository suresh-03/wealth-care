package com.ss.wealthcare.schema.builder;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

public class Column {
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
	@XmlAttribute(name = "old_name")
	String oldName;

	public String getName() {
		return name;
	}

	public String getNullable() {
		return nullable;
	}

	public String getAutoIncrement() {
		return autoIncrement;
	}

	public String getMaxSize() {
		return maxSize;
	}

	public String getDataType() {
		return dataType;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public String getOldName() {
		return oldName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public void setAutoIncrement(String autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

}
