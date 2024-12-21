package com.ss.wealthcare.util.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

import com.ss.wealthcare.util.dd.DDUtil;

public class YamlParserUtil
{
    private static final Logger LOGGER = Logger.getLogger(YamlParserUtil.class.getName());

    private YamlParserUtil()
    {
	DDUtil.throwUOE();

    }

    public static Map<String, Object> loadYamlFile(String filename)
    {
	Yaml yaml = new Yaml();
	try (InputStream inputStream = new FileInputStream(filename))
	{
	    return Collections.unmodifiableMap(yaml.load(inputStream));
	}
	catch (IOException e)
	{
	    LOGGER.log(Level.SEVERE, "Exception occured when connecting to DB", e);

	}
	return null;
    }
}
