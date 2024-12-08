package com.ss.wealthcare.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ss.wealthcare.schema.builder.Column;
import com.ss.wealthcare.schema.builder.Table;

public class DDTemplateUtil
{
    public static final String TEMPLATE_DIR = DirectoryUtil.RESOURCES_DIR + DirectoryUtil.SCHEMA
	    + DirectoryUtil.TEMPLATE;

    public static final String CLASS_TEMPLATE = "public static final class ";
    public static final String PACKAGE_TEMPLATE = "package com.ss.wealthcare.util.template";
    public static final char OPEN_CURLY = '{';
    public static final char CLOSE_CURLY = '}';
    public static final char SEMICOLON = ';';
    public static final char EQUAL = '=';
    public static final char NEWLINE = '\n';
    public static final char SPACE = ' ';
    public static final String TAB = "    ";
    public static final char DOUBLE_QUOTE = '\"';
    public static final String PROPERTY_TEMPLATE = "public static final String ";

    private static final Logger LOGGER = Logger.getLogger(DDTemplateUtil.class.getName());

    public static void createDDTemplate(Table table)
    {
	String fileName = table.getName();
	File file = new File(TEMPLATE_DIR + fileName + ".java");

	try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
	{
	    createDDTemplate(writer, table);
	}
	catch (Exception e)
	{
	    LOGGER.log(Level.SEVERE, "Exception occured creating file", e);
	}

    }

    private static void createDDTemplate(BufferedWriter writer, Table table) throws Exception
    {
	String className = table.getName().toUpperCase();
	String qualifiedClassName = CLASS_TEMPLATE + className;
	final String CLASS = PACKAGE_TEMPLATE + NEWLINE + NEWLINE + SEMICOLON + qualifiedClassName + SPACE + NEWLINE
		+ OPEN_CURLY + NEWLINE;

	writer.write(CLASS);

	List<Column> columns = table.getColumns();

	for (Column column : columns)
	{
	    String columnName = column.getName();
	    writer.write(TAB);
	    String propertyWithValue = PROPERTY_TEMPLATE + columnName + SPACE + EQUAL + SPACE + DOUBLE_QUOTE
		    + columnName + DOUBLE_QUOTE + SEMICOLON + NEWLINE;
	    writer.write(propertyWithValue);
	}

	writer.write(CLOSE_CURLY);

    }
}
