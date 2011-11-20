package org.toxbank.rest.protocol.metadata;

import org.toxbank.resource.IProject;

public class Project implements IProject {
	protected String name;
	public Project(String name) {
		this.name = name;
	}
	@Override
	public String toString() {

		return name;
	}
}
