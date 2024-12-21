package com.ss.wealthcare.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ss.wealthcare.util.dd.DDUtil;

public class FilesUtil
{

    private FilesUtil()
    {
	DDUtil.throwUOE();

    }

    public static List<File> getFiles(File directory)
    {
	List<File> listOfFiles = new ArrayList<>();

	if (directory.isDirectory())
	{
	    File[] files = directory.listFiles();
	    if (files != null)
	    {
		for (File file : files)
		{
		    if (file.isFile())
		    {
			listOfFiles.add(file);
		    }
		    else if (file.isDirectory())
		    {
			listOfFiles.addAll(getFiles(file));
		    }
		}
	    }
	}
	return listOfFiles;
    }
}
