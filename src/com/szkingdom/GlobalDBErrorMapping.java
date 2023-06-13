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
		MEANINGS.put(new Integer(ERRORCODE_204), "�������ı�����һ��δ���������");
		MEANINGS.put(new Integer(ERRORCODE_433), "SQL0433Nֵ <ֵ> ̫��");
		MEANINGS.put(new Integer(ERRORCODE_407), "������� NOT NULL �� <����> ���� NULL ֵ");
		MEANINGS.put(new Integer(ERRORCODE_798), "����Ϊ����Ϊ GENERATED ALWAYS ���� <����> ָ��ֵ");
		MEANINGS.put(new Integer(ERRORCODE_803), "!!���д�����ͬ����!!");
	}
	
	public String getErrorMeaningByErrorCode(int errorCode)
	{
		String meaning = "";
		
		if (MEANINGS.get(new Integer(errorCode)) == null)
		{
			meaning = "�����:" + errorCode + " û�ж�Ӧ�Ľ���";
		}
		else
		{
			meaning = (String) MEANINGS.get(new Integer(errorCode));
		}
		
		return meaning;
	}
}
