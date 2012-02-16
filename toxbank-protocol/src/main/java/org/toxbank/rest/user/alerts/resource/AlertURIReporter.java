package org.toxbank.rest.user.alerts.resource;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;
import net.toxbank.client.Resources;

import org.restlet.Request;
import org.toxbank.rest.user.alerts.db.DBAlert;

/**
 * Generates URI for {@link AlertDBResource}
 * @author nina
 *
 * @param <Q>
 */
public class AlertURIReporter <Q extends IQueryRetrieval<DBAlert>> extends QueryURIReporter<DBAlert, Q> {
	String suffix = "";
	

	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8868430033131766579L;
	public AlertURIReporter(Request baseRef,String suffix) {
		super(baseRef,null);	
		this.suffix = suffix;
	}
	public AlertURIReporter(Request baseRef) {
		this(baseRef,"");
	}
	public AlertURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, DBAlert item) {

		return String.format("%s%s/A%d%s",ref,Resources.alert,item.getID(),suffix);
	}

}
