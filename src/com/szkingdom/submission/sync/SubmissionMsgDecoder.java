package com.szkingdom.submission.sync;

import static com.szkingdom.submission.sync.MsgUtils.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;



public class SubmissionMsgDecoder {

	private static Logger logger = Logger.getLogger(SubmissionMsgDecoder.class);
	
	public static Object decode(String msg){
		logger.debug("decode msg:["+msg+"]"); 
		if(StringUtils.isNotBlank(msg)){
			Object o = getMsgInstance(msg);
			return o;
		}
		return msg;
	}
	
	public static void main(String[] args){
		DOMConfigurator.configure("./mastloadconf/log4j.xml");
		
	}
}
