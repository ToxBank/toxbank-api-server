package org.toxbank.rest.groups;

import java.util.HashMap;
import java.util.Map;

import net.toxbank.client.Resources;
import net.toxbank.client.resource.Project;

import org.restlet.routing.Template;
import org.toxbank.rest.FileResource;

public class DBProject extends Project implements IDBGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4356100798611119822L;
	protected GroupType groupType = GroupType.PROJECT;
	public GroupType getGroupType() {
		return groupType;
	}
	public void setGroupType(GroupType groupType) {
		this.groupType = groupType;
	}
	protected int ID;

	public DBProject() {
		this((Integer)null);
	}
	public DBProject(Integer id) {
		if (id!=null) setID(id);
	}
	/**
	 * just copy it
	 * @param p
	 */
	public DBProject(Project p) {
		setTitle(p.getTitle());
		setGroupName(p.getGroupName());
		setResourceURL(p.getResourceURL());
		this.ID = -1;
	}
	
	@Override
	public int getID() {
		return ID;
	}
	@Override
	public void setID(int iD) {
		this.ID = iD;
		
	}
	@Override
	public String toString() {
		return String.format("<a href='%s' title='%s'>%s</a>",
					getResourceURL(),
					getTitle()==null?getResourceURL():getTitle(),
					getTitle()==null?getResourceURL():getTitle());
	}
	
	/**
	 * Parses its URI and generates ID
	 * @return
	 */
	public int parseURI(String baseReference)  {
		Template template = new Template(String.format("%s%s/{%s}",baseReference==null?"":baseReference,
				Resources.project,FileResource.resourceKey));
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			template.parse(getResourceURL().toString(), vars);
			return Integer.parseInt(vars.get(FileResource.resourceKey).toString().substring(1)); 
		} catch (Exception x) { return -1; }
	}
	
}
