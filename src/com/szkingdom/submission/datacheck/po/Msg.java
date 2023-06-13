package com.szkingdom.submission.datacheck.po;

import com.szkingdom.submission.datacheck.po.DataConstants.MsgType;

public class Msg {

	private MsgType type;
	private Object content;
	
	public final MsgType getType() {
		return type;
	}
	public final void setType(MsgType type) {
		this.type = type;
	}
	public final Object getContent() {
		return content;
	}
	public final void setContent(Object content) {
		this.content = content;
	}
}
