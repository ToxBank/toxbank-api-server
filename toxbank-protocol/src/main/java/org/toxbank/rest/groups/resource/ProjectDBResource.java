package org.toxbank.rest.groups.resource;

import org.toxbank.resource.Resources;
import org.toxbank.rest.groups.DBProject;
import org.toxbank.rest.groups.GroupType;
import org.toxbank.rest.groups.db.ReadGroup;
import org.toxbank.rest.groups.db.ReadProject;

public class ProjectDBResource extends GroupDBResource<DBProject> {

	@Override
	public ReadGroup<DBProject> createGroupQuery(Integer key, String search) {
		DBProject p = new DBProject();
		if (key!=null) p.setID(key);
		ReadProject q = new ReadProject(p);
		return q;
	}
	@Override
	public String getGroupBackLink() {
		return  Resources.project;
	}
	@Override
	public String getGroupTitle() {
		return GroupType.PROJECT.toString();
	}
}
