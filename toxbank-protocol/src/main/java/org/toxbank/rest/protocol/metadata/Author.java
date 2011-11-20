package org.toxbank.rest.protocol.metadata;

import org.toxbank.resource.IAuthor;

public class Author implements IAuthor {
	public String name;
	public Author(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return name;
	}
	
}
