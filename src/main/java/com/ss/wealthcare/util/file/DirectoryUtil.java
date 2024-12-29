package com.ss.wealthcare.util.file;

import com.ss.wealthcare.util.dd.DDUtil;

public class DirectoryUtil
{

    private DirectoryUtil()
    {
	DDUtil.throwUOE();

    }

    // Directories
    public static final String SRC = "src/";
    public static final String MAIN = "main/";
    public static final String JAVA = "java/";
    public static final String RESOURCES = "resources/";
    public static final String SCHEMA = "schema/";
    public static final String MYSQL = "mysql/";
    public static final String TEMPLATE = "template/";
    public static final String XML = "xml/";
    public static final String CONFIG = "config/";
    public static final String YAML = "yaml/";
    public static final String WEALTHCARE_PACKAGE = "com/ss/wealthcare/";

    // Common Directories
    public static final String MAIN_DIR = SRC + MAIN;
    public static final String JAVA_DIR = MAIN_DIR + JAVA;
    public static final String RESOURCES_DIR = MAIN_DIR + RESOURCES;
    public static final String CONF_DIR = MAIN_DIR + RESOURCES + CONFIG;

}
