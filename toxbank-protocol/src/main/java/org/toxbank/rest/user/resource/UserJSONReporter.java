package org.toxbank.rest.user.resource;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;
import net.toxbank.client.resource.Organisation;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;
import org.toxbank.rest.groups.resource.GroupQueryURIReporter;
import org.toxbank.rest.user.DBUser;

public class UserJSONReporter <Q extends IQueryRetrieval<DBUser>>  extends QueryReporter<DBUser,Q,Writer>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4566136103208284105L;
	protected String comma = null;
	protected Request request;
	protected QueryURIReporter uriReporter;
	protected GroupQueryURIReporter groupURIReporter ;
	
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	protected Reference baseReference;
	public Reference getBaseReference() {
		return baseReference;
	}

	public UserJSONReporter(Request request) {
		this.baseReference = (request==null?null:request.getRootRef());
		setRequest(request);
		uriReporter = new UserURIReporter(request,"");
	}	


	private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"id\": %s,\n\t\"username\": \"%s\",\n\t\"title\": \"%s\",\n\t\"firstname\": \"%s\",\n\t\"lastname\": \"%s\",\n\t\"email\": \"%s\",\n\t\"homepage\": \"%s\",\n\t\"organisation\": [\n\t\t%s\n\t]\n}";
	private static String formatGroup = "{\n\t\t\"uri\":\"%s\",\n\t\t\"title\": \"%s\"\n\t\t}";
	//output.write("Title,First name,Last name,user name,email,Keywords,Reviewer\n");

	@Override
	public Object processItem(DBUser user) throws AmbitException {
		try {
			if (comma!=null) getOutput().write(comma);
			
			StringBuilder group = null;
			if (user.getOrganisations()!=null)
				for (Organisation org : user.getOrganisations()) {
					if (group == null) group = new StringBuilder();
					else group.append(",");
					group.append(String.format(formatGroup,
							groupURIReporter.getURI(org),
							org.getTitle()
							));
				}

			
			String uri = user.getID()>0?uriReporter.getURI(user):"";
			
			getOutput().write(String.format(format,
					uri,
					(user.getID()>0)?String.format("\"U%s\"",user.getID()):null,
					user.getUserName()==null?"":user.getUserName(),
					user.getTitle()==null?"":user.getTitle(),
					user.getFirstname()==null?"":user.getFirstname(),
					user.getLastname()==null?"":user.getLastname(),
					user.getEmail()==null?"":user.getEmail(),
					user.getHomepage()==null?"":user.getHomepage(),
					group==null?"":group.toString()
					));
			comma = ",";
		} catch (IOException x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}	
	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
	};
	@Override
	public void header(Writer output, Q query) {
		try {
			output.write("{\"user\": [");
		} catch (Exception x) {}
		
	};
	
	public void open() throws DbAmbitException {
		
	}
	@Override
	public void close() throws Exception {
		setRequest(null);
		super.close();
	}
	@Override
	public String getFileExtension() {
		return null;
	}
}