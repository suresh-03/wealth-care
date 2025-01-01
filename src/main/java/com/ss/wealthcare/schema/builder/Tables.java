package com.ss.wealthcare.schema.builder;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tables")
public class Tables
{
    @XmlElement(name = "table")
    private List<Table> schemaTables;

    public List<Table> getTables()
    {
	return schemaTables;
    }

    public void setTables(List<Table> tables)
    {
	this.schemaTables = tables;
    }

    @Override
    public String toString()
    {
	return schemaTables.toString();
    }
}
