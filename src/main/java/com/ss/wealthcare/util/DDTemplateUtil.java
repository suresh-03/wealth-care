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
    public static final String TEMPLATE_DIR = DirectoryUtil.JAVA_DIR + DirectoryUtil.WEALTHCARE_PACKAGE
	    + DirectoryUtil.SCHEMA + DirectoryUtil.TEMPLATE;

    public static final String CLASS_TEMPLATE = "public final class ";
    public static final String PACKAGE_TEMPLATE = "package com.ss.wealthcare.schema.template;";
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
	String fileName = table.getName().toUpperCase() + ".java";
	File directory = new File(TEMPLATE_DIR);

	if (directory.isDirectory())
	{
	    if (directory.mkdir())
	    {
		LOGGER.log(Level.INFO, "Directory {0} created", directory);
	    }
	    else
	    {
		LOGGER.log(Level.INFO, "Directory {0} is already exists", directory);

	    }
	}
	else
	{
	    LOGGER.log(Level.INFO, "{0} is not a Directory", directory);
	}
	File file = new File(TEMPLATE_DIR + fileName);

	try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
	{
	    if (file.isFile())
	    {
		if (file.createNewFile())
		{
		    LOGGER.log(Level.INFO, "File {0} is created", file);
		}
		else
		{
		    LOGGER.log(Level.INFO, "File {0} is already exists", file);

		}
	    }
	    else
	    {
		LOGGER.log(Level.INFO, "{0} is not a File", file);

	    }
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
	final String CLASS = PACKAGE_TEMPLATE + NEWLINE + NEWLINE + qualifiedClassName + SPACE + NEWLINE + OPEN_CURLY
		+ NEWLINE;

	final String CONSTRUCTOR = TAB + "private " + className + "()" + NEWLINE + TAB + OPEN_CURLY + NEWLINE + TAB
		+ CLOSE_CURLY + NEWLINE + NEWLINE;

	writer.write(CLASS);
	writer.write(CONSTRUCTOR);

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
