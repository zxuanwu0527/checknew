package com.szkingdom;


import java.util.HashMap;
import java.util.Map;


public final class GlobalDBErrorMapping
{
	public static final GlobalDBErrorMapping S_DBERRORMAPPINGINSTANCE = new GlobalDBErrorMapping();
	
	private final int ERRORCODE_204 = -204;

	private final int ERRORCODE_433 = -433;

	private final int ERRORCODE_407 = -407;

	private final int ERRORCODE_798 = -798;

	private final int ERRORCODE_803 = -803;
	
	private Map MEANINGS = new HashMap();
	
	private GlobalDBErrorMapping()
	{
		MEANINGS.put(new Integer(ERRORCODE_204), "您操作的表名是一个未定义的名称");
		MEANINGS.put(new Integer(ERRORCODE_433), "SQL0433N值 <值> 太长");
		MEANINGS.put(new Integer(ERRORCODE_407), "不允许对 NOT NULL 列 <名称> 赋予 NULL 值");
		MEANINGS.put(new Integer(ERRORCODE_798), "不能为定义为 GENERATED ALWAYS 的列 <列名> 指定值");
		MEANINGS.put(new Integer(ERRORCODE_803), "!!表中存在相同数据!!");
	}
	
	public String getErrorMeaningByErrorCode(int errorCode)
	{
		String meaning = "";
		
		if (MEANINGS.get(new Integer(errorCode)) == null)
		{
			meaning = "错误号:" + errorCode + " 没有对应的解释";
		}
		else
		{
			meaning = (String) MEANINGS.get(new Integer(errorCode));
		}
		
		return meaning;
	}
}
