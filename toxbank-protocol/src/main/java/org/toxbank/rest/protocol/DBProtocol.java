package org.toxbank.rest.protocol;

import net.toxbank.client.resource.Protocol;

public class DBProtocol extends Protocol {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6632168193661223228L;
	protected int ID;

	public DBProtocol() {
		
	}
	
	
	public DBProtocol(int id, int version) {
		setID(id);
		setVersion(version);
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}

	@Override
	public String toString() {
		return String.format("<a href='%s'>%s</a>",getResourceURL(),getTitle()==null?getResourceURL():getTitle());
	}
	
}
