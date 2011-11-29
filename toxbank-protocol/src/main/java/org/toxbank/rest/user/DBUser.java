package org.toxbank.rest.user;

import java.util.HashMap;
import java.util.Map;

import org.restlet.routing.Template;
import org.toxbank.resource.Resources;
import org.toxbank.rest.FileResource;

import net.toxbank.client.resource.Project;
import net.toxbank.client.resource.User;

public class DBUser extends User {
	public enum fields {
		iduser,
		username,
		title,
		firstname,
		lastname,
		institute,
		weblog,
		homepage		
	}
	protected int id=-1;
	public int getID() {
		return id;
	}
	public void setID(int id) {
		this.id = id;
	}
	public DBUser(int id) {
		this.id = id;
	}
	public DBUser() {
		this.id = -1;
	}
	public DBUser(User p) {
		setTitle(p.getTitle());
		setUserName(p.getUserName());
		setFirstname(p.getFirstname());
		setLastname(p.getLastname());
		setHomepage(p.getHomepage());
		setInstitute(p.getInstitute());
		setSeuratProject(p.getSeuratProject());
		setWeblog(p.getWeblog());
		setResourceURL(p.getResourceURL());
		this.id = -1;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3789186431450997758L;

	/**
	 * Parses its URI and generates ID
	 * @return
	 */
	public int parseURI(String baseReference)  {
		Template template = new Template(String.format("%s%s/{%s}",baseReference==null?"":baseReference,
				Resources.user,FileResource.resourceKey));
		Map<String, Object> vars = new HashMap<String, Object>();
		try {
			template.parse(getResourceURL().toString(), vars);
			return Integer.parseInt(vars.get(FileResource.resourceKey).toString().substring(1)); 
		} catch (Exception x) { return -1; }
	}
}
