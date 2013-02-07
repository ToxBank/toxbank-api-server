package org.toxbank.rest.protocol.resource.db;

import java.io.Writer;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.toxbank.rest.groups.IDBGroup;
import org.toxbank.rest.groups.resource.GroupQueryURIReporter;
import org.toxbank.rest.protocol.DBProtocol;
import org.toxbank.rest.user.DBUser;
import org.toxbank.rest.user.resource.UserURIReporter;

public class ProtocolJSONReporter extends QueryReporter<DBProtocol, IQueryRetrieval<DBProtocol>,Writer>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3537339785122677311L;
	protected String comma = null;
	protected QueryURIReporter uriReporter;
	
	public QueryURIReporter getUriReporter() {
		return uriReporter;
	}

	protected GroupQueryURIReporter<IQueryRetrieval<IDBGroup>> groupReporter;
	protected UserURIReporter<IQueryRetrieval<DBUser>> userReporter;
	
	
	public ProtocolJSONReporter(Request request) {
		super();
		uriReporter = new ProtocolQueryURIReporter<IQueryRetrieval<DBProtocol>>(request);
		/*
		groupReporter = new GroupQueryURIReporter<IQueryRetrieval<IDBGroup>>(request);
		userReporter = new UserURIReporter<IQueryRetrieval<DBUser>>(request);
		*/
	}
	
	@Override
	public void header(Writer output, IQueryRetrieval<DBProtocol> query) {
		try {
			output.write("{\"protocols\": [\n");
		} catch (Exception x) {}
		
	}
	
	private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"identifier\": \"%s\",\n\t\"title\": \"%s\",\n\t\"abstract\": \"%s\",\n\t\"updated\": %d\n\t}";
		
	@Override
	public Object processItem(DBProtocol item) throws AmbitException {
		try {
			if (comma!=null) getOutput().write(comma);
			String uri = uriReporter.getURI(item);
		
			/* uncomment if these are to be included in the json
			if ((item.getProject()!=null) && (item.getProject().getResourceURL()==null))
				item.getProject().setResourceURL(new URL(groupReporter.getURI((DBProject)item.getProject())));
			if ((item.getOrganisation()!=null) && (item.getOrganisation().getResourceURL()==null))
				item.getOrganisation().setResourceURL(new URL(groupReporter.getURI((DBOrganisation)item.getOrganisation())));
			if ((item.getOwner()!=null) && (item.getOwner().getResourceURL()==null))
				item.getOwner().setResourceURL(new URL(userReporter.getURI((DBUser)item.getOwner())));
			*/
			List<String> comments = item.getKeywords();

			getOutput().write(String.format(format,
					uri,
					item.getIdentifier(),
					jsonEscape(item.getTitle()),
					jsonEscape(item.getAbstract()),
					item.getTimeModified())
					);

			comma = ",";
		} catch (Exception x) {
			
		}
		return item;
	}
	
	@Override
	public void footer(Writer output, IQueryRetrieval<DBProtocol> query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
	}
	
	public static String jsonEscape(String value) {
        return value.replace("\\", "\\\\")
        .replace("/", "\\/")
        .replace("\b", "\\b")
        .replace("\f", "\\f")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        .replace("\"", "\\\"");
	}

}
