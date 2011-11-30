package org.toxbank.rest.db;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.db.convertors.QueryHTMLReporter;

import org.restlet.Request;

public class DBHtmlReporter extends QueryHTMLReporter<DBVersion,DBVersionQuery> {
	protected boolean create = false;
	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	protected Request request;
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -5343958374065342370L;

	public DBHtmlReporter(Request baseRef) {
		super(baseRef,false,null);

	}
	
	@Override
	public void open() throws DbAmbitException {
	}

	@Override
	protected QueryURIReporter createURIReporter(Request request,
			ResourceDoc doc) {
		return new QueryURIReporter(request,null) {
			@Override
			public String getURI(String ref, Object item) {
				return "";
			}
		};
	}
	public Object processItem(DBVersion item) throws AmbitException {
		try {
			if (isCreate()) {
				output.write("<form method='post' action='?method=post'>");
				output.write(String.format("<h4>The database %s does not exist. Please use the form below to create tables in database.</h4>",item.getDbname()));
				output.write(String.format("<h5>First create the database via MySQL console.</h5>",item.getDbname()));
			}
			getOutput().write("<table>");
			

			if (isCreate()) {
				
				getOutput().write(String.format("<tr><th>DB name</th><td><input type='text' size='40' name='dbname' value='%s' title='The database name has to match with the \"Database\" property, defined in {servlet-home}/WEB-INF/classes/ambit2/rest/config/ambit.pref file'></td></tr>",item.getDbname()));
				getOutput().write(String.format("<tr><th>Admin user name</th><td title='existing MySQL user with sufficient privileges to create a database'><input type='text' size='40' name='user' value='%s'></td></tr>",""));
				getOutput().write(String.format("<tr><th>Password</th><td title='MySQL user password'><input type='password' size='40' name='pass' value='%s'></td></tr>",""));
				getOutput().write("<tr><td></td><td><input align='bottom' type=\"submit\" value=\"Create database\"></td></tr>");
			} else {
				getOutput().write(String.format("<tr><th>DB name</th><td>%s</td></tr>",item.getDbname()));
				getOutput().write(String.format("<tr><th>Version</th><td>%d.%d</td></tr>",item.getMajor(),item.getMinor()));
				getOutput().write(String.format("<tr><th>Created</th><td>%s</td></tr>",item.getCreated()));
				getOutput().write(String.format("<tr><th>Note</th><td>%s</td></tr>",item.getComments()));
			}
			getOutput().write("</table>");
			if (isCreate())
				output.write("</form>");
			return item;
	} catch (Exception x) { x.printStackTrace(); return null;}
	}
	
}