package org.toxbank.rest.groups.resource;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.toxbank.rest.groups.IDBGroup;

/**
 * Generates URI for {@link ReferenceResource}
 * @author nina
 *
 * @param <Q>
 */
public class GroupQueryURIReporter <Q extends IQueryRetrieval<IDBGroup>> extends QueryURIReporter<IDBGroup, Q> {
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
	public GroupQueryURIReporter(Request baseRef,String suffix) {
		super(baseRef,null);	
		this.suffix = suffix;
	}
	public GroupQueryURIReporter(Request baseRef) {
		this(baseRef,"");
	}
	public GroupQueryURIReporter() {
		this(null);
	}	

	@Override
	public String getURI(String ref, IDBGroup item) {

		return String.format("%s/%s/G%d%s",ref,item.getGroupType().name().toLowerCase(),item.getID(),suffix);
	}

}
