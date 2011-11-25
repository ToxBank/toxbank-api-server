package org.toxbank.rest.groups.resource;

import org.toxbank.resource.Resources;
import org.toxbank.rest.groups.DBOrganisation;
import org.toxbank.rest.groups.GroupType;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.groups.db.ReadGroup;
import org.toxbank.rest.groups.db.ReadOrganisation;

public class OrganisationDBResource extends GroupDBResource<DBOrganisation> {

	@Override
	public ReadGroup<DBOrganisation> createGroupQuery(Integer key, String search) {
		DBOrganisation p = new DBOrganisation();
		if (key!=null) p.setID(key);
		ReadOrganisation q = new ReadOrganisation(p);
		return q;
	}
	@Override
	public String getGroupBackLink() {
		return  Resources.organisation;
	}
	@Override
	public String getGroupTitle() {
		return GroupType.ORGANISATION.toString();
	}
}
