package org.toxbank.rest.protocol.metadata;

import org.toxbank.resource.IUser;


public class Author implements IUser {
	public String name;
	public Author(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	
}
