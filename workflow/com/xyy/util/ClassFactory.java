package com.xyy.util;

public class ClassFactory {

    public static Object CreateObject(String className)
    {
        try
        {
        	@SuppressWarnings("rawtypes")
			Class clazz = Class.forName(className);
        	Object obj = clazz.newInstance();
        	return obj;

        }
        catch(Exception e)
        {
            return null;
        }
    }
}
