package org.toxbank.rest.protocol;

import org.toxbank.resource.ITemplate;


public class DataTemplate implements ITemplate {
	String content;
	public DataTemplate(String content) {
		this.content = content==null?"":content;
	}
	@Override
	public String toString() {

		return content;
	}
}
