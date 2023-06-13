package com.szkingdom.a;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.szkingdom.LogUtils;






public class XMLUtil
{
	/**
	 * ��documentת�����ַ���
	 * @param document
	 * @return
	 */
	public static String doc2String(Document document)
	{
		String s = "";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputFormat format = new OutputFormat("", true, "GB2312");
		XMLWriter writer;
		
		try
		{
			writer = new XMLWriter(out, format);
			writer.write(document);
			s = out.toString("GB2312");
		}
		catch (UnsupportedEncodingException e)
		{
			LogUtils.debugStackTrace(e.getStackTrace());
		} 
		catch (IOException e)
		{
			LogUtils.debugStackTrace(e.getStackTrace());
		}
		
		return s;
	}

	/**
	 * ����һ���ļ� 
	 * @param filename
	 * @return 
	 */
	public static Document loadFile(File filename)
	{
		Document document = null;
		try
		{
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(filename);
			return document;
		}
		catch (Exception e)
		{
			LogUtils.debugStackTrace(e.getStackTrace());
			return null;
		}
	}
	public static Document loadFile(String filename)
	{
		Document document = null;
		try
		{
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(filename);
			return document;
		}
		catch (Exception e)
		{
			LogUtils.debugStackTrace(e.getStackTrace());
			return null;
		}
	}
	
	/**
	 * ��һ���������м���xml����
	 * @param is
	 * @return Document
	 */
	public static Document loadStream(InputStream is)
	{
		Document document = null;
		SAXReader saxReader = new SAXReader();
		
		try
		{
			saxReader.setEncoding("utf-8");//����xml���ݵ�ʱ�����һ��Ҫ����,����������������
			document = saxReader.read(is);
			return document;
		}
		catch (DocumentException e)
		{
			LogUtils.debugStackTrace(e.getStackTrace());
			return null;
		}
	}
	
	/**
	 * ��һ���ַ�������һ��document
	 * @param str
	 * @return Document
	 */
	public static Document loadString(String str)
	{
		Document document = null;
		SAXReader saxReader = new SAXReader();
		
		try
		{
			saxReader.setEncoding("utf-8");//����xml���ݵ�ʱ�����һ��Ҫ����,����������������
			document = saxReader.read(new ByteArrayInputStream(str.getBytes("utf-8")));
			return document;
		}
		catch (DocumentException e)
		{
			LogUtils.debugStackTrace(e.getStackTrace());
		}
		catch (UnsupportedEncodingException e)
		{
			LogUtils.debugStackTrace(e.getStackTrace());
		}
		
		return null;
	}
}