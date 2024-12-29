package com.ss.wealthcare.schema.builder;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.util.dd.DDUtil;
import com.ss.wealthcare.util.file.DirectoryUtil;
import com.ss.wealthcare.util.file.FilesUtil;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class SchemaBuilder
{

    public static final String SCHEMA_DIR = DirectoryUtil.RESOURCES_DIR + DirectoryUtil.SCHEMA + DirectoryUtil.XML;
    public static final Logger LOGGER = Logger.getLogger(SchemaBuilder.class.getName());

    public static void buildSchema() throws Exception
    {

	try
	{
	    JAXBContext context = JAXBContext.newInstance(Table.class);

	    Unmarshaller unmarsheller = context.createUnmarshaller();

	    File xmlSchema = new File(SCHEMA_DIR);

	    List<File> schemaFiles = FilesUtil.getFiles(xmlSchema);

	    for (File schemaFile : schemaFiles)
	    {
		Table table = (Table) unmarsheller.unmarshal(schemaFile);
		if (!DDUtil.isNull(table.getForeignKey()))
		{
		    table.getForeignKey().loadMap();
		}

		DDUtil.xmlParser(table);
	    }
	}

	catch (JAXBException e)
	{
	    LOGGER.log(Level.SEVERE, "Exception occurred while building schema", e);
	}
    }

    public static void main(String[] args) throws Exception
    {
	buildSchema();
    }

}
