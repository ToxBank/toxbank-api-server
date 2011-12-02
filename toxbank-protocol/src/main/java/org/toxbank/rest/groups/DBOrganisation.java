package org.toxbank.rest.groups;

import java.util.HashMap;
import java.util.Map;

import org.restlet.routing.Template;
import org.toxbank.resource.Resources;
import org.toxbank.rest.FileResource;

import net.toxbank.client.resource.Organisation;

public class DBOrganisation extends Organisation implements IDBGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6081839402557578567L;
	protected GroupType groupType = GroupType.ORGANISATION;
	protected int ID;
	
	public DBOrganisation() {
		this((Integer)null);
	}
	public DBOrganisation(Integer id) {
		super();
		if (id!=null) setID(id);
	}
	public DBOrganisation(Organisation p) {
		setTitle(p.getTitle());
		setGroupName(p.getGroupName());
		setResourceURL(p.getResourceURL());
		this.ID = -1;
	}
	@Override
	public GroupType getGroupType() {
		return groupType;
	}
	@Override
	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
		
	}
	@Override
	public int getID() {
		return ID;
	}
	@Override
	public void setID(int iD) {
		this.ID = iD;
		
	}
	
	public int parseURI(String baseReference)  {
		Template template = new Template(String.format("%s%s/{%s}",baseReference==null?"":baseReference,
				Resources.organisation,FileResource.resourceKey));
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			template.parse(getResourceURL().toString(), vars);
			return Integer.parseInt(vars.get(FileResource.resourceKey).toString().substring(1)); 
		} catch (Exception x) { return -1; }
	}
	
	@Override
	public String toString() {
		return String.format("<a href='%s' title='%s'>%s</a>",
					getResourceURL(),
					getTitle()==null?getResourceURL():getTitle(),
					getTitle()==null?getResourceURL():getTitle());
	}
}
