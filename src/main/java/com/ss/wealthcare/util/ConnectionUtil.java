package com.ss.wealthcare.util;

import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionUtil
{
    private static final Map<String, Object> MYSQL_INFO = YamlParserUtil
	    .loadYamlFile(DirectoryUtil.CONF_DIR + DirectoryUtil.MYSQL + "conf.yaml");

    private static HikariDataSource dataSource;

    private static final int MAX_POOL_SIZE = 10;

    private static final Logger LOGGER = Logger.getLogger(ConnectionUtil.class.getName());

    static
    {
	String host = (String) MYSQL_INFO.get("host");
	int port = (int) MYSQL_INFO.get("port");
	String database = (String) MYSQL_INFO.get("database");
	String username = (String) MYSQL_INFO.get("username");
	String password = (String) MYSQL_INFO.get("password");

	String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

	HikariConfig config = new HikariConfig();

	config.setJdbcUrl(url);
	config.setUsername(username);
	config.setPassword(password);
	config.setMaximumPoolSize(MAX_POOL_SIZE);

	dataSource = new HikariDataSource(config);
	LOGGER.log(Level.INFO, "Hikari Datasource created with Max Pool Size: {0}", MAX_POOL_SIZE);

    }

    public static Connection getConnection() throws Exception
    {
	return dataSource.getConnection();
    }
}
